package kz.itzhiti.donernaabaya.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.data.api.Order
import kz.itzhiti.donernaabaya.data.repositories.OrderRepository

class OrderViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = OrderRepository(app)

    private val _orders = MutableLiveData<List<Order>>(emptyList())
    val orders: LiveData<List<Order>> = _orders

    private val _selectedOrder = MutableLiveData<Order?>(null)
    val selectedOrder: LiveData<Order?> = _selectedOrder

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadUserOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userOrders = repo.getUserOrders()
                _orders.value = userOrders
            } catch (t: Throwable) {
                _error.value = "Ошибка загрузки заказов: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getOrderDetails(orderId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val order = repo.getOrder(orderId)
                _selectedOrder.value = order
            } catch (t: Throwable) {
                _error.value = "Ошибка загрузки заказа: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.cancelOrder(orderId)
                loadUserOrders()
            } catch (t: Throwable) {
                _error.value = "Ошибка отмены заказа: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshOrders() {
        loadUserOrders()
    }
}
