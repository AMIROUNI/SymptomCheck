import { KeycloakService } from "keycloak-angular";

export function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'symptomcheck-realm',
        clientId: 'angular-client',
      },
      initOptions: {
        onLoad: 'check-sso', // ✅ Check if user is already logged in
        checkLoginIframe: false,
        flow: 'standard'
      },
      // ✅ Automatically add "Authorization: Bearer <token>" to HTTP requests
      enableBearerInterceptor: true,

      // ✅ Exclude public endpoints
      bearerExcludedUrls: [
        '/assets',
        '/api/v1/users/register',
        '/api/v1/users/public',
        '/api/v1/auth',
        'http://localhost:8080'
      ],

      bearerPrefix: 'Bearer'
    })
    .then(authenticated => {
      console.log('Keycloak initialized. Authenticated:', authenticated);

      if (authenticated) {
        // Get and log user profile
        keycloak.loadUserProfile().then(profile => {
          console.log('✅ User profile:', profile);
        });

        // Get and log access token
        keycloak.getToken().then(token => {
          console.log('✅ Access token:', token);
        });
      } else {
        console.log('ℹ️ User is not authenticated');
      }

      return authenticated;
    })
    .catch(err => {
      console.error("❌ Keycloak initialization failed:", err);
      return false;
    });
}
