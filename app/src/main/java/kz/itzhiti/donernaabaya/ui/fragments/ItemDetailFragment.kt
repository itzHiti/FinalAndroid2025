package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.ItemDetailFragmentBinding
import kz.itzhiti.donernaabaya.ui.viewmodels.BasketViewModel
import kz.itzhiti.donernaabaya.ui.viewmodels.HomeViewModel

class ItemDetailFragment : Fragment() {
    private var _binding: ItemDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var basketViewModel: BasketViewModel
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        basketViewModel = ViewModelProvider(requireActivity()).get(BasketViewModel::class.java)

        val productId = arguments?.getLong("productId", -1L) ?: -1L
        if (productId != -1L) {
            loadProductDetails(productId)
        }

        setupListeners()
    }

    private fun loadProductDetails(productId: Long) {
        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            val product = products.find { it.id == productId }
            if (product != null) {
                binding.apply {
                    productName.text = product.name
                    productDescription.text = product.description
                    productPrice.text = "${product.price} ₸"
                    productCategory.text = product.category
                    productAvailability.text = if (product.available) "В наличии" else "Нет в наличии"
                    productAvailability.setTextColor(
                        if (product.available) 0xFF4CAF50.toInt() else 0xFFFF6B6B.toInt()
                    )

                    btnAtb.isEnabled = product.available
                    btnAtb.setOnClickListener {
                        basketViewModel.addItem(product, quantity)
                        findNavController().navigate(R.id.action_itemDetailFragment_to_basketFragment)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
