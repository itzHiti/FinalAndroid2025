package kz.itzhiti.donernaabaya.data.repositories

import android.content.Context
import kz.itzhiti.donernaabaya.data.api.ApiClient
import kz.itzhiti.donernaabaya.data.api.OrderService
import kz.itzhiti.donernaabaya.data.api.Product
import kz.itzhiti.donernaabaya.data.database.AppDatabase
import kz.itzhiti.donernaabaya.data.database.entities.ProductEntity

class ProductRepository(context: Context) {
    private val api = ApiClient.orderService(context)
    private val db = AppDatabase.getDatabase(context)
    private val productDao = db.productDao()

    suspend fun getProducts(): List<Product> {
        return try {
            val products = api.getProducts()
            // Кэшируем в БД
            productDao.deleteAllProducts()
            productDao.insertProducts(products.map { it.toEntity() })
            products
        } catch (e: Exception) {
            // Если нет интернета, берем из БД
            productDao.getAllAvailableProducts().map { it.toProduct() }
        }
    }

    suspend fun getProduct(id: Long): Product {
        return try {
            val product = api.getProduct(id)
            productDao.insertProduct(product.toEntity())
            product
        } catch (e: Exception) {
            productDao.getProductById(id)?.toProduct() ?: throw e
        }
    }

    suspend fun searchProducts(query: String): List<Product> {
        return try {
            val products = api.getProducts()
            products.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            productDao.searchProducts(query).map { it.toProduct() }
        }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val products = api.getProducts()
            products.filter { it.category == category }
        } catch (e: Exception) {
            productDao.getProductsByCategory(category).map { it.toProduct() }
        }
    }

    private fun Product.toEntity(): ProductEntity {
        return ProductEntity(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            imageUrl = imageUrl,
            available = available
        )
    }

    private fun ProductEntity.toProduct(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            imageUrl = imageUrl,
            available = available
        )
    }
}
