package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Badge
import com.se.fintechadvise.R
import com.se.fintechadvise.AdapterClasses.BadgesAdapter
import com.se.fintechadvise.AdapterClasses.OnBadgeClickListener
import com.se.fintechadvise.HelperClasses.FragmentHelper

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
        badgesRecycler = view.findViewById<RecyclerView>(R.id.gridLayout)
        initBadges()

        view.findViewById<ImageButton>(R.id.personalisedLearningButton).setOnClickListener {
            Log.d("MainActivity", "Personalised Learning Button clicked")
            FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(CourseDetailFragment())
        }

        return view
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