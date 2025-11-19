export const environment = {
  production: false,
  userserviceApiUrl : 'http://localhost:8082/api/v1',
  doctorserviceApiUrl : 'http://localhost:8087/api/v1/doctor',
  appointmentApiUrl : 'http://localhost:8089/api/v1/appointments',
  clincApiUrl : 'http://localhost:8085/api/v1',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'symptomcheck-realm',
    clientId: 'your-angular-client'
  },
  uploadsUrl: 'http://localhost:8082'
};
