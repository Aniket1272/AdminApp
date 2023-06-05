package com.example.admincontroll.fragments.controlHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admincontroll.R
import com.example.admincontroll.adapters.UserOrderAdapter
import com.example.admincontroll.databinding.FragmentUserOrderBinding
import com.example.admincontroll.utils.Resource
import com.example.admincontroll.viewmodels.OrdersProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserOrderFragment : Fragment() {

    private lateinit var binding: FragmentUserOrderBinding
    private val viewModel by viewModels<OrdersProductViewModel>()

    private lateinit var ordersProductAdapter: UserOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrderProductRv()

        ordersProductAdapter.onClick = {
            val bundle = Bundle().apply {
                putParcelable("orders", it)
            }
            findNavController().navigate(
                R.id.action_userOrderFragment_to_userOrderDetailsFragment,
                bundle
            )
        }

        lifecycleScope.launch {
            viewModel.ordersProductList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        hideLoader()
                    }

                    is Resource.Loading -> {
                        showLoader()
                    }

                    is Resource.Success -> {
                        hideLoader()
                        ordersProductAdapter.differ.submitList(it.data)

                    }

                    else -> {
                        showLoader()
                    }
                }
            }
        }
    }

    private fun hideLoader() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoader() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun setUpOrderProductRv() {
        ordersProductAdapter = UserOrderAdapter()
        binding.rvOrderProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)
            adapter = ordersProductAdapter
        }
    }


}