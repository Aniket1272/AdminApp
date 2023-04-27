package com.example.admincontroll.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    fun isUserSignedIn() : Boolean {
        if (FirebaseAuth.getInstance().currentUser?.uid != null)
            return true
        return false

    }
}