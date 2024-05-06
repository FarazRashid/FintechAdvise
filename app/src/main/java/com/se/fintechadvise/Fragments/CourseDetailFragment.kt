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

        return view
    }


    private fun setupUIElements(view:View?){
        val CompletionPercent = view?.findViewById<TextView>(R.id.CompletionPercent)
        val Difficulty = view?.findViewById<TextView>(R.id.difficultyTextView)
        val EstimatedTime = view?.findViewById<TextView>(R.id.estimatedTimeTextView)
        val progressText = view?.findViewById<TextView>(R.id.progressText)
        val progress = view?.findViewById<ProgressBar>(R.id.progressBar)

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
            articleList.add(Article("Article $i", "Description $i", difficulty, "10 minutes", completion))
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
        fun newInstance(param1: String, param2: String) =
            CourseDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}