package kz.itzhiti.donernaabaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.itzhiti.donernaabaya.data.api.Order
import kz.itzhiti.donernaabaya.databinding.ItemOrderBinding

class OrderAdapter(
    private val onItemClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onItemClick: (Order) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                orderId.text = "Заказ #${order.id}"
                orderPrice.text = "${order.totalPrice} ₸"
                orderStatus.text = getStatusLabel(order.status)
                orderStatus.setTextColor(getStatusColor(order.status))
                orderDate.text = order.createdAt

                root.setOnClickListener { onItemClick(order) }
            }
        }

        private fun getStatusLabel(status: String): String = when (status) {
            "PENDING" -> "В ожидании"
            "CONFIRMED" -> "Подтвержден"
            "PREPARING" -> "Готовится"
            "READY" -> "Готов"
            "ON_DELIVERY" -> "В доставке"
            "DELIVERED" -> "Доставлен"
            "CANCELLED" -> "Отменен"
            else -> status
        }

        private fun getStatusColor(status: String): Int = when (status) {
            "PENDING" -> 0xFFFFA500.toInt()
            "CONFIRMED" -> 0xFF2196F3.toInt()
            "DELIVERED" -> 0xFF4CAF50.toInt()
            "CANCELLED" -> 0xFFFF6B6B.toInt()
            else -> 0xFF9E9E9E.toInt()
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
    }
}
