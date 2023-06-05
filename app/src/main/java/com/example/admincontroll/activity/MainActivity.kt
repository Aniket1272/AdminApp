package com.example.admincontroll.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.admincontroll.R
import com.example.admincontroll.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentMainCon)
        binding.bottomNav.setupWithNavController(navController)
    }

//
//    private fun showMyApp() {
//        when {
//            intent?.action == Intent.ACTION_SEND -> {
//                if (intent.type?.startsWith("image/") == true) {
//                    handleSendImage(intent) // Handle single image being sent
//                }
//            }
//            intent?.action == Intent.ACTION_SEND_MULTIPLE
//                    && intent.type?.startsWith("image/") == true -> {
//                handleSendMultipleImages(intent) // Handle multiple images being sent
//            }
//            else -> {
//                Toast.makeText(this@MainActivity, "Welcome", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun handleSendImage(intent: Intent) {
//        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
//            // Update UI to reflect image being shared
//            selectedImages.add(it)
//            updateImages()
//        }
//    }
//
//    private fun handleSendMultipleImages(intent: Intent) {
//        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
//            // Update UI to reflect multiple images being shared
//
//            it.forEach {
//                Log.d("mmme", it.toString())
//                selectedImages.add(it as Uri)
//                updateImages()
//            }
//        }
//    }


}