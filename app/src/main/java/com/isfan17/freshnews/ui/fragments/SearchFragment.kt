package com.isfan17.freshnews.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isfan17.freshnews.R
import com.isfan17.freshnews.databinding.FragmentSearchBinding
import com.isfan17.freshnews.helper.Constants.Companion.NO_RESULTS_MSG
import com.isfan17.freshnews.helper.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.isfan17.freshnews.helper.Result
import com.isfan17.freshnews.ui.adapters.ArticleCardAdapter
import com.isfan17.freshnews.ui.adapters.ArticleListAdapter
import com.isfan17.freshnews.ui.viewmodels.NewsViewModel
import com.isfan17.freshnews.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleListAdapter: ArticleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels {
            factory
        }

        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.searchNews.observe(viewLifecycleOwner) { result ->
            if (result != null)
            {
                when (result) {
                    is Result.Loading -> {
                        binding.rvSearchNews.visibility = View.GONE
                        showNetworkErrorMsg(false)
                        showNoResultsMsg(false)
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.rvSearchNews.visibility = View.VISIBLE
                        showNetworkErrorMsg(false)
                        showNoResultsMsg(false)
                        binding.progressBar.visibility = View.GONE

                        val articles = result.data
                        articleListAdapter.submitList(articles)
                    }
                    is Result.Error -> {
                        binding.rvSearchNews.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE

                        Log.e("SearchFragment", "ERROR: ${result.error}")
                        if (result.error == NO_RESULTS_MSG) {
                            showNoResultsMsg(true)
                        } else {
                            showNetworkErrorMsg(true)
                            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.toString().isNotEmpty()) {
                    viewModel.searchForNews(query.toString())
                }

                return true
            }

            var job: Job? = null
            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    if (newText.toString().isNotEmpty()) {
                        viewModel.searchForNews(newText.toString())
                    }
                }

                return true;
            }

        })
    }

    private fun showNoResultsMsg(state: Boolean) {
        binding.ivNoResult.visibility = if (state) View.VISIBLE else View.GONE
        binding.tvNoResult.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showNetworkErrorMsg(state: Boolean) {
        binding.ivNoNetwork.visibility = if (state) View.VISIBLE else View.GONE
        binding.tvNoNetwork.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        articleListAdapter = ArticleListAdapter { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_articleFragment,
                bundle
            )
        }

        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = articleListAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}