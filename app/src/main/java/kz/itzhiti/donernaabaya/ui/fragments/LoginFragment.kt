package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.LoginFragmentBinding
import kz.itzhiti.donernaabaya.ui.viewmodels.AuthViewModel

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.LoginState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Загрузка..."
                }
                is AuthViewModel.LoginState.Success -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Вход"
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is AuthViewModel.LoginState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Вход"
                    binding.tvError.text = state.message
                    binding.tvError.visibility = View.VISIBLE
                }
                is AuthViewModel.LoginState.Idle -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Вход"
                    binding.tvError.visibility = View.GONE
                }
            }
        }
    }
}


