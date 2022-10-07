package net.catenax.edc.xsuaa.authenticator;

import com.sap.cloud.security.client.HttpClientException;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.config.Environments;
import com.sap.cloud.security.mtls.SSLContextFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a {@link CloseableHttpClient} instance. Overwrite library's DefaultHttpClientFactory: -
 * with XSUAA configured Client ID and Client Secret - to avoid the warning <a
 * href="https://github.com/SAP/cloud-security-xsuaa-integration/blob/main/token-client/README.md#new-warning-in-productive-environment-provide-well-configured-httpclientfactory-service">...</a>
 */
public class CustomHttpClientFactory implements HttpClientFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpClientFactory.class);
  // reuse ssl connections
  final ConcurrentHashMap<String, CustomHttpClientFactory.SslConnection> sslConnectionPool =
      new ConcurrentHashMap<>();
  final Set<String> httpClientsCreated = Collections.synchronizedSet(new HashSet<>());
  static final int MAX_CONNECTIONS_PER_ROUTE = 4; // 2 is default
  static final int MAX_CONNECTIONS = 20;
  private static final int DEFAULT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(5);
  private final RequestConfig timeoutConfig;

  public CustomHttpClientFactory() {
    timeoutConfig =
        RequestConfig.custom()
            .setConnectTimeout(DEFAULT_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
            .setSocketTimeout(DEFAULT_TIMEOUT)
            .build();
  }

  @Override
  public CloseableHttpClient createClient(ClientIdentity clientIdentity)
      throws HttpClientException {

    // Initialize Client Credentials with the registered XSUAA client id and secret
    if (Objects.isNull(Environments.getCurrent().getXsuaaConfiguration())) {
      clientIdentity =
          new ClientCredentials(
              System.getenv("XSUAA_CLIENT_ID"), System.getenv("XSUAA_CLIENT_SECRET"));
    } else clientIdentity = Environments.getCurrent().getXsuaaConfiguration().getClientIdentity();

    String clientId = clientIdentity != null ? clientIdentity.getId() : null;
    httpClientsCreated.add(clientId);

    if (clientId != null && clientIdentity.isCertificateBased()) {
      ClientIdentity finalClientIdentity = clientIdentity;
      CustomHttpClientFactory.SslConnection connectionPool =
          sslConnectionPool.computeIfAbsent(
              clientId, s -> new CustomHttpClientFactory.SslConnection(finalClientIdentity));
      return HttpClients.custom()
          .setDefaultRequestConfig(timeoutConfig)
          .setConnectionManager(connectionPool.poolingConnectionManager)
          .setSSLContext(connectionPool.context)
          .setSSLSocketFactory(connectionPool.sslSocketFactory)
          .build();
    }
    return HttpClients.createDefault();
  }

  private static class SslConnection {
    SSLContext context;
    SSLConnectionSocketFactory sslSocketFactory;
    PoolingHttpClientConnectionManager poolingConnectionManager;

    public SslConnection(ClientIdentity clientIdentity) {
      try {
        this.context = SSLContextFactory.getInstance().create(clientIdentity);
      } catch (IOException | GeneralSecurityException e) {
        throw new HttpClientException(
            String.format(
                "Couldn't set up https client for service provider %s. %s.",
                clientIdentity.getId(), e.getLocalizedMessage()));
      }
      this.sslSocketFactory = new SSLConnectionSocketFactory(context);
      Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory>create()
              .register("http", PlainConnectionSocketFactory.getSocketFactory())
              .register("https", sslSocketFactory)
              .build();
      this.poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      this.poolingConnectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
      this.poolingConnectionManager.setMaxTotal(MAX_CONNECTIONS);
    }
  }
}
