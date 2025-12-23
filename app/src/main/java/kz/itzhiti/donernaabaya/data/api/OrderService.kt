package kz.itzhiti.donernaabaya.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderService {
    // ===== PRODUCTS =====
    @GET("api/products")
    suspend fun getProducts(): List<Product>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): Product

    @POST("api/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Product

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body request: UpdateProductRequest): Product

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Void

    // ===== ORDERS =====
    @GET("api/orders")
    suspend fun getUserOrders(): List<Order>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Order

    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Long): Order

    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: Long, @Body request: UpdateOrderStatusRequest): Order

    @DELETE("api/orders/{id}")
    suspend fun cancelOrder(@Path("id") id: Long): Void

    @GET("api/orders/all")
    suspend fun getAllOrders(): List<Order>
}

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String?,
    val available: Boolean
)

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val available: Boolean? = null
)

data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val deliveryAddress: String,
    val phone: String,
    val comment: String? = null
)

data class UpdateOrderStatusRequest(
    val status: String
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

data class Order(
    val id: Long,
    val customerId: String,
    val totalPrice: Double,
    val status: String,
    val createdAt: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val productId: Long,
    val name: String,
    val price: Double,
    val quantity: Int
)
