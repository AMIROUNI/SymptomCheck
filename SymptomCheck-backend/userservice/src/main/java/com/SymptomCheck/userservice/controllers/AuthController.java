package com.SymptomCheck.userservice.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs pour l'authentification des utilisateurs")
public class AuthController {
    /*

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login")
    @Operation(summary = "Connecter un utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Configuration Keycloak
            String keycloakUrl = "http://localhost:8080";
            String realm = "symptomcheck-realm";
            String clientId = "userservice";
            String clientSecret = "userservice-secret";

            // Préparer la requête pour obtenir le token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", loginRequest.getUsername());
            body.add("password", loginRequest.getPassword());
            body.add("grant_type", "password");
            body.add("scope", "openid");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // Appel à Keycloak
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Rafraîchit un token JWT expiré")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) {
        try {
            String keycloakUrl = "http://localhost:8080";
            String realm = "symptomcheck-realm";
            String clientId = "userservice";
            String clientSecret = "userservice-secret";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("grant_type", "refresh_token");
            body.add("refresh_token", refreshRequest.getRefreshToken());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token refresh failed"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Refresh failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnecter un utilisateur", description = "Invalide le token de l'utilisateur")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        try {
            String keycloakUrl = "http://localhost:8080";
            String realm = "symptomcheck-realm";
            String clientId = "userservice";
            String clientSecret = "userservice-secret";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", logoutRequest.getRefreshToken());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            String logoutUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, request, String.class);

            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }

    */
}