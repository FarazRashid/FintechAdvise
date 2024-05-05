package com.se.fintechadvise.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Article
import com.se.fintechadvise.R

class ArticleAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_articles, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        (position + 1).toString().also { holder.number.text = it }
        holder.articleName.text = article.title
        holder.articleCompletion.text = article.completion
    }

    override fun getItemCount() = articles.size

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.number)
        val imageView: ImageView = view.findViewById(R.id.imageView7)
        val articleName: TextView = view.findViewById(R.id.articleName)
        val articleCompletion: TextView = view.findViewById(R.id.articleCompletion)
    }
}