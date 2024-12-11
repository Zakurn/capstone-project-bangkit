package com.example.helotani.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String,
    val author: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val sourceName: String
):Parcelable