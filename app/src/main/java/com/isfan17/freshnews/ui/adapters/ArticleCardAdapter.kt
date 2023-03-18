package com.isfan17.freshnews.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.isfan17.freshnews.R
import com.isfan17.freshnews.data.model.Article
import com.isfan17.freshnews.databinding.ItemArticleCardBinding
import com.isfan17.freshnews.helper.DateFormatter

class ArticleCardAdapter(private val onItemClicked: (Article) -> Unit): ListAdapter<Article, ArticleCardAdapter.ArticleViewHolder>(DiffCallback) {

    inner class ArticleViewHolder(private var binding: ItemArticleCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.apply {
                tvSource.text = article.source
                tvTitle.text = article.title
                tvPublishedAt.text = DateFormatter.formatDate(article.publishedAt)
            }
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // The image has been loaded successfully, hide the progress bar
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // The image failed to load, hide the progress bar
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .placeholder(R.drawable.freshnews_placeholder)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url === newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }
}