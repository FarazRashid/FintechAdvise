package com.se.fintechadvise.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se.fintechadvise.HelperClasses.Navigator
import com.se.fintechadvise.R
import java.util.Collections
import java.util.Locale
import java.util.regex.Pattern
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.se.fintechadvise.DataClasses.User
import com.se.fintechadvise.HelperClasses.CustomToastMaker
import com.se.fintechadvise.ManagerClasses.UserManager
import com.se.fintechadvise.ManagerClasses.WebserviceManger
import java.util.UUID
import java.util.concurrent.TimeUnit


class SignUpActivity : AppCompatActivity() {

    private lateinit var loginTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var signUpButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordStrengthProgressBar: ProgressBar
    private lateinit var countrySpinner: Spinner
    private lateinit var tooltip: PopupWindow
    private var isSignUpInProgress = false
    private var storedVerificationId: String? = ""


    private lateinit var mauth: FirebaseAuth


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initializeViews()
        setUpOnClickListeners()
        setPasswordListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViews() {
        loginTextView = findViewById(R.id.loginTextView)
        backButton = findViewById(R.id.backButton)
        signUpButton = findViewById(R.id.signUpButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.reEnterPasswordEditText)
        phoneNumberEditText = findViewById(R.id.phoneEditText)
        passwordStrengthProgressBar = findViewById(R.id.passwordStrengthProgressBar)
        passwordEditText.tooltipText = "Password must be greater than 8 characters, must have one upper case, one special character, and have a numeric value"
        countrySpinner = findViewById(R.id.countryEditText)
        setupCountrySpinner()
        mauth = FirebaseAuth.getInstance()
        var settings= mauth.firebaseAuthSettings
        settings.forceRecaptchaFlowForTesting(true)
        //mauth.firebaseAuthSettings = settings

    }

