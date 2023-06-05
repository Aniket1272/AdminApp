package com.example.admincontroll.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admincontroll.model.Orders
import com.example.admincontroll.model.User
import com.example.admincontroll.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersProductViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _ordersProductList =
        MutableStateFlow<Resource<List<Orders>>>(Resource.Unspecified())
    val ordersProductList = _ordersProductList.asStateFlow()

    init {
        getUser()
    }

    private fun getOrdersFromBase(adminCompanyEmail: String?) {
        viewModelScope.launch {
            db.collection("orders").whereEqualTo("productCompanyEmail", adminCompanyEmail).get()
                .addOnSuccessListener {
                    val orderProduct = it.toObjects(Orders::class.java)
                    viewModelScope.launch {
                        _ordersProductList.emit(Resource.Success(orderProduct))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _ordersProductList.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun getUser() {
        db.collection("Admin").document(firebaseAuth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user.let {
                    Log.d("TAG", "getUser: $it")
                    viewModelScope.launch {
                        if (it != null) {
                            getOrdersFromBase(it.emailOfCompany)
                        }
                    }
                }
            }
            .addOnFailureListener {

            }

    }

}

