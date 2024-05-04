package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.se.fintechadvise.AdapterClasses.InvestmentAdapter
import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.ManagerClasses.InvestmentManager
import com.se.fintechadvise.ManagerClasses.UserManager
import com.se.fintechadvise.ManagerClasses.WebserviceManger
import com.se.fintechadvise.R
import com.smd.surmaiya.Fragments.InvestmentProfileFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YourInvestmentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourInvestmentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var shimmerContainer: ShimmerFrameLayout

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_your_investments, container, false)

        shimmerContainer = view.findViewById(R.id.shimmer_view_container)
        lifecycleScope.launch {
            getYourInvestments()
            delay(5000) // Delay for 500 milliseconds. Adjust this value as needed.
        }

        return view
    }

    private fun setupRecyclerView(view: View, investments: List<Investment>) {

        Log.d("Investment", "setupRecyclerView: $investments")

        val yourInvestmentsRecyclerView = view.findViewById<RecyclerView>(R.id.recommendedInvestmentsRecyclerView)
        yourInvestmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        yourInvestmentsRecyclerView.adapter = InvestmentAdapter(investments, object : InvestmentAdapter.OnInvestmentClickListener {
            override fun onInvestmentClick(investment: Investment) {
                // Handle click event
//                FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(InvestmentProfileFragment())
                InvestmentManager.setCurrentInvestment(investment)
                showPlayerBottomSheetDialog()

            }
        })

    }

    private fun showPlayerBottomSheetDialog() {
        val investmentProfileFragment = InvestmentProfileFragment()
        investmentProfileFragment.show(requireActivity().supportFragmentManager, investmentProfileFragment.tag)
        Log.d("InvestmentProfileFragment", "showPlayerBottomSheetDialog: ")
    }

    private fun getYourInvestments(){
        Log.d("Investment", "gettingInvestments")
        shimmerContainer.startShimmer()
        WebserviceManger.getUserInvestments(requireContext(), UserManager.getCurrentUser()!!.id) { userInvestments, error ->
            if (userInvestments != null) {
                Log.d("Your Investment", "Investment IDs: $userInvestments")
                val userInvestmentIds = userInvestments.map { it.first } // Extract only the IDs

                val allInvestments = InvestmentManager.getInvestments()
                Log.d("Your Investment", "All Investments: $allInvestments")
                val usersInvestments = allInvestments.filter { it.id in userInvestmentIds }

                Log.d("Your Investment", "User Investments: $usersInvestments")

                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE

                view?.let { setupRecyclerView(it, usersInvestments) }
            } else {
                Log.e("Investment", "Error getting user investments: $error")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YourInvestmentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YourInvestmentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}