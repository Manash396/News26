package com.example.news26.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.news26.NewsViewModel
import com.example.news26.R
import com.example.news26.data.Article
import com.example.news26.databinding.FragmentWebBinding
import com.example.news26.db.RoomArticle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class Web : Fragment() {

    private var _binding : FragmentWebBinding? =null
    private val binding get() = _binding!!

    private lateinit var webView: WebView
    private  var article: Article? = null
    private lateinit var favAddbtn : FloatingActionButton

    private val viewModel : NewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWebBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.webContent
        favAddbtn = binding.addFav
        arguments?.let {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                article = it.getSerializable("article", Article::class.java)
            }else{
                article = it.getSerializable("article") as Article?
            }
        }

        setUpWebView()

        favAddbtn.setOnClickListener {
            article?.let {
                val roomArticle = RoomArticle(
                    author = article!!.author,
                    content = article!!.content,
                    description = article!!.description,
                    publishedAt = article!!.publishedAt,
                    source = article!!.source,
                    title =  article!!.title,
                    url = article!!.url,
                    urlToImage = article!!.urlToImage,
                )

                viewModel.addToFav(roomArticle)

                Snackbar.make(view,"Added to Favourites", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                         viewModel.deleteArticle(roomArticle)
                    }
                    show()
                }


            }

        }

    }

    private fun setUpWebView(){
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.settings.apply {
            domStorageEnabled = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            allowFileAccess = false
        }

        article?.let {
            webView.loadUrl(article!!.url)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}