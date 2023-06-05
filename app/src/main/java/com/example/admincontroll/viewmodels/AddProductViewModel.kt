package com.example.admincontroll.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(): ViewModel() {

    private val selectedImages = mutableListOf<Uri>()

    fun addImages(uri: Uri) {
        selectedImages.add(uri)
    }

    fun noOfImage():String {
        return selectedImages.size.toString()
    }

    fun selectImageReturn(): MutableList<Uri> = selectedImages

    fun removeImages(): String = selectedImages.clear().toString()

}