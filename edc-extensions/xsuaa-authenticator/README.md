# Data management API authentication using XSUAA Authentication Service

XSUAA authentication service rely on [OAuth 2.0](https://oauth.net/) protocol and OAuth 2.0 access tokens. Some platform which makes use of it are [SAP Cloud Platform](https://www.sap.com/products/cloud-platform.html), [SAP HANA XS Advanced](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/2.0.00/en-US), ...

This extension makes use of opensource [Java-security client library](https://github.com/SAP/cloud-security-xsuaa-integration/tree/main/java-security) to verify the incoming token on data management API.

This extension could also serve as an implementation template for other OAuth 2.0 services.

**Please note** The service configuration needs to be loaded as described [here](https://github.com/SAP/cloud-security-xsuaa-integration/tree/main/java-security#setup-step-1-load-the-service-configurations). 

For local testing, please provide the values for the following environment variables: `XSUAA_APP_ID`, `XSUAA_UAA_DOMAIN`, `XSUAA_AUTH_URL`, `XSUAA_CLIENT_ID`, `XSUAA_CLIENT_SECRET`

In Kubernetes environment, please configure as described [here](https://github.com/SAP/cloud-security-xsuaa-integration/tree/main/java-security#mega-service-configuration-in-kuberneteskyma-environment), so that the client library could load the service configuration values.