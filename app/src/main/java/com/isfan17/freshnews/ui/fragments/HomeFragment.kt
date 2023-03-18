package com.isfan17.freshnews.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.isfan17.freshnews.R
import com.isfan17.freshnews.databinding.FragmentHomeBinding
import com.isfan17.freshnews.ui.adapters.ArticleCardAdapter
import com.isfan17.freshnews.ui.viewmodels.NewsViewModel
import com.isfan17.freshnews.ui.viewmodels.ViewModelFactory
import com.isfan17.freshnews.helper.Result
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleCardAdapter: ArticleCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels {
            factory
        }

        // Observing The Country Code
        viewModel.getCountryCode().observe(viewLifecycleOwner) { countryCode ->
            Log.d("HomeFragment", "Country Code : $countryCode")

            binding.btnCountry.text = countryCode.uppercase(Locale.ROOT)
            viewModel.getFreshNews(countryCode = countryCode)

            // Categories Chip Setup
            binding.cgCategories.removeAllViews()
            val categories = resources.getStringArray(R.array.data_categories)
            categories.forEachIndexed { index, category ->
                val chip = createCategoryChip(category)
                if (index == 0) chip.isChecked = true
                chip.setOnClickListener {
                    chip.isChecked = true
                    viewModel.getFreshNews(countryCode = countryCode, category = chip.text.toString())
                }
            }

            // Refreshing News When Reconnected to Internet
            binding.btnRefresh.setOnClickListener {
                val checkedChip: Chip = view.findViewById(binding.cgCategories.checkedChipId)
                val category = checkedChip.text.toString()
                Log.d("HomeFragment", "CheckedChip: $category")
                viewModel.getFreshNews(countryCode = countryCode, category = category)
            }
        }

        // Btn Country Click Listener
        val countries = resources.getStringArray(R.array.data_countries)
        binding.btnCountry.setOnClickListener {
            val dialogBuilder = showCountryList()
            dialogBuilder?.setItems(countries) { _, item ->
                extractCountryCode(countries[item].toString())?.let { countryCode ->
                    viewModel.saveCountryCode(countryCode)
                }
            }

            val alert = dialogBuilder?.create()
            alert?.show()
        }

        binding.btnBookmarks.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_bookmarkedNewsFragment
            )
        }

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_searchFragment
            )
        }

        setupRecyclerView()

        viewModel.freshNews.observe(viewLifecycleOwner) { result ->
            if (result != null)
            {
                when (result) {
                    is Result.Loading -> {
                        binding.rvBreakingNews.visibility = View.GONE
                        showNetworkErrorMsg(false)
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.rvBreakingNews.visibility = View.VISIBLE
                        showNetworkErrorMsg(false)
                        binding.progressBar.visibility = View.GONE

                        val articles = result.data
                        articleCardAdapter.submitList(articles)
                    }
                    is Result.Error -> {
                        binding.rvBreakingNews.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        showNetworkErrorMsg(true)
                        Log.e("HomeFragment", "ERROR: ${result.error}")
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showNetworkErrorMsg(state: Boolean) {
        binding.ivNoNetwork.visibility = if (state) View.VISIBLE else View.GONE
        binding.tvNoNetwork.visibility = if (state) View.VISIBLE else View.GONE
        binding.btnRefresh.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun createCategoryChip(category: String): Chip {
        val chip = Chip(context)
        val chipDrawable = context?.let { it -> ChipDrawable.createFromAttributes(it, null, 0, com.google.android.material.R.style.Widget_Material3_Chip_Suggestion) }
        chip.setChipDrawable(chipDrawable!!)
        chip.setPadding(0, 0, 16, 0)
        chip.text = category
        chip.isCheckable = true
        binding.cgCategories.addView(chip)

        return chip
    }

    private fun showCountryList(): AlertDialog.Builder? {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Select a country")
        builder?.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        return builder
    }

    private fun extractCountryCode(countryString: String): String? {
        val pattern = "\\((.*?)\\)".toRegex()
        val matchResult = pattern.find(countryString)
        return matchResult?.groupValues?.get(1)
    }

    private fun setupRecyclerView() {
        articleCardAdapter = ArticleCardAdapter { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_articleFragment,
                bundle
            )
        }

        binding.rvBreakingNews.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = articleCardAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}