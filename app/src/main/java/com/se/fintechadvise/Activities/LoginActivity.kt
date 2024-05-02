package com.se.fintechadvise.Activities


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ViewUtils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.se.fintechadvise.DataClasses.User
import com.se.fintechadvise.HelperClasses.CustomToastMaker
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.ManagerClasses.WebserviceManger
import com.se.fintechadvise.R
import  com.se.fintechadvise.ManagerClasses.UserManager
import java.util.UUID
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUpButton: TextView
    private lateinit var backButton: ImageView
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberMeCheckBox: CheckBox
    private var storedVerificationId: String? = ""
    private lateinit var mauth: FirebaseAuth



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
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
        mauth = FirebaseAuth.getInstance()
    }

    private fun setUpOnClickListeners() {

        loginButton.setOnClickListener {
            verifyFields { isValid ->
                if (isValid) {
                    WebserviceManger.getInstance().loginUser(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString(),
                        this
                    ) { isSuccess, user ->
                        if (isSuccess) {
                            if (user != null) {
                                UserManager.getInstance().setCurrentUser(user)
                                UserManager.getInstance().getCurrentUser()
                                    ?.let { it1 -> sendPhoneVerificationCode(it1.phone) }
                                showOtpDialog()
                            }



                        }
                    }
                }
            }
        }

        signUpButton.setOnClickListener {
            // Navigate to SignUpActivity
            Navigator.navigateToActivity(this, SignUpActivity::class.java)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        forgotPasswordTextView.setOnClickListener {
            // Navigate to ForgotPasswordActivity
            Navigator.navigateToActivity(this, ForgotPasswordActivity::class.java)
        }
    }

    fun showOtpDialog() {
        // Inflate the dialog layout
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_otp, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent the user from dismissing the dialog by pressing back
            .create()

        // Get the EditText and Button from the dialog layout
        val otpEditText = dialogView.findViewById<EditText>(R.id.phoneOtpEditText)
        val verifyOtpButton = dialogView.findViewById<Button>(R.id.verifyOtpButton)

        // Set a click listener for the "Verify OTP" button
        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            if (otp.isNotEmpty()) {
                verifyCode(otp)
                dialog.dismiss()
            } else {
                otpEditText.error = "Please enter the OTP"
            }
        }

        // Show the dialog
        dialog.show()
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

    fun sendPhoneVerificationCode(phoneNumber:String)
    {
        Log.d("SignUpActivity", "sendPhoneVerificationCode: $phoneNumber") // Debug statement

        val options = PhoneAuthOptions.newBuilder(mauth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    var callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code:String= credential.smsCode.toString()
            Log.d("SignUpActivity", "onVerificationCompleted: $code") // Debug statement
            if(code!=null)
            {
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d("SignUpActivity", "onVerificationFailed: ${e.message}") // Debug statement
            // CustomToastMaker().showToast(this@SignUpActivity, "Verification failed")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId

            CustomToastMaker().showToast(this@LoginActivity, "Code sent")
        }
    }



    fun verifyCode(code:String)
    {
        Log.d("SignUpActivity", "verifyCode: $code") // Debug statement

        var credential: PhoneAuthCredential? =
            storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }


        if(rememberMeCheckBox.isChecked){
            Log.d("LoginActivity", "Remember me is checked")
            UserManager.saveUserLoggedInSP(
                true,
                getSharedPreferences("USER_LOGIN", MODE_PRIVATE)
            )
            UserManager.saveUserEmailSP(emailEditText.text.toString(), getSharedPreferences("USER_LOGIN", MODE_PRIVATE))
        }

        Navigator.navigateToActivity(this, HomeActivity::class.java)
        finish()

    }


}
