package com.example.helotani.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import bitmapToFile
import com.example.helotani.R
import com.example.helotani.data.HistoryItem
import com.example.helotani.data.News
import com.example.helotani.data.api.ApiConfig
import com.example.helotani.data.pref.UserPref
import com.example.helotani.databinding.FragmentHomeBinding
import com.example.helotani.ui.detail.DetailNewsActivity
import com.example.helotani.ui.detail.DetailPredictActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import uriToFile

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingIndicator: ProgressBar
    private var progressDialog: AlertDialog? = null
    private var currentImageUri: Uri? = null
    private lateinit var newsViewModel: NewsViewModel


    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(),"Permission Request Denied",Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Log.e("HomeFragment", "Gallery permission denied")
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                currentImageUri = uri
                uploadImage() // Panggil uploadImage() setelah gambar dipilih
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            photo?.let { bitmap ->
                val imageFile = bitmapToFile(bitmap, requireContext())
                currentImageUri = Uri.fromFile(imageFile)
                uploadImage()
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerViewNews.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        binding.recyclerViewNews.adapter = NewsAdapter(emptyList()){}

        loadingIndicator = binding.loadingIndicator

        binding.materialCardView.setOnClickListener { startGallery() }
        binding.materialCardView2.setOnClickListener { startCamera() }

        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)


        observeViewModel()

        return root
    }

    private fun startCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(cameraIntent)
        } else {
            Log.e("HomeFragment", "No camera app available")
        }
    }

    private fun startGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Below Android 13
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            galleryLauncher.launch(galleryIntent)
        } else {
            Log.e("HomeFragment", "No gallery app available")
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())
            showLoading(true)

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.uploadImage(multipartBody)

                    val probabilities = successResponse.probabilities?.toDoubleArray()
                    val totalProbability = probabilities?.sum() ?: 0.0

                    if (totalProbability < 0.5) {
                        showToast("Gambar Tidak Sesuai atau Image Not Valid")
                    } else {
                        // Simpan ke history
                        val userPref = UserPref(requireContext())
                        userPref.addHistoryItem(HistoryItem(uri.toString(),successResponse.class_name,System.currentTimeMillis()))

                        val intent = Intent(requireContext(), DetailPredictActivity::class.java).apply {
                            putExtra(DetailPredictActivity.EXTRA_IMAGE_URI, uri.toString())
                            putExtra(DetailPredictActivity.EXTRA_CLASS_NAME, successResponse.class_name)
                            putExtra(DetailPredictActivity.EXTRA_PROBABILITIES, probabilities)
                            putExtra(DetailPredictActivity.EXTRA_IMAGE_URL, successResponse.uploaded_image_url)
                        }
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error: ${e.message}")
                    showToast(getString(R.string.empty_image_warning))
                } finally {
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (progressDialog == null) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(R.layout.dialog_loading)
                builder.setCancelable(false)
                progressDialog = builder.create()
            }
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        newsViewModel.newsList.observe(viewLifecycleOwner) { newsList ->
            binding.recyclerViewNews.adapter = NewsAdapter(newsList) { news ->
                openDetailNews(news)
            }
        }

        newsViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        newsViewModel.fetchNews()
    }

    private fun openDetailNews(news: News) {
        val intent = Intent(requireContext(), DetailNewsActivity::class.java)
        intent.putExtra("news_data", news)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}