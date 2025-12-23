package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.BasketItemFragmentBinding
import kz.itzhiti.donernaabaya.ui.adapters.BasketAdapter
import kz.itzhiti.donernaabaya.ui.viewmodels.BasketViewModel

class BasketFragment : Fragment() {
    private var _binding: BasketItemFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BasketViewModel
    private lateinit var adapter: BasketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BasketItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(BasketViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = BasketAdapter(
            onQuantityChanged = { productId, newQuantity ->
                viewModel.updateQuantity(productId, newQuantity)
            },
            onRemoveItem = { productId ->
                viewModel.removeItem(productId)
            }
        )
        binding.rvBasket.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BasketFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.basketItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            binding.tvTotalPrice.text = "Итого: $total ₸"
        }

        viewModel.isPlacingOrder.observe(viewLifecycleOwner) { isPlacing ->
            binding.btnOrder.isEnabled = !isPlacing
            binding.btnOrder.text = if (isPlacing) "Оформление..." else "Оформить заказ"
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        viewModel.orderSuccess.observe(viewLifecycleOwner) { orderId ->
            if (orderId != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Успешно!")
                    .setMessage("Заказ #$orderId создан. Спасибо за покупку!")
                    .setPositiveButton("OK") { _, _ ->
                        findNavController().navigate(R.id.action_basket_to_home)
                    }
                    .show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnOrder.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            when {
                phone.isEmpty() -> {
                    binding.etPhone.error = "Введите номер телефона"
                    binding.etPhone.requestFocus()
                }
                address.isEmpty() -> {
                    binding.etAddress.error = "Введите адрес доставки"
                    binding.etAddress.requestFocus()
                }
                else -> {
                    viewModel.placeOrder(address, phone)
                }
            }
        }

        // Toolbar navigation
        binding.root.findViewById<View>(R.id.ivLogo)?.setOnClickListener {
            findNavController().navigate(R.id.action_basket_to_home)
        }

        binding.root.findViewById<View>(R.id.btnProfile)?.setOnClickListener {
            findNavController().navigate(R.id.action_basket_to_profile)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
