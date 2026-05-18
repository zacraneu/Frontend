package com.example.front.utils

import java.io.File
import javax.inject.Inject

class ImageCompression @Inject constructor() {
    fun compress(file: File): File {
        // Real compression will be added with document picking; keep the API ready for ViewModels.
        return file
    }
}
