package com.example.helotani.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helotani.R
import com.example.helotani.data.News

class NewsAdapter(
    private var newsList: List<News>,
    private val onItemClicked: (News) -> Unit // Tambahkan callback untuk klik item
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsTitle: TextView = itemView.findViewById(R.id.newsName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.newsTitle.text = news.title
        Glide.with(holder.itemView.context).load(news.urlToImage).into(holder.newsImage)

        // Tambahkan listener klik pada item
        holder.itemView.setOnClickListener {
            onItemClicked(news)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}
