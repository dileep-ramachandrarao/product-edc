package net.catenax.edc.xsuaa.authenticator;

import com.sap.cloud.security.config.Environments;
import com.sap.cloud.security.config.OAuth2ServiceConfiguration;
import com.sap.cloud.security.config.OAuth2ServiceConfigurationBuilder;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.config.cf.CFConstants;
import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;
import com.sap.cloud.security.token.validation.CombiningValidator;
import com.sap.cloud.security.token.validation.ValidationResult;
import com.sap.cloud.security.token.validation.validators.JwtValidatorBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.dataspaceconnector.api.auth.AuthenticationService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

public class XsuaaBasedAuthenticationService implements AuthenticationService {

  private static final String BEARER_TOKEN_AUTH_HEADER_NAME = "Authorization";

  private final OAuth2ServiceConfiguration serviceConfig;
  // logging service
  private final Monitor monitor;

  public XsuaaBasedAuthenticationService(Monitor monitor) {
    this.monitor = monitor;

    // Load service configuration from VCAP_SERVICES in Cloud Foundry or from secrets in Kubernetes
    // environment
    if (Objects.isNull(Environments.getCurrent().getXsuaaConfiguration())) {
      monitor.debug(
          "Service Config from Kubernetes environment could not be loaded. Loading service configs from local env variables");
      // In case of null, load the service configuration using environment variables(LOCAL TESTING
      // ONLY)
      this.serviceConfig =
          OAuth2ServiceConfigurationBuilder.forService(Service.XSUAA)
              .withProperty(CFConstants.XSUAA.APP_ID, System.getenv("XSUAA_APP_ID"))
              .withProperty(CFConstants.XSUAA.UAA_DOMAIN, System.getenv("XSUAA_UAA_DOMAIN"))
              .withUrl(System.getenv("XSUAA_AUTH_URL"))
              .withClientId(System.getenv("XSUAA_CLIENT_ID"))
              .withClientSecret(System.getenv("XSUAA_CLIENT_SECRET"))
              .build();
    } else this.serviceConfig = Environments.getCurrent().getXsuaaConfiguration();
  }

  @Override
  public boolean isAuthenticated(Map<String, List<String>> headers) {
    Objects.requireNonNull(headers, "headers");

    return headers.keySet().stream()
        .filter(k -> k.equalsIgnoreCase(BEARER_TOKEN_AUTH_HEADER_NAME))
        .map(headers::get)
        .filter(list -> !list.isEmpty())
        .anyMatch(list -> list.stream().anyMatch(this::validateBearerToken));
  }

  private boolean validateBearerToken(Object authorizationHeader) {
    ValidationResult result;

    try {
      // Token Validators - supports tokens issued by xsuaa and ias
      Token token = Token.create(authorizationHeader.toString());
      CombiningValidator<Token> validators =
          JwtValidatorBuilder.getInstance(this.serviceConfig).build();
      result = validators.validate(token);

      if (result.isErroneous()) {
        this.monitor.severe("Invalid token" + result.getErrorDescription());
        return false;
      }
      // SecurityContext caches only successfully validated tokens within the same thread
      SecurityContext.setToken(token);
      this.monitor.info("Token validated successfully");
    } catch (Exception e) {
      this.monitor.severe("Token Invalid Exception: " + e.getMessage());
      e.printStackTrace();
      return false;
    }

    return result.isValid();
  }
}
