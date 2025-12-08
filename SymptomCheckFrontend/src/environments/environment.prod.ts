export const environment = {
  production: false,

  // URLs des microservices Admin
  userserviceAdminApiUrl: 'http://userservice/api/admin',
  doctorserviceAdminApiUrl: 'http://doctorservice/api/admin',
  appointmentAdminApiUrl: 'http://appointmentservice/api/admin',
  clinicAdminApiUrl: 'http://clinicservice/api/admin',
  aiApiUrl: 'http://aiservice/api/v1/predict/' ,

  // URLs des microservices API
  userserviceApiUrl: 'http://userservice/api/v1',
  doctorserviceApiUrl: 'http://doctorservice/api/v1/doctor',
  appointmentApiUrl: 'http://appointmentservice/api/v1/appointments',
  clinicApiUrl: 'http://clinicservice/api/v1',

  //  AJOUT: Service de reviews
  reviewApiUrl: 'http://reviewservice/api/v1/reviews',

  // Configuration Keycloak
  keycloak: {
    url: 'http://keycloak',
    realm: 'symptomcheck-realm',
    clientId: 'your-angular-client'
  },

  // URL pour les uploads
  uploadsUrl: 'http://userservice',

  // ✅ AJOUT: URL de base de l'API (optionnel)
  apiUrl: 'http://clinicservice/api/v1'
};
