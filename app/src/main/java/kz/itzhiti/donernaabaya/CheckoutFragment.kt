package kz.itzhiti.donernaabaya

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CheckoutFragment : Fragment(R.layout.activity_checkout_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        val btnSendCode = view.findViewById<MaterialButton>(R.id.btnSendCode)
        val tvCodeHint = view.findViewById<View>(R.id.tvCodeHint)
        val codeContainer = view.findViewById<View>(R.id.codeContainer)

        val etCode1 = view.findViewById<EditText>(R.id.etCode1)
        val etCode2 = view.findViewById<EditText>(R.id.etCode2)
        val etCode3 = view.findViewById<EditText>(R.id.etCode3)
        val etCode4 = view.findViewById<EditText>(R.id.etCode4)

        val btnConfirmOrder = view.findViewById<MaterialButton>(R.id.btnConfirmOrder)

        // "Отправка" кода (пока фейк)
        btnSendCode.setOnClickListener {
            val phone = etPhone.text?.toString().orEmpty()

            if (phone.length < 8) {
                Toast.makeText(requireContext(), "Введите корректный номер", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Здесь мог бы быть вызов API / SMS, но мы делаем фейк
            Toast.makeText(requireContext(), "Код отправлен на $phone (допустим 1234)", Toast.LENGTH_SHORT).show()

            tvCodeHint.visibility = View.VISIBLE
            codeContainer.visibility = View.VISIBLE
            etCode1.requestFocus()
        }

        // Авто-переход между полями кода
        setupCodeField(etCode1, next = etCode2)
        setupCodeField(etCode2, next = etCode3, prev = etCode1)
        setupCodeField(etCode3, next = etCode4, prev = etCode2)
        setupCodeField(etCode4, next = null, prev = etCode3)

        btnConfirmOrder.setOnClickListener {
            val code = "${etCode1.text}${etCode2.text}${etCode3.text}${etCode4.text}"
            val correctCode = "1234" // фейковый правильный код

            if (code.length < 4) {
                Toast.makeText(requireContext(), "Введите полный код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (code == correctCode) {
                Toast.makeText(requireContext(), "Заказ оформлен! Спасибо 🙌", Toast.LENGTH_LONG).show()
                // Можно вернуть на главный экран:
                findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(requireContext(), "Неверный код", Toast.LENGTH_SHORT).show()
            }
        }

        // Включать кнопку "Подтвердить", когда все 4 цифры заполнены
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val fullCode = listOf(etCode1, etCode2, etCode3, etCode4).all { it.text?.length == 1 }
                btnConfirmOrder.isEnabled = fullCode
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etCode1.addTextChangedListener(watcher)
        etCode2.addTextChangedListener(watcher)
        etCode3.addTextChangedListener(watcher)
        etCode4.addTextChangedListener(watcher)
    }

    private fun setupCodeField(
        current: EditText,
        next: EditText? = null,
        prev: EditText? = null
    ) {
        current.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    next?.requestFocus()
                } else if (s?.isEmpty() == true) {
                    prev?.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
