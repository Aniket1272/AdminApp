package com.example.admincontroll.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.admincontroll.R
import com.example.admincontroll.databinding.UserOrderItemBinding
import com.example.admincontroll.model.Orders

class UserOrderAdapter : RecyclerView.Adapter<UserOrderAdapter.OrderProductViewHolder>() {

    inner class OrderProductViewHolder(private val binding: UserOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Orders) {
            binding.apply {
                productName.text = order.product?.get(0)?.product?.name
                productPrice.text = "₹ ${order.totalPrice}"
                buyerName.text = order.orderId.toString()
                val image = order.product!![0].product.image[0]
                Glide.with(itemView)
                    .load(image)
                    .into(orderProductImg)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Orders>() {
        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(
            UserOrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        val ordersProduct = differ.currentList[position]
        holder.bind(ordersProduct)
        holder.itemView.setOnClickListener {
            onClick?.invoke(ordersProduct)
        }

    }

    var onClick: ((Orders) -> Unit)? = null

}