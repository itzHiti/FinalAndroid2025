package kz.itzhiti.donernaabaya.data.repositories

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kz.itzhiti.donernaabaya.config.ApiConfig
import kz.itzhiti.donernaabaya.data.api.KeycloakService
import kz.itzhiti.donernaabaya.data.api.TokenResponse
import kz.itzhiti.donernaabaya.data.auth.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64
import java.util.concurrent.TimeUnit

class AuthRepository(private val context: Context) {
    private val keycloakBase = ApiConfig.KEYCLOAK_BASE_URL
    private val realm = ApiConfig.KEYCLOAK_REALM
    private val clientId = ApiConfig.KEYCLOAK_CLIENT_ID

    private val service: KeycloakService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(keycloakBase)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KeycloakService::class.java)
    }

    private val tokens = TokenManager(context)

    suspend fun login(username: String, password: String, clientSecret: String): TokenResponse {
        return try {
            val response = service.login(realm, clientId, clientSecret, username, password)

            // Декодировать JWT для получения userId
            val userId = extractUserIdFromToken(response.access_token)
            tokens.saveTokens(response.access_token, response.refresh_token, userId, username)
            response
        } catch (e: Exception) {
            throw AuthException("Ошибка входа: ${e.message}", e)
        }
    }

    suspend fun refresh(refreshToken: String): TokenResponse {
        return try {
            val response = service.refresh(realm, clientId, ApiConfig.KEYCLOAK_CLIENT_SECRET, refreshToken)
            tokens.saveTokens(response.access_token, response.refresh_token)
            response
        } catch (e: Exception) {
            throw AuthException("Ошибка обновления токена", e)
        }
    }

    fun logout() {
        tokens.clear()
    }

    fun isLoggedIn(): Boolean = tokens.isLoggedIn()

    fun getAccessToken(): String? = tokens.getAccessToken()

    fun getUsername(): String? = tokens.getUsername()

    fun getUserId(): String? = tokens.getUserId()

    private fun extractUserIdFromToken(token: String): String {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return "unknown"

            val decoded = String(Base64.getUrlDecoder().decode(parts[1]))
            val jsonObject = JsonParser.parseString(decoded) as JsonObject
            jsonObject.get("sub")?.asString ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}

class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)

