package kz.itzhiti.donernaabaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.itzhiti.donernaabaya.databinding.ItemBasketBinding
import kz.itzhiti.donernaabaya.ui.viewmodels.BasketItem

class BasketAdapter(
    private val onQuantityChanged: (Long, Int) -> Unit,
    private val onRemoveItem: (Long) -> Unit
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    private var items: List<BasketItem> = emptyList()

    fun submitList(newItems: List<BasketItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val binding = ItemBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding, onQuantityChanged, onRemoveItem)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class BasketViewHolder(
        private val binding: ItemBasketBinding,
        private val onQuantityChanged: (Long, Int) -> Unit,
        private val onRemoveItem: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BasketItem) {
            binding.apply {
                productName.text = item.product.name
                productPrice.text = "${item.product.price} ₸"
                quantity.text = item.quantity.toString()

                btnMinus.setOnClickListener {
                    onQuantityChanged(item.product.id, item.quantity - 1)
                }

                btnPlus.setOnClickListener {
                    onQuantityChanged(item.product.id, item.quantity + 1)
                }

                btnRemove.setOnClickListener {
                    onRemoveItem(item.product.id)
                }
            }
        }
    }
}
