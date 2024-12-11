package com.example.helotani.data.api

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("class_id")
    var class_id:Int? = null,
    @SerializedName("class_name")
    var class_name:String? = null,
    @SerializedName("probabilities")
    var probabilities:List<Double>? = null,
    @SerializedName("processing_time")
    var processing_time:Double? = null,
    @SerializedName("uploaded_image_url")
    var uploaded_image_url:String? = null
)