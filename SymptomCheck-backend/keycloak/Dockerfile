# Utilise l'image Keycloak officielle
FROM quay.io/keycloak/keycloak:24.0.2

# Copie la configuration du realm
COPY realm-export.json /opt/keycloak/data/import/realm-export.json

# Déclare les variables d'environnement pour la DB et Keycloak
# Utilise des valeurs par défaut si elles ne sont pas fournies
ENV KC_DB=${KC_DB:-postgres} \
    KC_DB_HOST=${KC_DB_HOST:-localhost} \
    KC_DB_PORT=${KC_DB_PORT:-5432} \
    KC_DB_NAME=${KC_DB_NAME:-keycloak} \
    KC_DB_USERNAME=${KC_DB_USERNAME:-admin} \
    KC_DB_PASSWORD=${KC_DB_PASSWORD:-admin} \
    KC_IMPORT=/opt/keycloak/data/import/realm-export.json \
    KC_IMPORT_FORCE=true \
    KC_DB_URL=jdbc:postgresql://${KC_DB_HOST}:${KC_DB_PORT}/${KC_DB_NAME}

# Démarre Keycloak en mode dev et importe le realm
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
