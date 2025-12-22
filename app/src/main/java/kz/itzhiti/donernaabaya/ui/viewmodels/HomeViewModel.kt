package kz.itzhiti.donernaabaya.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.data.api.Product
import kz.itzhiti.donernaabaya.data.repositories.ProductRepository

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductRepository(app)

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val allProducts = repo.getProducts()
                _products.value = allProducts
            } catch (t: Throwable) {
                _error.value = "Ошибка загрузки товаров: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = if (query.isBlank()) {
                    repo.getProducts()
                } else {
                    repo.searchProducts(query)
                }
                _products.value = results
            } catch (t: Throwable) {
                _error.value = "Ошибка поиска: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _selectedCategory.value = category
            _isLoading.value = true
            _error.value = null
            try {
                val filtered = repo.getProductsByCategory(category)
                _products.value = filtered
            } catch (t: Throwable) {
                _error.value = "Ошибка фильтрации: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearFilter() {
        _selectedCategory.value = null
        loadProducts()
    }
}
