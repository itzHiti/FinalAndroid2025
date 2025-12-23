package kz.itzhiti.donernaabaya.ui.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.data.api.CoinBalance
import kz.itzhiti.donernaabaya.data.api.CoinTransaction
import kz.itzhiti.donernaabaya.data.api.Order
import kz.itzhiti.donernaabaya.data.database.AppDatabase
import kz.itzhiti.donernaabaya.data.database.entities.AppSettingsEntity
import kz.itzhiti.donernaabaya.data.repositories.AuthRepository
import kz.itzhiti.donernaabaya.data.repositories.CoinRepository
import kz.itzhiti.donernaabaya.data.repositories.OrderRepository

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val authRepo = AuthRepository(app)
    private val coinRepo = CoinRepository(app)
    private val orderRepo = OrderRepository(app)
    private val appSettingsDao = AppDatabase.getDatabase(app).appSettingsDao()

    private val _isDarkMode = MutableLiveData<Boolean>(false)
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    init {
        // Загружаем настройку темы из БД
        viewModelScope.launch {
            val settings = appSettingsDao.getSettings()
            _isDarkMode.postValue(settings?.isDarkMode ?: false)
        }
    }

    private val _username = MutableLiveData<String?>(authRepo.getUsername())
    val username: LiveData<String?> = _username

    private val _userEmail = MutableLiveData<String?>(null)
    val userEmail: LiveData<String?> = _userEmail

    private val _coinBalance = MutableLiveData<CoinBalance?>(null)
    val coinBalance: LiveData<CoinBalance?> = _coinBalance

    private val _coinHistory = MutableLiveData<List<CoinTransaction>>(emptyList())
    val coinHistory: LiveData<List<CoinTransaction>> = _coinHistory

    private val _userOrders = MutableLiveData<List<Order>>(emptyList())
    val userOrders: LiveData<List<Order>> = _userOrders

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Загружаем баланс дкоинов
                val balance = coinRepo.getBalance()
                _coinBalance.value = balance

                // Загружаем историю дкоинов
                val history = coinRepo.getHistory()
                _coinHistory.value = history

                // Загружаем заказы пользователя
                val orders = orderRepo.getUserOrders()
                _userOrders.value = orders
            } catch (t: Throwable) {
                _error.value = "Ошибка загрузки профиля: ${t.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCoinHistoryForOrder(orderId: Long) {
        viewModelScope.launch {
            try {
                val transactions = coinRepo.getOrderTransactions(orderId)
                _coinHistory.value = transactions
            } catch (t: Throwable) {
                _error.value = "Ошибка загрузки истории дкоинов: ${t.message}"
            }
        }
    }

    fun logout() {
        authRepo.logout()
        _username.value = null
        _coinBalance.value = null
        _coinHistory.value = emptyList()
        _userOrders.value = emptyList()
    }

    fun refreshProfile() {
        loadProfileData()
    }

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        viewModelScope.launch {
            val settings = appSettingsDao.getSettings() ?: AppSettingsEntity()
            appSettingsDao.insertSettings(settings.copy(isDarkMode = isDark))
        }

        // Apply theme immediately
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
