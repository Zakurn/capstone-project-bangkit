package com.example.helotani.data

data class HistoryItem(
    val imageUri: String,
    val className: String?, // Tambahkan properti className untuk menyimpan nama penyakit
    val timestamp: Long
)
