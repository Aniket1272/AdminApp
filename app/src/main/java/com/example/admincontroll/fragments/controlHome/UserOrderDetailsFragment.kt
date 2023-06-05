package com.example.admincontroll.fragments.controlHome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.admincontroll.R
import com.example.admincontroll.databinding.FragmentUserOrderBinding
import com.example.admincontroll.databinding.FragmentUserOrderDetailsBinding

class UserOrderDetailsFragment : Fragment() {

    private val arg by navArgs<UserOrderDetailsFragmentArgs>()
    private lateinit var binding: FragmentUserOrderDetailsBinding

    private var flag = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserOrderDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orders = arg.orders
        val address = orders.address
        val product = orders.product!![0].product
        binding.apply {
            orderId.text = "#"+orders.orderId.toString()
            date.text = orders.date

            addressTitle.text = address!!.addressTitle
            streetAddress.text = address.street
            Log.d("TAG", "onViewCreated: ${address.phone}")

            phoneNumber.text = address.phone
            city.text = address.city
            state.text = address.state

            Glide.with(requireActivity()).load(product.image[0]).into(imageOfProduct)
            orderPName.text = product.name
            oderPPrice.text = "₹"+orders.totalPrice.toString()
            productDescription.text = product.description
        }

        binding.showDes.setOnClickListener {
            if(flag == 0) {
                showDescription()
            } else if (flag == 1) {
                hideDescription()
            }
        }
    }

    private fun hideDescription() {
        binding.cardView.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE
        binding.showDes.setImageResource(R.drawable.ic_up)
        flag = 0
    }

    private fun showDescription() {
        binding.cardView.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
        binding.showDes.setImageResource(R.drawable.ic_down)
        flag = 1
    }
}