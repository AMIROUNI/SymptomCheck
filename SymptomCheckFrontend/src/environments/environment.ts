export const environment = {
  production: false,

  // URLs des microservices Admin
  userserviceAdminApiUrl: 'http://localhost:8082/api/admin',
  doctorserviceAdminApiUrl: 'http://localhost:8087/api/admin',
  appointmentAdminApiUrl: 'http://localhost:8089/api/admin',
  clinicAdminApiUrl: 'http://localhost:8085/api/admin',
  
  // URLs des microservices API
  userserviceApiUrl: 'http://localhost:8082/api/v1',
  doctorserviceApiUrl: 'http://localhost:8087/api/v1/doctor',
  appointmentApiUrl: 'http://localhost:8089/api/v1/appointments',
  clinicApiUrl: 'http://localhost:8085/api/v1',
  
  // ✅ AJOUT: Service de reviews
  reviewApiUrl: 'http://localhost:8078/api/v1/reviews',
  
  // Configuration Keycloak
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'symptomcheck-realm',
    clientId: 'your-angular-client'
  },
  
  // URL pour les uploads
  uploadsUrl: 'http://localhost:8082',
  
  // ✅ AJOUT: URL de base de l'API (optionnel)
  apiUrl: 'http://localhost:8085/api/v1'
};