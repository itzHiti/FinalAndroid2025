package kz.itzhiti.donernaabaya.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kz.itzhiti.donernaabaya.config.ApiConfig

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        ApiConfig.PREFS_AUTH_TOKENS,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String?, userId: String? = null, username: String? = null) {
        prefs.edit().apply {
            putString(ApiConfig.KEY_ACCESS_TOKEN, accessToken)
            putString(ApiConfig.KEY_REFRESH_TOKEN, refreshToken)
            userId?.let { putString(ApiConfig.KEY_USER_ID, it) }
            username?.let { putString(ApiConfig.KEY_USERNAME, it) }
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString(ApiConfig.KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(ApiConfig.KEY_REFRESH_TOKEN, null)
    fun getUserId(): String? = prefs.getString(ApiConfig.KEY_USER_ID, null)
    fun getUsername(): String? = prefs.getString(ApiConfig.KEY_USERNAME, null)

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun clear() {
        prefs.edit().clear().apply()
    }
}
