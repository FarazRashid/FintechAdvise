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
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.ManagerClasses.ArticleManager
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
        recyclerView?.adapter = ArticleAdapter(articleList,object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, article: Article) {
                ArticleManager.setCurrentArticle(article)
                val articleFragment = ArticleFragment()
                FragmentHelper(requireActivity().supportFragmentManager, requireContext()).loadFragment(articleFragment)
            }
        })
    }

//    private fun getArticles(searchQuery: String) {
//        val difficulties = listOf("Easy", "Medium", "Hard")
//        for (i in 1..5) {
//            val difficulty = difficulties[Random.nextInt(difficulties.size)]
//            val completion = "${Random.nextInt(100)}%"
//            articleList.add(Article("Article $i", "Description $i", difficulty, "10 minutes", completion))
//        }
//
//        // Filter the articles based on the search query
//        articleList = articleList.filter {
//            it.title.contains(searchQuery, ignoreCase = true) ||
//                    it.description.contains(searchQuery, ignoreCase = true)
//        }.toMutableList()
//
//        //if no articles are found make articletextiview gone, make recylcer gone and show no results found textview
//        if (articleList.isEmpty()) {
//            articleTextView.visibility = View.GONE
//            recyclerView.visibility = View.GONE
//            noTextFoundView.visibility = View.VISIBLE
//        }
//    }

    private fun getArticles(searchQuery: String) {
        val difficulties = listOf("Easy", "Medium", "Hard")
        for (i in 1..10) {
            val difficulty = difficulties[Random.nextInt(difficulties.size)]
            val completion = "${Random.nextInt(100)}%"
            val title = when(i) {
                1 -> "The Fundamentals of Budgeting"
                2 -> "Strategies for Debt Management"
                3 -> "Exploring Investment Options"
                4 -> "Understanding Credit Scores"
                5 -> "Mastering Retirement Planning"
                6 -> "The Basics of Personal Finance"
                7 -> "Building an Emergency Fund"
                8 -> "Navigating Student Loans"
                9 -> "Planning for Major Life Events"
                10 -> "Achieving Financial Independence"
                else -> "Unknown Title"
            }
            val description = when(i) {
                1 -> "This comprehensive article delves into the fundamentals of budgeting, including creating a budget, tracking expenses, setting financial goals, and developing strategies to save money effectively. Learn practical tips to manage your finances efficiently and achieve financial stability."
                2 -> "In this in-depth guide, discover proven strategies for effectively managing and reducing debt. Explore techniques such as debt consolidation, prioritizing payments, negotiating with creditors, and adopting responsible spending habits to regain control of your finances and pave the way for a debt-free future."
                3 -> "Navigate the complex world of investment with this detailed exploration of various options, including stocks, bonds, mutual funds, real estate, and more. Gain insights into risk assessment, diversification strategies, portfolio management techniques, and long-term investment planning to make informed decisions and optimize your financial growth."
                4 -> "Unlock the mysteries of credit scores and their impact on your financial well-being in this comprehensive article. Learn how credit scores are calculated, factors affecting them, and strategies to improve your score. Discover the importance of maintaining good credit and how it influences loan approvals, interest rates, and overall financial health."
                5 -> "Prepare for a secure and fulfilling retirement with this expert guide to retirement planning. Explore topics such as pension plans, 401(k)s, IRAs, annuities, and Social Security benefits. Learn how to estimate retirement expenses, create a retirement income strategy, mitigate risks, and adapt your plan over time to enjoy a comfortable and worry-free retirement."
                6 -> "This article provides an overview of personal finance basics, including budgeting, saving, investing, and managing debt. Learn essential financial concepts, such as compound interest, inflation, and risk management, to build a solid foundation for your financial future."
                7 -> "Discover the importance of building an emergency fund and how it can protect you from unexpected financial setbacks. Learn how to calculate the ideal emergency fund size, where to keep your emergency savings, and strategies to grow your fund over time. Be prepared for life's uncertainties with a well-funded emergency account."
                8 -> "Navigate the complexities of student loans with this comprehensive guide to borrowing for higher education. Learn about different types of student loans, repayment options, interest rates, and loan forgiveness programs. Get tips on managing student loan debt, avoiding default, and planning for a successful financial future."
                9 -> "Prepare for major life events, such as buying a home, getting married, having children, or starting a business, with this informative article. Learn how to set financial goals, create a budget, and develop a plan to achieve your dreams. Get tips on saving, investing, and protecting your assets as you navigate life's milestones."
                10 -> "Achieve financial independence and take control of your financial future with this expert guide. Learn how to build wealth, reduce debt, and increase your income to achieve financial freedom. Discover strategies for saving, investing, and managing your money to reach your long-term financial goals."

                else -> "No description available"
            }
            articleList.add(Article(title, description, difficulty, "10 minutes", completion))
        }

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