package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.ProfileFragmentBinding
import kz.itzhiti.donernaabaya.ui.adapters.CoinTransactionAdapter
import kz.itzhiti.donernaabaya.ui.adapters.OrderAdapter
import kz.itzhiti.donernaabaya.ui.viewmodels.ProfileViewModel

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var transactionAdapter: CoinTransactionAdapter
    private lateinit var orderAdapter: OrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setupRecyclerViews()
        setupObservers()
        setupListeners()

        viewModel.loadProfileData()
    }

    private fun setupRecyclerViews() {
        transactionAdapter = CoinTransactionAdapter()
        binding.rvCoinHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }

        orderAdapter = OrderAdapter { order ->
            // TODO: Переход на детали заказа
        }
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }
    }

    private fun setupObservers() {
        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.tvUsername.text = username ?: "Гость"
        }

        viewModel.coinBalance.observe(viewLifecycleOwner) { balance ->
            if (balance != null) {
                binding.tvCoinBalance.text = "${balance.doner_coins} 🪙"
            }
        }

        viewModel.coinHistory.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }

        viewModel.userOrders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders)
            binding.tvNoOrders.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.btnViewCoins.setOnClickListener {
            showCoinHistoryDialog()
        }

        // Dark mode switch
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }

        // Load current theme setting
        viewModel.isDarkMode.observe(viewLifecycleOwner) { isDark ->
            binding.switchDarkMode.isChecked = isDark
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да") { _, _ ->
                    viewModel.logout()
                    findNavController().navigate(R.id.loginFragment)
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.refreshProfile()
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_home)
        }
    }

    private fun showCoinHistoryDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_coin_history, null)
        val rvHistory = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvHistory)
        val adapter = CoinTransactionAdapter()
        rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        viewModel.coinHistory.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("История Дкоинов")
            .setView(view)
            .setPositiveButton("Закрыть", null)
            .show()
    }
}

