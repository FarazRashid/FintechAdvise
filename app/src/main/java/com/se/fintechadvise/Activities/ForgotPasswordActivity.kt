package com.se.fintechadvise.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var sendButton: Button
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initializeViews()
        setUpOnClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        sendButton = findViewById(R.id.sendButton)
        loginTextView = findViewById(R.id.loginTextView)
    }

    private fun setUpOnClickListeners() {
        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            Navigator.navigateToActivity(this,ResetPasswordActivity::class.java)
        }

        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)
        }
    }

}
