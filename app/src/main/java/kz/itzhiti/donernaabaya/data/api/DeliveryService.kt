package kz.itzhiti.donernaabaya.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeliveryService {
    @GET("api/deliveries")
    suspend fun getDeliveries(): List<Delivery>

    @GET("api/deliveries/{id}")
    suspend fun getDelivery(@Path("id") id: Long): Delivery

    @PUT("api/deliveries/{id}/status")
    suspend fun updateDeliveryStatus(@Path("id") id: Long, @Body request: UpdateDeliveryStatusRequest): Delivery

    @POST("api/deliveries/{id}/assign")
    suspend fun assignCourier(@Path("id") id: Long, @Body request: AssignCourierRequest): Delivery

    @GET("api/couriers")
    suspend fun getCouriers(): List<Courier>
}

data class Delivery(
    val id: Long,
    val address: String,
    val status: String, // PENDING, ASSIGNED, ON_DELIVERY, COMPLETED, CANCELLED
    val etaMinutes: Int?,
    val courierId: Long?,
    val courierName: String?
)

data class UpdateDeliveryStatusRequest(
    val status: String
)

data class AssignCourierRequest(
    val courierId: Long
)

data class Courier(
    val id: Long,
    val name: String,
    val phone: String
)
