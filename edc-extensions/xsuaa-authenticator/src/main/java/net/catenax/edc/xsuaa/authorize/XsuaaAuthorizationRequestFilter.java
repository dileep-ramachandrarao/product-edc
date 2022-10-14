package net.catenax.edc.xsuaa.authorize;

import static jakarta.ws.rs.HttpMethod.OPTIONS;

import com.sap.cloud.security.token.SecurityContext;
import com.sap.cloud.security.token.Token;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.catenax.edc.xsuaa.authenticator.XsuaaBasedAuthenticationService;
import org.eclipse.dataspaceconnector.spi.exception.AuthenticationFailedException;
import org.eclipse.dataspaceconnector.spi.exception.NotAuthorizedException;

public class XsuaaAuthorizationRequestFilter implements ContainerRequestFilter {
  private final XsuaaBasedAuthenticationService authenticationService;

  public XsuaaAuthorizationRequestFilter(XsuaaBasedAuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    var headers = requestContext.getHeaders();

    // OPTIONS requests don't have credentials - do not authenticate
    if (!OPTIONS.equalsIgnoreCase(requestContext.getMethod())) {
      var isAuthenticated =
          authenticationService.isAuthenticated(
              headers.entrySet().stream()
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
      if (!isAuthenticated) {
        throw new AuthenticationFailedException();
      }
      // On successfull authentication, retrieve xsuaa token and the associated xsuaa scopes
      // attribute
      Token xsuaaToken = SecurityContext.getToken();
      if (xsuaaToken == null) {
        throw new NotAuthorizedException();
      }
      List<String> xsuaaScopes = (List<String>) xsuaaToken.getClaims().get("scope");

      // Check for authorization based on xsuaa scopes
      String pathName = requestContext.getUriInfo().getPath();
      String methodName = requestContext.getMethod();
      String concatenatedPathMethodNames = String.join(":", pathName, methodName);

      List<String> scopeMapping = XsuaaScopesMap.getScopes().get(concatenatedPathMethodNames);

      // compare xsuaaScopes with scopeMapping
      List<String> trimXsuaaScopes =
          xsuaaScopes.stream()
              .map(
                  scope -> {
                    String[] tempArray = scope.split("\\.");
                    return tempArray[tempArray.length - 1];
                  })
              .collect(Collectors.toList());

      // scopeMapping and trimXsuaaScopes size should be equal.
      Set<String> requiredScopePresent = new HashSet<>(trimXsuaaScopes);
      requiredScopePresent.retainAll(scopeMapping); // intersection of two lists

      // if no scopes are common in two lists then throw not authorized exception
      if (requiredScopePresent.size() != scopeMapping.size() || requiredScopePresent.size() == 0) {
        throw new NotAuthorizedException();
      }
    }
  }
}
