package com.se.fintechadvise.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.se.fintechadvise.Fragments.OnboardingScreen1Fragment
import com.se.fintechadvise.Fragments.OnboardingScreen2Fragment
import com.se.fintechadvise.Fragments.OnboardingScreen3Fragment
import com.se.fintechadvise.R

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var nextButton: Button
    private lateinit var skipTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        nextButton = findViewById(R.id.nextButton)
        skipTextView = findViewById(R.id.skipTextView)

        // Set up the ViewPager with the sections adapter.
        viewPager.adapter = SectionsPagerAdapter(supportFragmentManager)

        // Connect the TabLayout with the ViewPager.
        tabLayout.setupWithViewPager(viewPager)

        // Apply a PageTransformer for smooth transitions
        viewPager.setPageTransformer(true, DepthPageTransformer())

        nextButton.setOnClickListener {
            if (viewPager.currentItem < 2) {
                // If it's not the last page, move to the next page
                viewPager.currentItem++
            } else {
                // If it's the last page, start LoginActivity
                val intent = Intent(this@OnboardingActivity, LoginOrSignupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        skipTextView.setOnClickListener {
            // Start LoginActivity regardless of the current page
            val intent = Intent(this@OnboardingActivity, LoginOrSignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingScreen1Fragment()
                1 -> OnboardingScreen2Fragment()
                2 -> OnboardingScreen3Fragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    class DepthPageTransformer : ViewPager.PageTransformer {
        private val MIN_SCALE = 0.75f

        override fun transformPage(view: View, position: Float) {
            val pageWidth = view.width

            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.alpha = 1f
                    view.translationX = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    view.alpha = 1 - position

                    // Counteract the default slide transition
                    view.translationX = pageWidth * -position

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.alpha = 0f
                }
            }
        }
    }
}