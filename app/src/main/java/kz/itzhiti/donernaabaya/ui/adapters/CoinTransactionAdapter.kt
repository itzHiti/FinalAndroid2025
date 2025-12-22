package kz.itzhiti.donernaabaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.itzhiti.donernaabaya.data.api.CoinTransaction
import kz.itzhiti.donernaabaya.databinding.ItemCoinTransactionBinding

class CoinTransactionAdapter : ListAdapter<CoinTransaction, CoinTransactionAdapter.TransactionViewHolder>(
    TransactionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemCoinTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(private val binding: ItemCoinTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: CoinTransaction) {
            binding.apply {
                description.text = transaction.description
                amount.text = "${if (transaction.type == "EARNED") "+" else "-"}${transaction.amount} 🪙"
                amount.setTextColor(
                    if (transaction.type == "EARNED") 0xFF4CAF50.toInt() else 0xFFFF6B6B.toInt()
                )
                transactionType.text = getTypeLabel(transaction.type)
                date.text = transaction.created_at
            }
        }

        private fun getTypeLabel(type: String): String = when (type) {
            "EARNED" -> "Заработано"
            "SPENT" -> "Потрачено"
            "REFUNDED" -> "Возвращено"
            "BONUS" -> "Бонус"
            else -> type
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<CoinTransaction>() {
        override fun areItemsTheSame(oldItem: CoinTransaction, newItem: CoinTransaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CoinTransaction, newItem: CoinTransaction) =
            oldItem == newItem
    }
}
