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
        knowledgeRecyclerView?.adapter = ArticleAdapter(articleList)
    }

    private fun getArticles() {
        val difficulties = listOf("Easy", "Medium", "Hard")
        for (i in 1..5) {
            val difficulty = difficulties[Random.nextInt(difficulties.size)]
            val completion = "${Random.nextInt(100)}%"
            articleList.add(Article("Article $i", "Description $i", difficulty, "10 minutes", completion))
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