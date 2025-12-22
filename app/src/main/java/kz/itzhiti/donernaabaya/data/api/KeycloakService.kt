package kz.itzhiti.donernaabaya.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface KeycloakService {
    @FormUrlEncoded
    @POST("realms/{realm}/protocol/openid-connect/token")
    suspend fun login(
        @Path("realm") realm: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): TokenResponse

    @FormUrlEncoded
    @POST("realms/{realm}/protocol/openid-connect/token")
    suspend fun refresh(
        @Path("realm") realm: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): TokenResponse
}

data class TokenResponse(
    val access_token: String,
    val refresh_token: String?,
    val token_type: String,
    val expires_in: Long,
    val refresh_expires_in: Long
)
