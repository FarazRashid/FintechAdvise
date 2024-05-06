package com.se.fintechadvise.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.AdapterClasses.ArticleAdapter
import com.se.fintechadvise.DataClasses.Article
import com.se.fintechadvise.R
import kotlin.random.Random


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var articleList = mutableListOf<Article>()
    private lateinit var articleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var noTextFoundView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment first
        val view = inflater.inflate(R.layout.fragment_search_results, container, false)

        val searchquery = arguments?.getString("SearchQuery")

        // Initialize the views after the layout is inflated
        recyclerView = view.findViewById(R.id.lessonsRecyclerView)
        noTextFoundView = view.findViewById(R.id.noResultsFoundTextView)
        articleTextView = view.findViewById(R.id.articlesTextView)

        setupArticleRecyclerView(view, searchquery)

        return view
    }

    private fun setupArticleRecyclerView(view: View?, searchQuery: String?) {
        getArticles(searchQuery ?: "")


        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView?.adapter = ArticleAdapter(articleList)
    }

    private fun getArticles(searchQuery: String) {
        val difficulties = listOf("Easy", "Medium", "Hard")
        for (i in 1..5) {
            val difficulty = difficulties[Random.nextInt(difficulties.size)]
            val completion = "${Random.nextInt(100)}%"
            articleList.add(Article("Article $i", "Description $i", difficulty, "10 minutes", completion))
        }

        // Filter the articles based on the search query
        articleList = articleList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }.toMutableList()

        //if no articles are found make articletextiview gone, make recylcer gone and show no results found textview
        if (articleList.isEmpty()) {
            articleTextView.visibility = View.GONE
            recyclerView.visibility = View.GONE
            noTextFoundView.visibility = View.VISIBLE
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String) =
            SearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putString("SearchQuery", param1)
                }
            }
    }
}