package com.se.fintechadvise.HelperClasses

import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.se.fintechadvise.Fragments.CourseDetailFragment
import com.se.fintechadvise.Fragments.EducationHomeFragment
import com.se.fintechadvise.Fragments.ExploreInvestmentsFragment
import com.se.fintechadvise.Fragments.HomeFragment
import com.se.fintechadvise.Fragments.InvestmentPortfolioFragment
import com.se.fintechadvise.Fragments.PlanningFragment
import com.se.fintechadvise.R


class BottomNavigationHelper(private val activity: AppCompatActivity) {

    fun setUpBottomNavigation() {
        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    //if not current
                    if(activity.supportFragmentManager.findFragmentById(R.id.fragment_container) !is HomeFragment) {
                        loadFragment(HomeFragment())
                    }
                    Log.d("BottomNavigationHelper", "Home Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_investment -> {
                    if (activity.supportFragmentManager.findFragmentById(R.id.fragment_container) !is InvestmentPortfolioFragment) {
                        loadFragment(InvestmentPortfolioFragment())
                    }
                    Log.d("BottomNavigationHelper", "Investment Portfolio Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_goals -> {
                    if(activity.supportFragmentManager.findFragmentById(R.id.fragment_container) !is PlanningFragment) {
                        loadFragment(PlanningFragment())
                    }
//                    loadFragment(PlanningFragment())
                    Log.d("BottomNavigationHelper", "Explorations Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_education -> { loadFragment(ExploreInvestmentsFragment())
                    Log.d("BottomNavigationHelper", "Education Fragment loaded")
                    if (activity.supportFragmentManager.findFragmentById(R.id.fragment_container) !is EducationHomeFragment) {
                        loadFragment(EducationHomeFragment())
                    }
//                    loadFragment(CourseDetailFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        Log.d("BottomNavigationHelper", "Fragment loaded: ${fragment.javaClass.simpleName}")
    }

    fun setupVisibilityToggleWithTransition(trigger: TextView, container: ViewGroup, vararg views: View) {
        trigger.setOnClickListener {
            TransitionManager.beginDelayedTransition(container)
            views.forEach { view ->
                view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }
}
