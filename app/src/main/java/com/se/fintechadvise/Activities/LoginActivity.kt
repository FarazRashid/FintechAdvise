package com.se.fintechadvise.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ViewUtils
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R


class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUpButton: TextView
    private lateinit var backButton: ImageView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setUpOnClickListeners()
        setPasswordListener()
    }

    private fun initializeViews() {
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpTextView)
        backButton = findViewById(R.id.backButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
    }

    private fun setUpOnClickListeners() {
        loginButton.setOnClickListener {
            verifyFields(){
                if(it)
                    Navigator.navigateToActivity(this, HomeActivity::class.java)
            }
        }

        signUpButton.setOnClickListener {
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        forgotPasswordTextView.setOnClickListener {
           Navigator.navigateToActivity(this, ForgotPasswordActivity::class.java)
        }
    }

    fun verifyFields(CallBack: (Boolean) -> Unit){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        //check if email is empty
        if(email.isEmpty()){
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            CallBack(false)
            return
        }

        //check if email is valid it must contain some text an @ some more text and a .com

        if(!email.contains("@") || !email.contains(".")){
            emailEditText.error = "Email is invalid"
            emailEditText.requestFocus()
            CallBack(false)
            return
        }

        //check if password is empty

        if(password.isEmpty()){
            passwordEditText.error = "Password is required"
            passwordEditText.requestFocus()
            CallBack(false)
            return
        }

        //check if password is less than 8 characters

        if(password.length < 8){
            passwordEditText.error = "Password must be at least 8 characters"
            passwordEditText.requestFocus()
            CallBack(false)
            return
        }
        CallBack(true)
        return
    }

    @SuppressLint("ClickableViewAccessibility", "RestrictedApi")
    fun setPasswordListener(){
        passwordEditText.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                val touchArea = passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width() - ViewUtils.dpToPx(
                    this,
                    20
                ) // 20 is the additional touch area in dps
                if (event.rawX >= touchArea) {
                    // toggle password visibility
                    val selection = passwordEditText.selectionEnd
                    passwordEditText.inputType = if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    else
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordEditText.setSelection(selection)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }


}
