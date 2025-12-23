package kz.itzhiti.donernaabaya.data.api

import android.content.Context
import kz.itzhiti.donernaabaya.data.auth.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val ORDER_BASE_URL = "http://10.0.2.2:8081/"
    private const val DELIVERY_BASE_URL = "http://10.0.2.2:8082/"

    fun orderRetrofit(context: Context): Retrofit = buildRetrofit(context, ORDER_BASE_URL)
    fun deliveryRetrofit(context: Context): Retrofit = buildRetrofit(context, DELIVERY_BASE_URL)

    fun orderService(context: Context): OrderService = orderRetrofit(context).create(OrderService::class.java)
    fun deliveryService(context: Context): DeliveryService = deliveryRetrofit(context).create(DeliveryService::class.java)
    fun coinService(context: Context): CoinService = orderRetrofit(context).create(CoinService::class.java)

    private fun buildRetrofit(context: Context, baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
