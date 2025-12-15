package kz.itzhiti.donernaabaya

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton   // <-- ВАЖНО

class HomeFragment : Fragment(R.layout.activity_home_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // КНОПКА КОРЗИНЫ
        val basketButton = view.findViewById<MaterialButton>(R.id.btnBasket)
        basketButton.setOnClickListener {
            findNavController().navigate(R.id.basketFragment)
            // или R.id.action_homeFragment_to_basketFragment, если так называется action
        }

        // КНОПКА ОТКРЫТЬ ДЕТАЛИ
//        val openDetailsButton = view.findViewById<MaterialButton>(R.id.btnOpenDetails)
//        openDetailsButton.setOnClickListener {
//            findNavController().navigate(R.id.itemDetailFragment)
//            // или action_homeFragment_to_itemDetailFragment
//        }
    }
}
