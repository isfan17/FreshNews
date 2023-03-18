package com.isfan17.freshnews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isfan17.freshnews.R
import com.isfan17.freshnews.databinding.FragmentBookmarkedNewsBinding
import com.isfan17.freshnews.ui.adapters.ArticleListAdapter
import com.isfan17.freshnews.ui.viewmodels.NewsViewModel
import com.isfan17.freshnews.ui.viewmodels.ViewModelFactory

class BookmarkedNewsFragment : Fragment() {

    private var _binding: FragmentBookmarkedNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleAdapter: ArticleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels {
            factory
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()

        viewModel.getBookmarkedNews().observe(viewLifecycleOwner) { articles ->
            if (articles.isNotEmpty())
            {
                binding.ivNoData.visibility = View.GONE
                binding.tvNoData.visibility = View.GONE
            }
            else
            {
                binding.ivNoData.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.VISIBLE
            }
            articleAdapter.submitList(articles)
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleListAdapter { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_bookmarkedNewsFragment_to_articleFragment,
                bundle
            )
        }

        binding.rvBookmarkedNews.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = articleAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}