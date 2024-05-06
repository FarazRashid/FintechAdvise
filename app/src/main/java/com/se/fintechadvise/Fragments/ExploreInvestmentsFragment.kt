package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.se.fintechadvise.AdapterClasses.InvestmentAdapter
import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.DataClasses.InvestmentPerformance
import com.se.fintechadvise.DataClasses.InvestmentType
import com.se.fintechadvise.DataClasses.Plans
import com.se.fintechadvise.DataClasses.User
import com.se.fintechadvise.HelperClasses.FragmentHelper
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
 * Use the [ExploreInvestmentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExploreInvestmentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var shimmerContainer: ShimmerFrameLayout
    private lateinit var shimmerContainer1: ShimmerFrameLayout


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

    private fun setupYourInvestmentsButton(view:View){
        val yourInvestmentsButton = view.findViewById<ImageButton>(R.id.yourInvestmentsButton)
        yourInvestmentsButton.setOnClickListener {
            FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(YourInvestmentsFragment())
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


    private fun setupRecyclerView(view: View, investments: List<Investment>) {

        Log.d("Investment", "setupRecyclerView: $investments")
        InvestmentManager.setInvestments(investments)

        val allInvestmentsRecyclerView = view.findViewById<RecyclerView>(R.id.allInvestmentsRecyclerView)
        allInvestmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        allInvestmentsRecyclerView.adapter = InvestmentAdapter(investments, object : InvestmentAdapter.OnInvestmentClickListener {
            override fun onInvestmentClick(investment: Investment) {
                InvestmentManager.setCurrentInvestment(investment)
                showPlayerBottomSheetDialog()
            }
        })

        var filteredInvestments = investments

        UserManager.getCurrentUser()?.let { user ->
            // Filter investments based on user tolerance, if investment.performance index is greater than user tolerance show that to the user
            filteredInvestments = investments.filter { investment ->
                val investmentPerformance = investment.performanceIndex
                val userTolerance = user.riskTolerance

                if (investmentPerformance != null) {
                    if (investmentPerformance > userTolerance.toDouble()) {
                        return@filter true
                    }
                }
                return@filter false
            }
        }

        val recommendedInvestmentsRecyclerView = view.findViewById<RecyclerView>(R.id.recommendedInvestmentsRecyclerView)
        recommendedInvestmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recommendedInvestmentsRecyclerView.adapter = InvestmentAdapter(filteredInvestments, object : InvestmentAdapter.OnInvestmentClickListener {
            override fun onInvestmentClick(investment: Investment) {
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
    private fun getInvestments() {

        Log.d("Investment", "gettingInvestments")
        shimmerContainer.startShimmer()
        shimmerContainer1.startShimmer()
        var investments1= listOf<Investment>()
        WebserviceManger.getInvestments(requireContext()) { investments,string ->
            if (investments != null) {
                investments1=investments
            }

            for (investment in investments1) {
                Log.d("Investment", "Investment: $investment")
            }

            shimmerContainer.stopShimmer()
            shimmerContainer.visibility = View.GONE

            shimmerContainer1.stopShimmer()
            shimmerContainer1.visibility = View.GONE


            view?.let { setupRecyclerView(it,investments1) }

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explore_investments, container, false)

        setupYourInvestmentsButton(view)
        shimmerContainer = view.findViewById(R.id.shimmer_view_container)
        shimmerContainer1 = view.findViewById(R.id.shimmer_view_container1)

        setupMenuOpener(view)
        // Start a coroutine to fetch the data
        lifecycleScope.launch {
            getInvestments()
            delay(5000) // Delay for 500 milliseconds. Adjust this value as needed.
        }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreInvestmentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreInvestmentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}