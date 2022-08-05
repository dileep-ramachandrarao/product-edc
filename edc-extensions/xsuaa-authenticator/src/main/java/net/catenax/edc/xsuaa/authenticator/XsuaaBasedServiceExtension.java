package net.catenax.edc.xsuaa.authenticator;

import static java.lang.String.format;

import org.eclipse.dataspaceconnector.api.auth.AuthenticationService;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

@Provides(AuthenticationService.class)
public class XsuaaBasedServiceExtension implements ServiceExtension {

  @Override
  public void initialize(ServiceExtensionContext context) {

    context.getMonitor().info(format("API Authentication: using XSUAA Token"));

    var authService = new XsuaaBasedAuthenticationService(context.getMonitor());
    context.registerService(AuthenticationService.class, authService);
  }
}
