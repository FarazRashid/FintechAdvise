package com.se.fintechadvise.HelperClasses

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.se.fintechadvise.R

class FragmentHelper(private val fragmentManager: FragmentManager, private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("checkbox_states", Context.MODE_PRIVATE)


    fun loadFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun setupVisibilityToggleWithTransition(trigger: TextView, container: ViewGroup, vararg views: View) {
        trigger.setOnClickListener {
            TransitionManager.beginDelayedTransition(container)
            views.forEach { view ->
                view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }

    fun setupCheckboxListener(vararg checkboxes: CheckBox, button: Button) {
        var isFirstChange = true
        checkboxes.forEach { checkbox ->
            // Restore checkbox state from SharedPreferences
            checkbox.isChecked = sharedPreferences.getBoolean(checkbox.id.toString(), false)

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                button.visibility = View.VISIBLE
                if (isFirstChange) {
                    val fadeIn = ObjectAnimator.ofFloat(button, "alpha",  0f, 1f)
                    fadeIn.duration = 500 // Set the duration as per your need
                    fadeIn.start()
                    isFirstChange = false
                }

                // Save checkbox state to SharedPreferences
                with(sharedPreferences.edit()) {
                    putBoolean(checkbox.id.toString(), isChecked)
                    apply()
                }
            }
        }
    }

    fun closeDrawerWithDelay(drawerLayout: DrawerLayout, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            drawerLayout.closeDrawer(GravityCompat.START)
        }, delayMillis)
    }
}