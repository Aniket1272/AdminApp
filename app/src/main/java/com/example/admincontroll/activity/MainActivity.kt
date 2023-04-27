package com.example.admincontroll.activity

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.admincontroll.Product
import com.example.admincontroll.R
import com.example.admincontroll.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

//Change the security access settings in the firebase cloud and firestore
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var selectedImages = mutableListOf<Uri>()
    val firestore = Firebase.firestore
    val firebaseAuth = Firebase.auth

    private val storage = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showMyApp()

        //4
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
                                Log.d("yooo", it.toString())
                                selectedImages.add(it)
                            }
                        }

                        //One images was selected
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updateImages()
                }
            }
        //6
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
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
            selectedImages.clear()
            tvSelectedImages.text = "0"
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //1
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProducts() {
                Log.d("test", it.toString())
            }
        }
        if (item.itemId == R.id.logOut) {
            firebaseAuth.signOut()
            val intent = Intent(this, LoginRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    //2
    private fun validateInformation(): Boolean {
        if (selectedImages.isEmpty())
            return false
        if (binding.edName.text.toString().trim().isEmpty())
            return false
        if (binding.edCategory.text.toString().trim().isEmpty())
            return false
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false
        return true
    }

    //3
    private fun saveProducts(state: (Boolean) -> Unit) {
        val imagesByteArrays = getImagesByteArrays() //7
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
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)) {
                val imageAsByteArray = stream.toByteArray()
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }

    //5
    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun showMyApp() {
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
            intent?.action == Intent.ACTION_SEND_MULTIPLE
                    && intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
            else -> {
                Toast.makeText(this@MainActivity, "Welcome", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            // Update UI to reflect image being shared
            selectedImages.add(it)
            updateImages()
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            // Update UI to reflect multiple images being shared

            it.forEach {
                Log.d("mmme", it.toString())
                selectedImages.add(it as Uri)
                updateImages()
            }
        }
    }


}