    private fun countryList(): List<String> {
        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.length > 0 && !countries.contains(country)) {
                countries.add(country)
            }
        }
        Collections.sort(countries)
        countries.add(0, "Select A Country")
        return countries
    }


    private fun setupCountrySpinner() {
        val countrySpinner: Spinner = findViewById(R.id.countryEditText)
        val countries = countryList()
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, countries) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                view.isEnabled = position != 0 // Disable the first item
                return view
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item) // Set the drop-down view to spinner_item
        countrySpinner.adapter = adapter
    }

    private fun verifyFields(CallBack: (Boolean) -> Unit){
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()


        // Check if any field is empty
        if (email.isEmpty()) {
            emailEditText.error = "Please enter an email"
            emailEditText.requestFocus()
            CallBack(false)
            return
        }


        //email must contain some text before @ and after @ and before .com apply regex

        val emailPattern = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+.com$")

        val emailMatcher = emailPattern.matcher(email)

        if (!emailMatcher.matches()) {
            emailEditText.error = "Please enter a valid email"
            emailEditText.requestFocus()
            CallBack(false)
            return
        }

        //country must be selected and should not be equal to "Select A Country"

        if (countrySpinner.selectedItem.toString() == "Select A Country") {
            CustomToastMaker().showToast(this, "Please select a country")
            CallBack(false)
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Please enter a password"
            passwordEditText.requestFocus()
            CallBack(false)
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Please confirm your password"
            confirmPasswordEditText.requestFocus()
            CallBack(false)
            return
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.error = "Please enter a phone number"
            phoneNumberEditText.requestFocus()
            CallBack(false)
            return

        }

        if (!phoneNumber.startsWith("+") || phoneNumber.length != 13) {
            phoneNumberEditText.error = "Phone number must begin with + and be 13 digits long"
            phoneNumberEditText.requestFocus()
            CallBack(false)
            return
        }

        //phone number must not have any characters other than the starting plus

        val phoneNumberPattern = Pattern.compile("^[+][0-9]+\$")

        val phoneNumberMatcher = phoneNumberPattern.matcher(phoneNumber)

        if (!phoneNumberMatcher.matches()) {
            phoneNumberEditText.error = "Phone number must begin with + and be 13 digits long"
            phoneNumberEditText.requestFocus()
            CallBack(false)
            return
        }

        if(passwordStrengthProgressBar.progress < 4){
            passwordEditText.error = "Password is too weak"
            passwordEditText.requestFocus()
            CallBack(false)
            return
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Password and confirm password must be the same"
            confirmPasswordEditText.requestFocus()
            CallBack(false)
            return
        }

        CallBack(true)
        return

    }

    private fun getPasswordStrength(password: String): Int {
        var score = 0

        // Check password length
        if (password.length > 8) score++

        // Check for uppercase letters
        if (password.any { it.isUpperCase() }) score++

        // Check for lowercase letters
        if (password.any { it.isLowerCase() }) score++

        // Check for digits
        if (password.any { it.isDigit() }) score++

        val specialCharacters = arrayOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+')
        if (password.any { it in specialCharacters }) score++

        return score
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

    private fun setUpOnClickListeners() {
        loginTextView.setOnClickListener {
            Navigator.navigateToActivity(this,LoginActivity::class.java)

        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        signUpButton.setOnClickListener {



            if(!isSignUpInProgress){
                isSignUpInProgress = true
                verifyFields {
                    if(it) {

                        sendPhoneVerificationCode(phoneNumberEditText.text.toString())
                        showOtpDialog()
                    }
                    isSignUpInProgress = false

                }
            }

        }

        }



    fun setPasswordStrengthColor(passwordStrength: Int) {
        when (passwordStrength) {
            in 0..2 -> passwordStrengthProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF0000")) // Red color
            in 3..4 -> passwordStrengthProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFFF00")) // Yellow color
            5 -> passwordStrengthProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#008000")) // Green color
        }
    }

    private fun getTooltipText(passwordStrength: Int, password: String): String {
        var tooltipText = when (passwordStrength) {
            0 -> "Password is too weak"
            1 -> "Password is weak"
            2 -> "Password is fair"
            3 -> "Password is strong"
            4 -> "Password is very strong"
            else -> "Password is excellent"
        }

        // Add password requirements to tooltip text
        if (!password.any { it.isUpperCase() }) tooltipText += ", must have one upper case"
        if (!password.any { it.isDigit() }) tooltipText += ", must have a numeric value"
        //check special characters
        val specialCharacters = arrayOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+')
        if (!password.any { it in specialCharacters }) tooltipText += ", must have one special character"
        //length of password
        if (password.length < 8) tooltipText += ", must be greater than 8 characters"

        return tooltipText
    }

    @SuppressLint("ClickableViewAccessibility", "RestrictedApi")
    private fun setPasswordListener(){
        passwordEditText.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                val passwordStrength = getPasswordStrength(s.toString())
                passwordStrengthProgressBar.progress = passwordStrength
                setPasswordStrengthColor(passwordStrength)

                // Show tooltip
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val tooltipView = inflater.inflate(R.layout.tooltip_layout, null)
                val tooltipText: TextView = tooltipView.findViewById(R.id.tooltip_text)
                tooltipText.text = getTooltipText(passwordStrength, s.toString())

                // Dismiss previous tooltip if it is showing
                if (::tooltip.isInitialized && tooltip.isShowing) {
                    tooltip.dismiss()
                }

                tooltip = PopupWindow(tooltipView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tooltip.isOutsideTouchable = true
                tooltip.showAsDropDown(passwordEditText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }
        })

        passwordEditText.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                val touchArea = passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width() - dpToPx(this,20) // 20 is the additional touch area in dps
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

        confirmPasswordEditText.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                val touchArea = confirmPasswordEditText.right - confirmPasswordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width() - dpToPx(this,20) // 20 is the additional touch area in dps
                if (event.rawX >= touchArea) {
                    // toggle password visibility
                    val selection = confirmPasswordEditText.selectionEnd
                    confirmPasswordEditText.inputType = if (confirmPasswordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    else
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    confirmPasswordEditText.setSelection(selection)
                    return@setOnTouchListener true
                }
            }
            false
        }

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Show tooltip
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val tooltipView = inflater.inflate(R.layout.tooltip_layout, null)
                val tooltipText: TextView = tooltipView.findViewById(R.id.tooltip_text)
                tooltipText.text = "Email must be in the format: example@example.com"

                // Dismiss previous tooltip if it is showing
                if (::tooltip.isInitialized && tooltip.isShowing) {
                    tooltip.dismiss()
                }

                tooltip = PopupWindow(tooltipView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tooltip.isOutsideTouchable = true
                tooltip.showAsDropDown(emailEditText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }
        })

        phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Show tooltip
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val tooltipView = inflater.inflate(R.layout.tooltip_layout, null)
                val tooltipText: TextView = tooltipView.findViewById(R.id.tooltip_text)
                tooltipText.text = "Phone number must begin with + and be 13 digits long"

                // Dismiss previous tooltip if it is showing
                if (::tooltip.isInitialized && tooltip.isShowing) {
                    tooltip.dismiss()
                }

                tooltip = PopupWindow(tooltipView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                tooltip.isOutsideTouchable = true
                tooltip.showAsDropDown(phoneNumberEditText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }
        })
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
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId

            CustomToastMaker().showToast(this@SignUpActivity, "Code sent")
        }
    }



    fun verifyCode(code:String)
    {
        Log.d("SignUpActivity", "verifyCode: $code") // Debug statement

        var credential: PhoneAuthCredential? =
            storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }

        var user = User(UUID.randomUUID().toString(), "", emailEditText.text.toString(), countrySpinner.selectedItem.toString(), passwordEditText.text.toString(), phoneNumberEditText.text.toString(), "", "")
        UserManager.setCurrentUser(user)

        Navigator.navigateToActivity(this, RiskAssessmentActivity::class.java)
        finish()

    }
}