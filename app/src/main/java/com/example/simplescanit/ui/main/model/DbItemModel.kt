package com.example.simplescanit.ui.main.model

import androidx.annotation.Keep

@Keep
data class DbItemModel(
    val p: String? = null,
    val barcode: String,
    val name: String? = null,
    val price: String? = null,
    val remCount: String? = null
)