package com.se.fintechadvise.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed(Runnable {
//         startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
        // startActivity(Intent(this@MainActivity, PlanningActivity::class.java))
            startActivity(Intent(this@MainActivity, RiskAssessmentActivity::class.java))
            finish()
        }, MainActivity.SPLASH_DELAY)
    }
    companion object {
        private const val SPLASH_DELAY: Long = 2000 // 2 seconds
    }

}
