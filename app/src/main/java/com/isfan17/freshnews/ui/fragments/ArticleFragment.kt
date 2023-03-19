package com.isfan17.freshnews.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isfan17.freshnews.R
import com.isfan17.freshnews.databinding.FragmentArticleBinding
import com.isfan17.freshnews.helper.FragmentHelper
import com.isfan17.freshnews.ui.viewmodels.NewsViewModel
import com.isfan17.freshnews.ui.viewmodels.ViewModelFactory
import java.lang.ref.WeakReference

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels {
            factory
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val article = args.article
        setupWebView()
        binding.webView.loadUrl(article.url)

        viewModel.isNewsBookmarked(article.url).observe(viewLifecycleOwner) {
            if (it)
            {
                binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                binding.btnBookmark.setOnClickListener {
                    viewModel.setNewsBookmark(article, false)
                    Toast.makeText(context, R.string.remove_from_bookmark, Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_outline)
                binding.btnBookmark.setOnClickListener {
                    viewModel.setNewsBookmark(article, true)
                    Toast.makeText(context, R.string.add_to_bookmark, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        val webViewClient: WebViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (FragmentHelper.isFragmentVisible(WeakReference(this@ArticleFragment))) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.webView.visibility = View.GONE
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (FragmentHelper.isFragmentVisible(WeakReference(this@ArticleFragment))) {
                    binding.progressBar.visibility = View.GONE
                    binding.webView.visibility = View.VISIBLE
                }
                super.onPageFinished(view, url)
            }
        }

        binding.webView.webViewClient = webViewClient
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.defaultTextEncodingName = "utf-8"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}