package kz.itzhiti.donernaabaya

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class BasketFragment : Fragment(R.layout.activity_basket_item) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMakeOrder = view.findViewById<Button>(R.id.checkoutButton)
        btnMakeOrder.setOnClickListener {
            findNavController().navigate(
                R.id.action_basketFragment_to_checkoutFragment
            )
        }
    }
}
