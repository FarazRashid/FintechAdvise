package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.os.TokenWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Badge
import com.se.fintechadvise.R
import com.se.fintechadvise.AdapterClasses.BadgesAdapter
import com.se.fintechadvise.AdapterClasses.OnBadgeClickListener
import org.w3c.dom.Text
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.ManagerClasses.PersonalisedLearningManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EducationHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EducationHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val badges = ArrayList<Badge>()
    private lateinit var badgesRecycler: RecyclerView
    private lateinit var yellowWalletNumberOfLectures:TextView
    private lateinit var yellowWalletNumberOfCourses:TextView
    private lateinit var yellowWalletTopic:TextView

    private lateinit var blueWalletNumberOfLectures:TextView
    private lateinit var blueWalletNumberOfCourses:TextView
    private lateinit var blueWalletTopic:TextView

    private lateinit var redWalletNumberOfLectures:TextView
    private lateinit var redWalletNumberOfCourses:TextView
    private lateinit var redWalletTopic:TextView

    private lateinit var blueWallet:ImageView
    private lateinit var yellowWallet:ImageView
    private lateinit var redWallet:ImageView

    private var redWalletTopicText: String= "Budgeting"
    private var blueWalletTopicText: String= "Investments"
    private var yellowWalletTopicText: String = "Finances"

    private  lateinit var searchView:SearchView



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
        val view = inflater.inflate(R.layout.fragment_education_home, container, false)
        checkPersonalisedLearning(view)

        badgesRecycler = view.findViewById<RecyclerView>(R.id.gridLayout)

        initBadges()
        initializeViews(view)

        return view
    }

    private fun checkPersonalisedLearning(view:View) {
        if(PersonalisedLearningManager.isPersonalisedLearningEnabled()){
            view.findViewById<TextView>(R.id.textView200).visibility = View.GONE
            view.findViewById<ImageButton>(R.id.personalisedLearningButton).visibility = View.GONE
            view.findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.GONE
            view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
        }
        else{
            view.findViewById<TextView>(R.id.textView200).visibility = View.VISIBLE
            view.findViewById<ImageButton>(R.id.personalisedLearningButton).visibility = View.VISIBLE
            view.findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.VISIBLE
            view.findViewById<CardView>(R.id.cardView).visibility = View.VISIBLE
        }
    }

    private fun setupPersonalizedLearningQuestionare(view:View?) {
       view?.findViewById<ImageButton>(R.id.personalisedLearningButton)?.setOnClickListener {
            val fragment = PersonalisedLearningQuestionareFragment()
            FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(fragment)

       }
    }

    private fun initializeViews(view: View){

        yellowWalletTopic= view.findViewById(R.id.yellowWalletTopic)
        yellowWalletNumberOfLectures = view.findViewById(R.id.yellowWalletLectures)
        yellowWalletNumberOfCourses = view.findViewById(R.id.yellowWalletNumberOfCourses)

        blueWalletTopic= view.findViewById(R.id.blueWalletCourse)
        blueWalletNumberOfLectures= view.findViewById(R.id.blueWalletNumberOfLectures)
        blueWalletNumberOfCourses= view.findViewById(R.id.blueWalletNumberOfCourses)

        redWalletTopic=view.findViewById(R.id.redWalletTopic)
        redWalletNumberOfCourses=view.findViewById((R.id.redWalletNumberOfLectures))
        redWalletNumberOfLectures=view.findViewById(R.id.redWalletNmberOfLectures)

        blueWallet=view.findViewById(R.id.bluewallet)
        yellowWallet=view.findViewById(R.id.imageView6)
        redWallet=view.findViewById(R.id.imageView7)

        searchView=view.findViewById(R.id.searchView)

        //add on click listeners for red, yellow, annd blue wallets

        redWallet.setOnClickListener {
            val courseDetailFragment = CourseDetailFragment.newInstance(redWalletTopicText)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, courseDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        blueWallet.setOnClickListener {
            val courseDetailFragment = CourseDetailFragment.newInstance(blueWalletTopicText)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, courseDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        yellowWallet.setOnClickListener {
            val courseDetailFragment = CourseDetailFragment.newInstance(yellowWalletTopicText)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, courseDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val courseDetailFragment = SearchResultsFragment.newInstance(query!!)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, courseDetailFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        setupPersonalizedLearningQuestionare(view)

    }



    private fun initBadges() {
        badges.add(Badge("All", false))
        badges.add(Badge("Billings", false))
        badges.add(Badge("Investments", true)) // This badge is selected
        badges.add(Badge("Stocks", false))
        badges.add(Badge("Banking", false))

        badgesRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        badgesRecycler.adapter = BadgesAdapter(badges).apply {
            setBadgeClickListener(object : OnBadgeClickListener {
                override fun onBadgeClick(position: Int) {
                    Log.d("MainActivity", "Badge clicked at position: $position")
                    var name=badges[position].name

                    when(name)
                    {
                        "Billings"->{

                            redWalletTopicText = "Personal Finance"
                            blueWalletTopicText = "Corporate Finance"
                            yellowWalletTopicText = "Public Finance"


                            redWalletTopic.text="Personal Finance"
                            redWalletNumberOfCourses.text="10"
                            redWalletNumberOfLectures.text="50 Lectures"

                            blueWalletTopic.text="Corporate Finance"
                            blueWalletNumberOfCourses.text="8"
                            blueWalletNumberOfLectures.text="40 Lectures"

                            yellowWalletTopic.text="Public Finance"
                            yellowWalletNumberOfCourses.text="7"
                            yellowWalletNumberOfLectures.text="35 Lectures"
                        }

                        "Investments"->{

                            redWalletTopicText = "Equity Investments"
                            blueWalletTopicText = "Fixed Income Investments"
                            yellowWalletTopicText = "Derivative Investments"

                            redWalletTopic.text="Equity Investments"
                            redWalletNumberOfCourses.text="12"
                            redWalletNumberOfLectures.text="60 Lectures"

                            blueWalletTopic.text="Fixed Income"
                            blueWalletNumberOfCourses.text="10"
                            blueWalletNumberOfLectures.text="50 Lectures"

                            yellowWalletTopic.text="Derivative"
                            yellowWalletNumberOfCourses.text="9"
                            yellowWalletNumberOfLectures.text="45 Lectures"
                        }
                        "Stocks"->{

                            redWalletTopicText = "Stock Basics"
                            blueWalletTopicText = "Stock Analysis"
                            yellowWalletTopicText = "Stock Trading"

                            redWalletTopic.text="Stock Basics"
                            redWalletNumberOfCourses.text="15"
                            redWalletNumberOfLectures.text="75 Lectures"

                            blueWalletTopic.text="Stock Analysis"
                            blueWalletNumberOfCourses.text="13"
                            blueWalletNumberOfLectures.text="65 Lectures"

                            yellowWalletTopic.text="Stock Trading"
                            yellowWalletNumberOfCourses.text="11"
                            yellowWalletNumberOfLectures.text="55 Lectures"
                        }

                        "Banking"->{

                            redWalletTopicText = "Retail Banking"
                            blueWalletTopicText = "Corporate Banking"
                            yellowWalletTopicText = "Investment Banking"


                            redWalletTopic.text="Retail Banking"
                            redWalletNumberOfCourses.text="14"
                            redWalletNumberOfLectures.text="70 Lectures"

                            blueWalletTopic.text="Corporate Banking"
                            blueWalletNumberOfCourses.text="12"
                            blueWalletNumberOfLectures.text="60 Lectures"

                            yellowWalletTopic.text="Investment Banking"
                            yellowWalletNumberOfCourses.text="10"
                            yellowWalletNumberOfLectures.text="50 Lectures"
                        }
                    }


                }
            })
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EducationHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EducationHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}