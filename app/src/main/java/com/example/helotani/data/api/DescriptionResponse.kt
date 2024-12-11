package com.example.helotani.data.api

import com.google.gson.annotations.SerializedName

data class DescriptionResponse(
    @SerializedName("nama penyakit")
    val diseaseName: String,

    @SerializedName("penjelasan")
    val description: String,

    @SerializedName("cara penanganan")
    val treatment: String,

    @SerializedName("faktor penyebab")
    val causeFactors: String,

    @SerializedName("no")
    val number: Int,

    @SerializedName("Sumber 1")
    val source1: String?,

    @SerializedName("Sumber 2")
    val source2: String?,

    @SerializedName("Sumber 3")
    val source3: String?
)
