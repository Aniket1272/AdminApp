package com.example.admincontroll.fragments.authFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.admincontroll.R
import com.example.admincontroll.activity.MainActivity
import com.example.admincontroll.databinding.FragmentLoginBinding
import com.example.admincontroll.utils.RegisterValidation
import com.example.admincontroll.utils.Resource
import com.example.admincontroll.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewmodel by viewModels<LoginViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val email = edtLoginEmail.text.toString().trim()
                val password = edtLoginPassword.text.toString()


                viewmodel.login(email, password)
            }
        }

        lifecycleScope.launch {
            viewmodel.login.collect {
                when(it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "loading..", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "not logged in", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Success -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.validation.collect{validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtLoginEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if(validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtLoginPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }

}