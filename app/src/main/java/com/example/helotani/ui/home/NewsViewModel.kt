package com.example.helotani.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.helotani.data.News
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class NewsViewModel : ViewModel() {

    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> get() = _newsList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchNews() {
        if (_newsList.value != null) return // Jangan memuat ulang jika data sudah ada

        _loading.value = true
        val client = AsyncHttpClient()
        val url = "https://newsapi.org/v2/everything?q=pertanian+Indonesia&language=id&sortBy=publishedAt&apiKey=a30f3df71a2b4a3bae18a83e5ca33695"

        client.addHeader("User-Agent", "com.example.helotani/1.0 (Android App)")
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                val newsList = mutableListOf<News>()

                if (response.has("articles")) {
                    val articles = response.getJSONArray("articles")
                    for (i in 0 until articles.length()) {
                        val articleObject = articles.getJSONObject(i)
                        val sourceObject = articleObject.getJSONObject("source")

                        val news = News(
                            title = articleObject.optString("title", "Unknown Title"),
                            author = articleObject.optString("author", "Unknown Author"),
                            description = articleObject.optString("description", "No Description"),
                            url = articleObject.optString("url", ""),
                            urlToImage = articleObject.optString("urlToImage", null),
                            publishedAt = articleObject.optString("publishedAt", "Unknown Date"),
                            sourceName = sourceObject.optString("name", "Unknown Source")
                        )
                        newsList.add(news)
                    }
                }

                _newsList.postValue(newsList)
                _loading.postValue(false)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                _loading.postValue(false)
            }
        })
    }
}
