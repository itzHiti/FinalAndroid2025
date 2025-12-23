package kz.itzhiti.donernaabaya.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kz.itzhiti.donernaabaya.R
import kz.itzhiti.donernaabaya.databinding.RegisterFragmentBinding

class RegisterFragment : Fragment(R.layout.register_fragment) {
    private lateinit var binding: RegisterFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterFragmentBinding.bind(view)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInputs(username, email, password, confirmPassword)) {
                // TODO: Интегрировать с AuthViewModel для регистрации
                binding.tvError.text = "Регистрация через Keycloak еще не реализована"
                binding.tvError.visibility = View.VISIBLE
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInputs(username: String, email: String, password: String, confirmPassword: String): Boolean {
        binding.tvError.visibility = View.GONE

        if (username.isBlank()) {
            binding.tvError.text = "Пожалуйста введите имя пользователя"
            binding.tvError.visibility = View.VISIBLE
            return false
        }

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvError.text = "Пожалуйста введите корректный email"
            binding.tvError.visibility = View.VISIBLE
            return false
        }

        if (password.isBlank() || password.length < 6) {
            binding.tvError.text = "Пароль должен быть минимум 6 символов"
            binding.tvError.visibility = View.VISIBLE
            return false
        }

        if (password != confirmPassword) {
            binding.tvError.text = "Пароли не совпадают"
            binding.tvError.visibility = View.VISIBLE
            return false
        }

        return true
    }
}

