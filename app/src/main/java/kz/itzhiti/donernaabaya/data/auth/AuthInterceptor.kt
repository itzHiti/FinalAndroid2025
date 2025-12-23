package kz.itzhiti.donernaabaya.data.auth

import android.content.Context
import kz.itzhiti.donernaabaya.config.ApiConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AuthInterceptor(private val appContext: Context) : Interceptor {
    private val tokenManager = TokenManager(appContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val requestWithToken = if (!accessToken.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            original
        }

        var response = chain.proceed(requestWithToken)

        if (response.code == 401) {
            // Закрываем предыдущий response, чтобы не текли сокеты
            response.close()

            synchronized(this) {
                val currentAccessToken = tokenManager.getAccessToken()
                // Если другой поток уже обновил токен — ретраим с ним
                if (!currentAccessToken.isNullOrBlank() && currentAccessToken != accessToken) {
                    val retry = requestWithToken.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $currentAccessToken")
                        .build()
                    return chain.proceed(retry)
                }

                val refreshToken = tokenManager.getRefreshToken()
                if (!refreshToken.isNullOrBlank()) {
                    try {
                        val newTokens = refreshTokenRequest(refreshToken)
                        tokenManager.saveTokens(newTokens.access_token, newTokens.refresh_token)

                        val retried = requestWithToken.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer ${newTokens.access_token}")
                            .build()
                        return chain.proceed(retried)
                    } catch (_: Exception) {
                        tokenManager.clear()
                    }
                } else {
                    tokenManager.clear()
                }
            }
        }

        return response
    }

    private fun refreshTokenRequest(refreshToken: String): TokenRefreshResponse {
        val client = OkHttpClient()
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val bodyString = "grant_type=refresh_token&client_id=${ApiConfig.KEYCLOAK_CLIENT_ID}&client_secret=${ApiConfig.KEYCLOAK_CLIENT_SECRET}&refresh_token=$refreshToken"
        val requestBody = bodyString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("${ApiConfig.KEYCLOAK_BASE_URL}realms/${ApiConfig.KEYCLOAK_REALM}/protocol/openid-connect/token")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw Exception("Token refresh failed: ${resp.code}")
            val json = resp.body?.string() ?: throw Exception("Empty token response")
            return Gson().fromJson(json, TokenRefreshResponse::class.java)
        }
    }
}

data class TokenRefreshResponse(
    val access_token: String,
    val refresh_token: String?,
    val token_type: String,
    val expires_in: Long
)
