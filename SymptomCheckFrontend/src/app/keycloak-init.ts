import { KeycloakService } from "keycloak-angular";
import { Router } from "@angular/router";

export function initializeKeycloak(keycloak: KeycloakService, router: Router) {
  return () =>
    keycloak
      .init({
        config: {
          url: 'http://localhost:8080',
          realm: 'symptomcheck-realm',
          clientId: 'angular-client',
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false,
          flow: 'standard',
        },
        enableBearerInterceptor: true,
        bearerExcludedUrls: [
          '/assets',
          '/api/v1/users/register',
          '/api/v1/users/public',
          '/api/v1/auth',
          'http://localhost:8080'
        ],
        bearerPrefix: 'Bearer',
      })
      .then(async (authenticated) => {
        console.log('Keycloak initialized. Authenticated:', authenticated);

        if (!authenticated) {
          console.log("ℹ️ User not authenticated");
          return false;
        }

    const roles = keycloak.getUserRoles(true);
    console.log("################################################################")
    console.log("🎭 Extracted roles:", roles);

        // REDIRECT BASED ON ROLE
        if (roles.includes("ADMIN")) {
          router.navigate(['/admin/dashboard']);
        }  else {
          router.navigate(['/']);
        }

        return true;
      })
      .catch(err => {
        console.error("❌ Keycloak initialization error:", err);
        return false;
      });
}
