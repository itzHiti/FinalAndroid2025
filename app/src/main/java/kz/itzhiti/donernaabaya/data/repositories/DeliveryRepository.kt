package kz.itzhiti.donernaabaya.data.repositories

import android.content.Context
import kz.itzhiti.donernaabaya.data.api.ApiClient
import kz.itzhiti.donernaabaya.data.api.AssignCourierRequest
import kz.itzhiti.donernaabaya.data.api.Delivery
import kz.itzhiti.donernaabaya.data.api.DeliveryService
import kz.itzhiti.donernaabaya.data.api.UpdateDeliveryStatusRequest
import kz.itzhiti.donernaabaya.data.database.AppDatabase
import kz.itzhiti.donernaabaya.data.database.entities.DeliveryEntity

class DeliveryRepository(context: Context) {
    private val api = ApiClient.deliveryService(context)
    private val db = AppDatabase.getDatabase(context)
    private val deliveryDao = db.deliveryDao()

    suspend fun getDeliveries(): List<Delivery> {
        return try {
            val deliveries = api.getDeliveries()
            deliveries.forEach { delivery ->
                val entity = DeliveryEntity(
                    id = delivery.id,
                    orderId = 0L,
                    address = delivery.address,
                    status = delivery.status,
                    courierId = delivery.courierId,
                    courierName = delivery.courierName,
                    etaMinutes = delivery.etaMinutes,
                    createdAt = System.currentTimeMillis(),
                    synced = true
                )
                deliveryDao.insertDelivery(entity)
            }
            deliveries
        } catch (e: Exception) {
            deliveryDao.getAllDeliveries().map { it.toDelivery() }
        }
    }

    suspend fun getDelivery(id: Long): Delivery {
        return try {
            val delivery = api.getDelivery(id)
            val entity = DeliveryEntity(
                id = delivery.id,
                orderId = 0L,
                address = delivery.address,
                status = delivery.status,
                courierId = delivery.courierId,
                courierName = delivery.courierName,
                etaMinutes = delivery.etaMinutes,
                createdAt = System.currentTimeMillis(),
                synced = true
            )
            deliveryDao.insertDelivery(entity)
            delivery
        } catch (e: Exception) {
            deliveryDao.getDeliveryById(id)?.toDelivery() ?: throw e
        }
    }

    suspend fun updateDeliveryStatus(id: Long, status: String): Delivery {
        return try {
            val request = UpdateDeliveryStatusRequest(status)
            val delivery = api.updateDeliveryStatus(id, request)
            deliveryDao.updateDeliveryStatus(id, status)
            delivery
        } catch (e: Exception) {
            throw DeliveryException("Ошибка обновления статуса доставки: ${e.message}", e)
        }
    }

    suspend fun assignCourier(id: Long, courierId: Long): Delivery {
        return try {
            val request = AssignCourierRequest(courierId)
            val delivery = api.assignCourier(id, request)
            deliveryDao.assignCourier(id, courierId, delivery.courierName ?: "Unknown")
            delivery
        } catch (e: Exception) {
            throw DeliveryException("Ошибка назначения доставщика: ${e.message}", e)
        }
    }

    private fun DeliveryEntity.toDelivery(): Delivery {
        return Delivery(
            id = id,
            address = address,
            status = status,
            etaMinutes = etaMinutes,
            courierId = courierId,
            courierName = courierName
        )
    }
}

class DeliveryException(message: String, cause: Throwable? = null) : Exception(message, cause)
