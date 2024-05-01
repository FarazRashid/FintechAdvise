package com.se.fintechadvise.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.se.fintechadvise.HelperClasses.CustomToastMaker
import com.se.fintechadvise.R

class RiskAssessmentActivity: AppCompatActivity() {

    private lateinit var experienceSpinner: Spinner
    private lateinit var riskLevelSpinner: Spinner
    private lateinit var investmentTimeSpinner: Spinner
    private lateinit var financialStabilitySpinner: Spinner
    private lateinit var previousExperienceSpinner: Spinner
    private lateinit var riskToleranceRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_assessment)
        experienceSpinner = findViewById(R.id.experienceSpinner)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val experienceSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.experience_levels)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
        }
        experienceSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        experienceSpinner.adapter = experienceSpinnerAdapter

        riskLevelSpinner = findViewById(R.id.goalTypeSpinner)
        val riskLevelSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.risk_levels)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
        }
        riskLevelSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        riskLevelSpinner.adapter = riskLevelSpinnerAdapter
        investmentTimeSpinner = findViewById(R.id.goalInvestmentTimeSpinner)
        val investmentTimeSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.investment_time_horizons)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
        }
        investmentTimeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        investmentTimeSpinner.adapter = investmentTimeSpinnerAdapter

        financialStabilitySpinner = findViewById(R.id.financialStabilitySpinner)
        val financialStabilitySpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.financial_stability_levels)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
        }

        financialStabilitySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        financialStabilitySpinner.adapter = financialStabilitySpinnerAdapter

        previousExperienceSpinner = findViewById(R.id.previousExperience)
        val previousExperienceSpinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.experience_levels)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }
        }
        previousExperienceSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        previousExperienceSpinner.adapter = previousExperienceSpinnerAdapter
        
        riskToleranceRadioGroup = findViewById(R.id.riskToleranceRadioGroup)


        val submitButton = findViewById<Button>(R.id.submitFormButton)
        submitButton.setOnClickListener {
            val name = findViewById<TextView>(R.id.nameEditText).text.toString()
            val age = findViewById<TextView>(R.id.ageEditText).text.toString()
            val occupation = findViewById<TextView>(R.id.occupationEditText).text.toString()
            val income = findViewById<TextView>(R.id.incomeEditText).text.toString()
            val goals = findViewById<TextView>(R.id.goalsEditText).text.toString()
            val goaltime = findViewById<TextView>(R.id.goalTimeEditText).text.toString()
            val additionalComments = findViewById<TextView>(R.id.additionalCommentsEditText).text.toString()
            val selectedExperience = experienceSpinner.selectedItem.toString()
            val selectedRiskLevel = riskLevelSpinner.selectedItem.toString()
            val selectedInvestmentTime = investmentTimeSpinner.selectedItem.toString()
            val selectedFinancialStability = financialStabilitySpinner.selectedItem.toString()
            val selectedPreviousExperience = previousExperienceSpinner.selectedItem.toString()
            val selectedRiskToleranceRadioButtonId = riskToleranceRadioGroup.checkedRadioButtonId
            val selectedRiskTolerance = if (selectedRiskToleranceRadioButtonId != -1) {
                findViewById<RadioButton>(selectedRiskToleranceRadioButtonId).text.toString()
            } else {
                ""
            }
            val nameRegex = "^[a-zA-Z ]*$".toRegex()

            if (name.isEmpty() || age.isEmpty() || occupation.isEmpty() || income.isEmpty() || goals.isEmpty() || goaltime.isEmpty() || additionalComments.isEmpty() || selectedExperience.isEmpty() || selectedRiskLevel.isEmpty() || selectedInvestmentTime.isEmpty() || selectedFinancialStability.isEmpty() || selectedPreviousExperience.isEmpty() || selectedRiskTolerance.isEmpty()) {
                CustomToastMaker().showToast(this, "Please fill in all fields")
            }
            else if (name.isEmpty() || name.matches(nameRegex).not()) {
                CustomToastMaker().showToast(this, "Please enter a valid Name")
            } else if (age.isBlank() ||income.any { it !in '0'..'9' }|| age.toDouble() < 18 || age.toDouble() > 100) {
                CustomToastMaker().showToast(this, "You must be 18+ to use this app")
            } else if (occupation.isEmpty()) {
                CustomToastMaker().showToast(this, "Please enter your occupation")
            } else if (income.isBlank()|| income.any { it !in '0'..'9' } || income.toDouble() <= 0 || income.toDouble() > 1000000000) {
                CustomToastMaker().showToast(this, "Please enter a valid income")
            } else if (goals.isEmpty()) {
                CustomToastMaker().showToast(this, "Please enter your financial goals")
            } else if (goaltime.isEmpty()) {
                CustomToastMaker().showToast(this, "Please enter when you plan to achieve your goals")
            } else if (selectedExperience == "Select Experience Level") {
                CustomToastMaker().showToast(this, "Please select your experience level")
            } else if (selectedRiskLevel == "Select Risk Level") {
                CustomToastMaker().showToast(this, "Please select your risk level")
            } else if (selectedInvestmentTime == "Select Investment Time Horizon") {
                CustomToastMaker().showToast(this, "Please select your investment time horizon")
            } else if (selectedFinancialStability == "Select Financial Stability Level") {
                CustomToastMaker().showToast(this, "Please select your financial stability level")
            } else if (selectedPreviousExperience == "Select Previous Experience Level") {
                CustomToastMaker().showToast(this, "Please select your previous experience level")
            } else if (riskToleranceRadioGroup.isEmpty()|| riskToleranceRadioGroup.toString()=="" ||riskToleranceRadioGroup.checkedRadioButtonId == -1) {
                CustomToastMaker().showToast(this, "Please select your risk tolerance")
            } else {
                // All fields are valid, proceed with form submission


                // clear all fields
                findViewById<TextView>(R.id.nameEditText).text = ""
                findViewById<TextView>(R.id.ageEditText).text = ""
                findViewById<TextView>(R.id.occupationEditText).text = ""
                findViewById<TextView>(R.id.incomeEditText).text = ""
                findViewById<TextView>(R.id.goalsEditText).text = ""
                findViewById<TextView>(R.id.goalTimeEditText).text = ""
                findViewById<TextView>(R.id.additionalCommentsEditText).text = ""
                experienceSpinner.setSelection(0)
                riskLevelSpinner.setSelection(0)
                investmentTimeSpinner.setSelection(0)
                financialStabilitySpinner.setSelection(0)
                previousExperienceSpinner.setSelection(0)
                riskToleranceRadioGroup.clearCheck()
                CustomToastMaker().showToast(this, "Form submitted successfully")
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
        }

    }



}