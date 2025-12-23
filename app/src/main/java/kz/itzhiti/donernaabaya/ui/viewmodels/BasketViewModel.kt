package kz.itzhiti.donernaabaya.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.data.api.OrderItemRequest
import kz.itzhiti.donernaabaya.data.api.Product
import kz.itzhiti.donernaabaya.data.repositories.OrderRepository

data class BasketItem(
    val product: Product,
    val quantity: Int
)

class BasketViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = OrderRepository(app)

    private val _basketItems = MutableLiveData<List<BasketItem>>(emptyList())
    val basketItems: LiveData<List<BasketItem>> = _basketItems

    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> = _totalPrice

    private val _isPlacingOrder = MutableLiveData<Boolean>(false)
    val isPlacingOrder: LiveData<Boolean> = _isPlacingOrder

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _orderSuccess = MutableLiveData<Long?>(null)
    val orderSuccess: LiveData<Long?> = _orderSuccess

    fun addItem(product: Product, quantity: Int) {
        val currentItems = _basketItems.value?.toMutableList() ?: mutableListOf()
        val existingIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentItems.add(BasketItem(product, quantity))
        }

        _basketItems.value = currentItems
        updateTotalPrice()
    }

    fun updateQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(productId)
            return
        }

        val currentItems = _basketItems.value?.toMutableList() ?: return
        val index = currentItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            _basketItems.value = currentItems
            updateTotalPrice()
        }
    }

    fun removeItem(productId: Long) {
        val currentItems = _basketItems.value?.toMutableList() ?: return
        currentItems.removeAll { it.product.id == productId }
        _basketItems.value = currentItems
        updateTotalPrice()
    }

    fun clearBasket() {
        _basketItems.value = emptyList()
        _totalPrice.value = 0.0
        _orderSuccess.value = null
    }

    fun placeOrder(deliveryAddress: String, phone: String, comment: String? = null) {
        if (deliveryAddress.isBlank()) {
            _error.value = "Пожалуйста укажите адрес доставки"
            return
        }

        if (phone.isBlank()) {
            _error.value = "Пожалуйста укажите номер телефона"
            return
        }

        val items = _basketItems.value ?: emptyList()
        if (items.isEmpty()) {
            _error.value = "Корзина пуста"
            return
        }

        val orderItems = items.map { BasketItem(it.product, it.quantity) }
            .map { OrderItemRequest(it.product.id, it.quantity) }

        viewModelScope.launch {
            _isPlacingOrder.value = true
            _error.value = null
            try {
                val order = repo.createOrder(orderItems, deliveryAddress, phone, comment)
                _orderSuccess.value = order.id
                clearBasket()
            } catch (e: Exception) {
                _error.value = "Ошибка создания заказа: ${e.message}"
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }

    private fun updateTotalPrice() {
        val total = _basketItems.value?.sumOf { item ->
            item.product.price * item.quantity
        } ?: 0.0
        _totalPrice.value = total
    }
}
