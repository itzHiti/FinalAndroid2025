package kz.itzhiti.donernaabaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.itzhiti.donernaabaya.data.api.Product
import kz.itzhiti.donernaabaya.databinding.ItemProductBinding

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                productName.text = product.name
                productDescription.text = product.description
                productPrice.text = "${product.price} ₸"
                productAvailability.text = if (product.available) "В наличии" else "Нет в наличии"
                productAvailability.setTextColor(
                    if (product.available) 0xFF4CAF50.toInt() else 0xFFFF6B6B.toInt()
                )

                root.setOnClickListener { onItemClick(product) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
