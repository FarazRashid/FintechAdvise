package com.se.fintechadvise.HelperClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.se.fintechadvise.R

class FragmentHelper(private val fragmentManager: FragmentManager) {

    fun loadFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}