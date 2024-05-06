package com.se.fintechadvise.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.se.fintechadvise.Activities.PrivacyPolicy
import com.se.fintechadvise.AdapterClasses.ArticleAdapter
import com.se.fintechadvise.DataClasses.Article
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.ManagerClasses.ArticleManager
import com.se.fintechadvise.R
import org.w3c.dom.Text
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var articleList = mutableListOf<Article>()
    private lateinit var courseTitle:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun setupBackButton(view: View) {
        val backButton = view.findViewById<ImageView>(R.id.backButtonTran)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_detail, container, false)
        setupBackButton(view)
        setupUIElements(view)
        setupArticleRecyclerView(view)

        val title = arguments?.getString("title")
        courseTitle.text = title

        return view
    }


    private fun setupUIElements(view:View?){
        val CompletionPercent = view?.findViewById<TextView>(R.id.CompletionPercent)
        val Difficulty = view?.findViewById<TextView>(R.id.difficultyTextView)
        val EstimatedTime = view?.findViewById<TextView>(R.id.estimatedTimeTextView)
        val progressText = view?.findViewById<TextView>(R.id.progressText)
        val progress = view?.findViewById<ProgressBar>(R.id.progressBar)

        courseTitle = view?.findViewById(R.id.textsoadnfoaView17)!!

        // random values for all the fields

        val completion = "${Random.nextInt(100)}% Completed"
        val difficulty = listOf("Easy", "Medium", "Hard")[Random.nextInt(3)]
        val estimatedTime = "${Random.nextInt(10, 100)} mins"

        CompletionPercent?.text = completion
        Difficulty?.text = difficulty
        EstimatedTime?.text = estimatedTime
        progressText?.text = completion.split("%")[0].toInt().toString() + "%"
        progress?.progress = completion.split("%")[0].toInt()



    }
    private fun setupArticleRecyclerView(view: View?) {
        getArticles()

        val recyclerView = view?.findViewById<RecyclerView>(R.id.lessonsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView?.adapter = ArticleAdapter(articleList, object : ArticleAdapter.OnItemClickListener {
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
                1 -> "The Fundamentals of Budgeting"
                2 -> "Strategies for Debt Management"
                3 -> "Exploring Investment Options"
                4 -> "Understanding Credit Scores"
                5 -> "Mastering Retirement Planning"
                else -> "Unknown Title"
            }
            val description = when(i) {
                1 -> "This comprehensive article delves into the fundamentals of budgeting, including creating a budget, tracking expenses, setting financial goals, and developing strategies to save money effectively. Learn practical tips to manage your finances efficiently and achieve financial stability."
                2 -> "In this in-depth guide, discover proven strategies for effectively managing and reducing debt. Explore techniques such as debt consolidation, prioritizing payments, negotiating with creditors, and adopting responsible spending habits to regain control of your finances and pave the way for a debt-free future."
                3 -> "Navigate the complex world of investment with this detailed exploration of various options, including stocks, bonds, mutual funds, real estate, and more. Gain insights into risk assessment, diversification strategies, portfolio management techniques, and long-term investment planning to make informed decisions and optimize your financial growth."
                4 -> "Unlock the mysteries of credit scores and their impact on your financial well-being in this comprehensive article. Learn how credit scores are calculated, factors affecting them, and strategies to improve your score. Discover the importance of maintaining good credit and how it influences loan approvals, interest rates, and overall financial health."
                5 -> "Prepare for a secure and fulfilling retirement with this expert guide to retirement planning. Explore topics such as pension plans, 401(k)s, IRAs, annuities, and Social Security benefits. Learn how to estimate retirement expenses, create a retirement income strategy, mitigate risks, and adapt your plan over time to enjoy a comfortable and worry-free retirement."
                else -> "No description available"
            }
            articleList.add(Article(title, description, difficulty, "10 minutes", completion))
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CourseDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String) =
            CourseDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}