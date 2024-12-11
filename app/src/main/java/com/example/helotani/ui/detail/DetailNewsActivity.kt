package com.example.helotani.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helotani.data.News
import com.example.helotani.databinding.ActivityDetailNewsBinding

class DetailNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Ambil data berita dari intent
        val news: News? = intent.getParcelableExtra("news_data")

        // Tampilkan data di UI
        news?.let {
            binding.newsName.text = it.title
            binding.newsDescription.text = it.description
            binding.newsUrl.text = "Sumber : \n${news.url}"
            binding.newsPublished.text = "Di Publish oleh: ${it.publishedAt}"
            Glide.with(this).load(it.urlToImage).into(binding.newsImage)

            binding.newsUrl.setOnClickListener {
                openUrlInBrowser(news.url)
            }
        }
    }
    private fun openUrlInBrowser(url: String) {
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
