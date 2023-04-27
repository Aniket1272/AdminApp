package com.example.admincontroll.viewmodels

import androidx.lifecycle.ViewModel
import com.example.admincontroll.model.User
import com.example.admincontroll.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()


    fun crateAccountWithEmailAndPassword(user: User) {
        if(checkValidation(user)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.emailOfCompany, user.password)
                .addOnSuccessListener {
                    it.user?.let { fireUser ->
                        saveUserInfo(user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }

        } else {
            val registerFieldState = RegisterFieldState(
                validateEmail(user.emailOfCompany),
                validationPassword(user.password)
            )

            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }


    private fun saveUserInfo(user: User) {
        db.collection("Admin").document(user.companyName)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User): Boolean {
        val emailValidation = validateEmail(user.emailOfCompany)
        val passwordValidation = validationPassword(user.password)

        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}