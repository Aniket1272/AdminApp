package com.example.admincontroll

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
import com.example.admincontroll.databinding.FragmentRegisterBinding
import com.example.admincontroll.model.User
import com.example.admincontroll.utils.RegisterValidation
import com.example.admincontroll.utils.Resource
import com.example.admincontroll.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewmodel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    edtRegisterName.text.toString(),
                    edtRegisterCompanyName.text.toString(),
                    edtRegisterAddress.text.toString(),
                    edtRegisterEmail.text.toString(),
                    edtRegisterPassword.text.toString()
                )
                viewmodel.crateAccountWithEmailAndPassword(user)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.register.collect {
                when(it) {
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "loading..", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "account already exist", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onViewCreated: loading")
                    }
                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment2)
                        Toast.makeText(requireContext(), "account created successfully", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.validation.collect { validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtRegisterEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtRegisterPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}