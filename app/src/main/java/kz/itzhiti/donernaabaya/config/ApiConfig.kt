package kz.itzhiti.donernaabaya.config

object ApiConfig {
    const val ORDER_SERVICE_BASE_URL = "http://10.0.2.2:8081/"
    const val DELIVERY_SERVICE_BASE_URL = "http://10.0.2.2:8082/"
    const val KEYCLOAK_BASE_URL = "http://10.0.2.2:8080/"

    const val KEYCLOAK_REALM = "doner-realm"
    const val KEYCLOAK_CLIENT_ID = "doner-client"
    const val KEYCLOAK_CLIENT_SECRET = "EtB97IKbN8e0KooVEejOuf1C4osBlJhE"

    const val COIN_EARN_PERCENTAGE = 0.05
    const val TIMEOUT_SECONDS = 30L

    // Database
    const val DATABASE_NAME = "doner_naabaya.db"

    // Preferences
    const val PREFS_AUTH_TOKENS = "auth_tokens"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"
}
