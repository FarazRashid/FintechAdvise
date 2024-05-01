package com.se.fintechadvise.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R

class LoginOrSignupActivity : AppCompatActivity() {
    private lateinit var loginTextView: TextView
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_signup)
        val loginTextView = findViewById<TextView>(R.id.LoginTextView)

        initalizeViews()
        setUpOnClickListeners()
    }

    fun initalizeViews()
    {
        loginTextView = findViewById(R.id.LoginTextView)
        signUpButton = findViewById(R.id.signUpButton)
    }

    fun setUpOnClickListeners()
    {
        loginTextView.setOnClickListener{
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            Navigator.navigateToActivity(this, LoginActivity::class.java)

        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }
    }

}