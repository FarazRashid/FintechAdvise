package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.se.fintechadvise.AdapterClasses.ArticleAdapter
import com.se.fintechadvise.AdapterClasses.PlanAdapter
import com.se.fintechadvise.DataClasses.Article
import com.se.fintechadvise.DataClasses.Plans
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.ManagerClasses.ArticleManager
import com.se.fintechadvise.R
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InvestmentPortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvestmentPortfolioFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var articleList = mutableListOf<Article>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun setupMenuOpener(view: View) {
        val menuOpener = view.findViewById<ImageView>(R.id.menu_opener)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)

        menuOpener.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            setupNavigationView()
        }
    }

    private fun setupNavigationView() {
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.side_nav)
        val fragmentHelper = FragmentHelper(requireActivity().supportFragmentManager, requireContext())
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingsButton -> true
                R.id.monthlyRankingsButton -> true
                R.id.popularPLaylistsButton -> {
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(SettingsFragment())
                    }, 300)

                    true
                }
                R.id.notificataionsButton -> true
                else -> false
            }
        }
    }

    private fun getPlans(): List<Plans> {
        return listOf(
            Plans("Platinum", "Invest in mutual funds", "@drawable/investment.xml", "200"),
            Plans("Gold", "Invest in stocks", "@drawable/investment.xml", "130"),
            Plans("Silver", "Invest in bonds", "@drawable/investment.xml", "50"),
            Plans("Bronze", "Invest in estate", "@drawable/investment.xml", "20")
        )
    }

    private fun setupRecyclerView(view: View) {
        val plans: List<Plans> = getPlans()

        val recyclerView = view.findViewById<RecyclerView>(R.id.plansRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = PlanAdapter(plans)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_investment_portfolio, container, false)

        setupRecyclerView(view)
        setupKnowledgeRecyclerView(view)
        setupButtonNavigation(view)
        setupMenuOpener(view)

        return view
    }

    private fun setupKnowledgeRecyclerView(view: View?) {
        getArticles()
        val knowledgeRecyclerView = view?.findViewById<RecyclerView>(R.id.knowledgeRecyclerView)
        knowledgeRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        knowledgeRecyclerView?.adapter = ArticleAdapter(articleList, object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, article: Article) {
                ArticleManager.setCurrentArticle(article)
                val articleFragment = ArticleFragment()
                FragmentHelper(requireActivity().supportFragmentManager, requireContext()).loadFragment(articleFragment)
            }
        })
    }

    private fun getArticles() {
        val difficulties = listOf("Easy", "Medium", "Hard")
        for (i in 1..5) {
            val difficulty = difficulties[Random.nextInt(difficulties.size)]
            val completion = "${Random.nextInt(100)}%"
            val title = when(i) {
                1 -> "Introduction to Stock Market Investing"
                2 -> "A Guide to Asset Allocation"
                3 -> "Understanding Mutual Funds"
                4 -> "Real Estate Investment Strategies for Beginners"
                5 -> "Navigating Cryptocurrency Investments"
                else -> "Unknown Title"
            }
            val description = when(i) {
                1 -> "Discover the basics of stock market investing, including how stocks work, key investment principles, and factors to consider before investing. Learn how to research stocks, evaluate risk, and build a diversified portfolio to achieve your financial goals."
                2 -> "Explore the importance of diversification in investment portfolios and learn effective asset allocation strategies. Understand the risk-return tradeoff, correlation between asset classes, and techniques to spread risk while maximizing returns for long-term financial growth."
                3 -> "Dive into the world of mutual funds with this comprehensive guide. Learn about different types of mutual funds, including index funds, actively managed funds, and ETFs. Understand the benefits of mutual fund investing, such as diversification, professional management, and liquidity."
                4 -> "Embark on your real estate investment journey with this beginner's guide. Explore various real estate investment options, such as rental properties, REITs, and crowdfunding platforms. Learn how to analyze real estate markets, evaluate properties, and generate passive income through real estate investing."
                5 -> "Get acquainted with the world of cryptocurrencies and blockchain technology. Understand how cryptocurrencies work, different types of cryptocurrencies, and factors influencing their prices. Explore investment opportunities in cryptocurrencies, along with risks and regulations associated with this emerging asset class."
                else -> "No description available"
            }
            articleList.add(Article(title, description, difficulty, "10 minutes", completion))
        }
    }

    private fun setupButtonNavigation(view: View?) {
        val goToExploreButton = view?.findViewById<Button>(R.id.investNowButton)
        val fragmentHelper = FragmentHelper(requireActivity().supportFragmentManager, requireContext())
        goToExploreButton?.setOnClickListener {
            fragmentHelper.loadFragment(ExploreInvestmentsFragment())
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InvestmentPortfolioFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InvestmentPortfolioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}