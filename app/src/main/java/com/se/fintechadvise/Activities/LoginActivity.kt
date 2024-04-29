package com.se.fintechadvise.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R


class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUpButton: TextView
    private lateinit var backButton: ImageView
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setUpOnClickListeners()
    }

    private fun initializeViews() {
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpTextView)
        backButton = findViewById(R.id.backButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
    }

    private fun setUpOnClickListeners() {
        loginButton.setOnClickListener {
          //  Navigator.navigateToActivity(this, HomeActivity::class.java)
        }

        signUpButton.setOnClickListener {
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        forgotPasswordTextView.setOnClickListener {
           // Navigator.navigateToActivity(this, ForgotPasswordActivity::class.java)
        }
    }


}
