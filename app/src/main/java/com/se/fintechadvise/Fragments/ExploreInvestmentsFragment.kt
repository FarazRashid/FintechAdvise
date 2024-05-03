package com.se.fintechadvise.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.se.fintechadvise.AdapterClasses.InvestmentAdapter
import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.DataClasses.InvestmentPerformance
import com.se.fintechadvise.DataClasses.InvestmentType
import com.se.fintechadvise.DataClasses.Plans
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.R
import com.smd.surmaiya.Fragments.InvestmentProfileFragment

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
        )
    }

    private fun setupRecyclerView(view: View) {
        val investments: List<Investment> = getInvestments() // Replace with your actual implementation

        val allInvestmentsRecyclerView = view.findViewById<RecyclerView>(R.id.allInvestmentsRecyclerView)
        allInvestmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        allInvestmentsRecyclerView.adapter = InvestmentAdapter(investments, object : InvestmentAdapter.OnInvestmentClickListener {
            override fun onInvestmentClick(investment: Investment) {
                // Handle click event
//                FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(InvestmentProfileFragment())
                showPlayerBottomSheetDialog()

            }
        })

        val recommendedInvestmentsRecyclerView = view.findViewById<RecyclerView>(R.id.recommendedInvestmentsRecyclerView)
        recommendedInvestmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recommendedInvestmentsRecyclerView.adapter = InvestmentAdapter(investments, object : InvestmentAdapter.OnInvestmentClickListener {
            override fun onInvestmentClick(investment: Investment) {
                // Handle click event
//                FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(InvestmentProfileFragment())
                showPlayerBottomSheetDialog()

            }
        })

    }
    private fun showPlayerBottomSheetDialog() {
        val investmentProfileFragment = InvestmentProfileFragment()
        investmentProfileFragment.show(requireActivity().supportFragmentManager, investmentProfileFragment.tag)
        Log.d("InvestmentProfileFragment", "showPlayerBottomSheetDialog: ")
    }
    private fun getInvestments(): List<Investment> {
            val investments = listOf(
            Investment(
                id = "1",
                name = "Investment 1",
                allocation = 50.0,
                type = InvestmentType.STOCK, // Replace with actual type
                currentValue = 10.0,
                investmentImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png",
                historicalPerformance = listOf(InvestmentPerformance("22/10/2024",25.0)
                    ,InvestmentPerformance("10/10/2024",23.0)),
                performanceIndex = 1.0
            ),
            Investment(
                id = "2",
                name = "Investment 2",
                allocation = 30.0,
                type = InvestmentType.STOCK, // Replace with actual type
                currentValue = 20.0,
                investmentImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png",
                historicalPerformance = listOf(InvestmentPerformance("22/10/2024",25.0)
                    ,InvestmentPerformance("10/10/2024",23.0)),
                performanceIndex = 1.2
            ),
                Investment(
                id = "3",
                name = "Investment 3",
                allocation = 20.0,
                type = InvestmentType.STOCK, // Replace with actual type
                currentValue = 30.0,
                investmentImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png",
                historicalPerformance = listOf(InvestmentPerformance("22/10/2024",25.0)
                    ,InvestmentPerformance("10/10/2024",23.0)),
                performanceIndex = 1.3
            )
            // Add more investments as needed
        )

        return investments
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explore_investments, container, false)
        setupMenuOpener(view)
        setupRecyclerView(view)
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