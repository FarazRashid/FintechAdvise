package com.se.fintechadvise.HelperClasses

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.se.fintechadvise.Fragments.HomeFragment
import com.se.fintechadvise.R


class BottomNavigationHelper(private val activity: AppCompatActivity) {

    fun setUpBottomNavigation() {
        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    Log.d("BottomNavigationHelper", "Home Fragment loaded")
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
}
