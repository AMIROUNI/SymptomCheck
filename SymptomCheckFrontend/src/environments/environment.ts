export const environment = {
  production: false,
  userserviceApiUrl : 'http://localhost:8082/api/v1',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'symptomcheck-realm',
    clientId: 'your-angular-client'
  },
  uploadsUrl: 'http://localhost:8082'
};
