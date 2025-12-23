package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.HomeFragmentBinding
import kz.itzhiti.donernaabaya.ui.adapters.ProductAdapter
import kz.itzhiti.donernaabaya.ui.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val bundle = Bundle().apply {
                putLong("productId", product.id)
            }
            findNavController().navigate(R.id.action_home_to_details, bundle)
        }
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }

    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
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
         binding.btnBasket.setOnClickListener {
             findNavController().navigate(R.id.action_home_to_basket)
         }

         binding.btnProfile?.setOnClickListener {
             findNavController().navigate(R.id.action_home_to_profile)
         }

         binding.swipeRefresh?.setOnRefreshListener {
             viewModel.loadProducts()
             binding.swipeRefresh?.isRefreshing = false
         }

         binding.btnSearch?.setOnClickListener {
             val query = binding.etSearch?.text.toString()
             viewModel.searchProducts(query)
         }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
