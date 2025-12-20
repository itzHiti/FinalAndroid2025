package kz.itzhiti.donernaabaya

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ItemDetailFragment : Fragment(R.layout.item_detail_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // кнопка "В корзину" на экране детали
        val addToBasketButton = view.findViewById<Button>(R.id.btnAtb)
        addToBasketButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_itemDetailFragment_to_basketFragment
            )
        }

        val closeButton = view.findViewById<ImageButton>(R.id.btnClose)
        closeButton.setOnClickListener {
            findNavController().navigateUp()   // ← вернёт обратно на HomeFragment
        }
    }
}
