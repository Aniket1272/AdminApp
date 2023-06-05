package com.example.admincontroll.fragments.controlHome

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.admincontroll.model.Product
import com.example.admincontroll.R
import com.example.admincontroll.activity.LoginRegisterActivity
import com.example.admincontroll.databinding.FragmentAddProductBinding
import com.example.admincontroll.viewmodels.AddProductViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID


@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private val viewModel by viewModels<AddProductViewModel>()

    private var selectedImages = mutableListOf<Uri>()
    private val firestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth
    private val storage = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateImages()
        binding.toolbar.title = "DailyStore"
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.saveProduct -> {
                    val productValidation: Boolean =
                        binding.edName.text.trim().isEmpty()
                                && binding.edCategory.text.trim().isEmpty()
                                && binding.edPrice.text.trim().isEmpty()
                                && binding.edDescription.text.trim().isEmpty()
                                && selectedImages.isEmpty()

                    println("this is test$productValidation")
                    if (!productValidation) {
                        saveProducts {
                            Log.d("test", it.toString())
                        }
                    } else {
                        Toast.makeText(
                            requireActivity().applicationContext,
                            "check input",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }

                R.id.logOut -> {
                    firebaseAuth.signOut()
                    val intent =
                        Intent(requireActivity().applicationContext, LoginRegisterActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }

                else -> {
                    Toast.makeText(requireActivity().applicationContext, "this", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    //Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imagesUri = intent.clipData?.getItemAt(it)?.uri
                            imagesUri?.let {
                                selectedImages = viewModel.selectImageReturn()
                                viewModel.addImages(it)
                            }
                        }

                        //One images was selected
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let {
                            selectedImages = viewModel.selectImageReturn()
                            viewModel.addImages(it)

                        }
                    }
                    updateImages()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }

        binding.btnReset.setOnClickListener {
            clearAll()
        }

    }

    private fun clearAll() {
        binding.apply {
            edName.text.clear()
            edCategory.text.clear()
            edPrice.text.clear()
            edDescription.text.clear()
            viewModel.removeImages()
            tvSelectedImages.text = "0"
        }
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = viewModel.noOfImage()
    }

    private fun saveProducts(state: (Boolean) -> Unit) {
        val imagesByteArrays = getImagesByteArrays() //7
        if (imagesByteArrays.isEmpty()) println("is empty")
        val name = binding.edName.text.toString().trim()
        val images = mutableListOf<String>()
        val category = binding.edCategory.text.toString().trim()
        val productDescription = binding.edDescription.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val adminEmail = binding.email.text.toString().trim()

        lifecycleScope.launch {
            showLoading()
            try {
                async {
                    Log.d("test1", "test")
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = storage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: java.lang.Exception) {
                hideLoading()
                state(false)
            }

            Log.d("test2", "test")

            val product = Product(
                name,
                category,
                price.toFloat(),
                productDescription,
                images,
                adminEmail
            )

            firestore.collection("products").add(product).addOnSuccessListener {
                state(true)
                hideLoading()
            }.addOnFailureListener {
                Log.e("test2", it.message.toString())
                state(false)
                hideLoading()
            }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE

    }


    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            println("this is Uri: $it")
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)) {
                val imageAsByteArray = stream.toByteArray()
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }


}