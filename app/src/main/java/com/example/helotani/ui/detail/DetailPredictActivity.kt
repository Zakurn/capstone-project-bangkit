package com.example.helotani.ui.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helotani.data.api.ApiConfig
import com.example.helotani.databinding.ActivityDetailPredictBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailPredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPredictBinding

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_CLASS_NAME = "extra_class_name"
        const val EXTRA_PROBABILITIES = "extra_probabilities"
        const val EXTRA_IMAGE_URL = "extra_image_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val className = intent.getStringExtra(EXTRA_CLASS_NAME)
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)

        // Menampilkan gambar
        imageUri?.let {
            Glide.with(this).load(Uri.parse(it)).into(binding.testImage)
        }

        // Menampilkan nama kelas hasil prediksi
        binding.predictName.text = className ?: "Unknown Class"

        // Tampilkan loading indicator saat memuat data
        binding.progressBar.visibility = View.VISIBLE

        // Ambil dan tampilkan deskripsi penyakit berdasarkan class_name
        className?.let { fetchDiseaseDetails(it) }
    }

    private fun fetchDiseaseDetails(className: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                binding.progressBar.visibility = View.GONE
                val response = ApiConfig.getDiseaseApiService().getDiseaseDesc()
                if (response.isSuccessful) {
                    val diseaseList = response.body()
                    val diseaseDetail = diseaseList?.find { it.diseaseName.equals(className, true) }

                    withContext(Dispatchers.Main) {
                        diseaseDetail?.let {
                            binding.diseaseDescription.text = it.description
                            binding.diseaseTreatment.text = it.treatment
                            binding.causeFactors.text = it.causeFactors
                            binding.source1.text = it.source1 ?: ""
                            binding.source2.text = it.source2 ?: ""
                            binding.source3.text = it.source3 ?: ""
                        } ?: run {
                            binding.titleFaktor.visibility = View.GONE
                            binding.titleSumber.visibility = View.GONE
                            binding.titlePenanganan.visibility = View.GONE
                            binding.diseaseDescription.text = "Detail Penyakit tidak ditemukan atau Langka."
                        }
                    }
                } else {
                    Log.e("DetailPredict", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DetailPredict", "Exception: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
