import { KeycloakConfig } from 'keycloak-js';

const keycloakConfig: KeycloakConfig = {
  url: 'http://keycloack:8080',
  realm: 'symptomcheck-realm',
  clientId: 'angular-client'
};

export default keycloakConfig;
