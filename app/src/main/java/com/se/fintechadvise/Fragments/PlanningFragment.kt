package com.se.fintechadvise.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.AdapterClasses.GoalAdapter
import com.se.fintechadvise.DataClasses.Goal
import com.se.fintechadvise.ManagerClasses.UserManager
import com.se.fintechadvise.ManagerClasses.WebserviceManger
import com.se.fintechadvise.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.UUID

class PlanningFragment : Fragment(), GoalAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var goals: MutableList<Goal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        goals = mutableListOf()

        getUserGoals()
//        goals.add(Goal("Buy a new car", "Car", "31/10/2024", "1000", "100000", "Planning", "1","High"))
//        goals.add(Goal("Save money to retire", "More Money", "12/5/2023", "1000", "1000000", "Retirement", "2","Medium"))
        recyclerView = view.findViewById(R.id.goalsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = GoalAdapter(goals, this)
        recyclerView.adapter = adapter

        val addGoalButton = view.findViewById<Button>(R.id.addGoalButton)

        addGoalButton.setOnClickListener {
            Log.d("PlanningActivity", "Add goal button clicked")

            // Inflate the custom layout
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_add_goal, null)



            // Set the onTouchListener for the goalDateEditText
            val goalDateEditText = dialogLayout.findViewById<EditText>(R.id.goalDateEditText)
            goalDateEditText.setOnTouchListener { v, event ->

                val DRAWABLE_LEFT = 0

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (goalDateEditText.left - goalDateEditText.compoundDrawables[DRAWABLE_LEFT].bounds.width())) {
                        // your action here
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            // Display Selected date in textbox
                            goalDateEditText.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                        }, year, month, day)

                        dpd.show()

                        v.performClick() // Call performClick here
                        return@setOnTouchListener true
                    }
                }
                false
            }
            val goalTypeSpinner = dialogLayout.findViewById<Spinner>(R.id.goalTypeEditText)

            // Create an ArrayAdapter using the string array and a default spinner layout
            // Create an ArrayAdapter using the string array and a default spinner layout
            val Aadapter = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.goal_types)
            ) {
                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }
            }
            // Specify the layout to use when the list of choices appears
            Aadapter.setDropDownViewResource(R.layout.spinner_item)

            // Apply the adapter to the spinner
            goalTypeSpinner.adapter = Aadapter

            // Set the on item selected listener
            goalTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedGoalType = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when nothing is selected
                }

            }


            val goalPrioritySpinner = dialogLayout.findViewById<Spinner>(R.id.goalPriorityEditText)
            val priorityAdapter = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.goal_priorities)
            ) {
                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }
            }
            priorityAdapter.setDropDownViewResource(R.layout.spinner_item)
            goalPrioritySpinner.adapter = priorityAdapter
            goalPrioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedGoalPriority = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do something when nothing is selected
                }

            }
            // Create an AlertDialog.Builder instance
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Add Goal")
            builder.setView(dialogLayout)

            // Set positive and negative buttons
            builder.setPositiveButton("Add") { dialog, _ ->
                val goalName = dialogLayout.findViewById<EditText>(R.id.goalNameEditText).text.toString()
                val goalType = dialogLayout.findViewById<Spinner>(R.id.goalTypeEditText).selectedItem.toString()
                val goalDate = dialogLayout.findViewById<EditText>(R.id.goalDateEditText).text.toString()
                val currentAmount = dialogLayout.findViewById<EditText>(R.id.currentAmountEditText).text.toString()
                val goalAmount = dialogLayout.findViewById<EditText>(R.id.goalAmountEditText).text.toString()
                val goalPriority = dialogLayout.findViewById<Spinner>(R.id.goalPriorityEditText).selectedItem.toString()
                val goalId = "3"
                if(goalName.isEmpty() || goalType.isEmpty() || goalDate.isEmpty() || currentAmount.isEmpty() || goalAmount.isEmpty() || goalPriority.isEmpty()) {
                    Toast.makeText(dialogLayout.context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    // name should only have alphabets and numbers and should not be empty and should not be more than 20 characters
                    // type should not be empty
                    // date should not be empty
                    // current amount should be a positive number and should not be empty
                    // goal amount should be a positive number and should not be empty
                    val nameRegex = "^[a-zA-Z0-9 ]*$".toRegex()
                    if(!goalName.matches(nameRegex)) {
                        Toast.makeText(dialogLayout.context, "Name should only have alphabets and numbers", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(goalName.isEmpty() || goalName.length > 20) {
                        Toast.makeText(dialogLayout.context, "Name should not be empty and should not be more than 20 characters", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(goalType.isEmpty() || goalType == "Select goal type") {
                        Toast.makeText(dialogLayout.context, "Type should not be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(goalDate.isEmpty()) {
                        Toast.makeText(dialogLayout.context, "Date should not be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton

                    }
                    if(!currentAmount.matches("^[0-9]*$".toRegex())){
                        Toast.makeText(dialogLayout.context, "Current amount should be a number", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(currentAmount.isEmpty() || currentAmount.toInt() < 0) {
                        Toast.makeText(dialogLayout.context, "Current amount should be a positive number and should not be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    //goal amount is a number
                    if(!goalAmount.matches("^[0-9]*$".toRegex())){
                        Toast.makeText(dialogLayout.context, "Goal amount should be a number", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(goalAmount.isEmpty() || goalAmount.toInt() < 0 || goalAmount.toInt() < currentAmount.toInt()){
                        Toast.makeText(dialogLayout.context, "Goal amount should be a positive number and Greater than current amount", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if(goalPriority.isEmpty() || goalPriority == "Select priority") {
                        Toast.makeText(dialogLayout.context, "Priority should not be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    goals.add(Goal(goalName, goalType, goalDate, currentAmount, goalAmount, goalType, goalId, goalPriority))

                    addNewGoal(Goal(goalName, goalType, goalDate, currentAmount, goalAmount, goalType, goalId, goalPriority))

                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                return@setPositiveButton

            }
            builder.setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()

            }


            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))


        }
        // Your existing code from onCreate in PlanningActivity goes here
        // Make sure to replace 'this' with 'requireContext()' or 'requireActivity()' as needed
        // For example, replace 'this' in LinearLayoutManager(this) with 'requireContext()'
    }

    private fun getUserGoals() {
        val params = JSONObject()
        params.put("userId", UserManager.getCurrentUser()!!.id)

        WebserviceManger.getInstance().jsonArrayGetRequest("get_goals", params, requireContext(), { response ->
            Log.d("PlanningFragment", "Response: $response")
            val goalsArray = response
            for (i in 0 until goalsArray.length()) {
                val goal = goalsArray.getJSONObject(i)
                goals.add(Goal(goal.getString("name"), goal.getString("target"), goal.getString("goalDate"), goal.getString("currentAmount"), goal.getString("goalAmount"), goal.getString("goalType"), goal.getString("goalId"), goal.getString("goalPriority")))
            }
            adapter.notifyDataSetChanged()
        }, { error ->
            Log.e("PlanningFragment", "Error: $error")
        })
    }

    private fun addNewGoal(goal: Goal){
        // Add the goal to the database
        // Create a JSONObject
        val params = JSONObject()
        params.put("name", goal.name)
        params.put("target", goal.goalType)
        params.put("goalDate", goal.goalDate)
        params.put("currentAmount", goal.currentAmount)
        params.put("goalAmount", goal.goalAmount)
        params.put("goalType", goal.goalType)
        params.put("goalId", UUID.randomUUID().toString())
        params.put("goalPriority", goal.goalPriority)
        params.put("userId", UserManager.getCurrentUser()!!.id)

        // Call the postRequest method from the WebserviceManager
        WebserviceManger.getInstance().postRequest("add_goal", params, requireContext(), { response ->
            Log.d("PlanningFragment", "Goal added successfully")

        }, { error ->
            Log.e("PlanningFragment", "Error: $error")
        })
    }
    override fun onItemClick(goal: Goal) {
        Log.d("PlanningActivity", "Item clicked: ${goal.name}")

// Inflate the custom layout
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_view_goal, null)

// Set the text of each TextView
        dialogLayout.findViewById<TextView>(R.id.goalNameTextView).text = goal.name
        dialogLayout.findViewById<TextView>(R.id.goalTypeTextView).text = goal.goalType
        dialogLayout.findViewById<TextView>(R.id.goalDateTextView).text = goal.goalDate
        dialogLayout.findViewById<TextView>(R.id.currentAmountTextView).text = goal.currentAmount
        dialogLayout.findViewById<TextView>(R.id.goalAmountTextView).text = goal.goalAmount
        dialogLayout.findViewById<TextView>(R.id.goalPriorityTextView).text = goal.goalPriority

// Create an AlertDialog.Builder instance
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Goal Details")
        builder.setView(dialogLayout) // Set the custom layout as the content view
        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()

        dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
    }
    companion object {
        @JvmStatic
        fun newInstance() = PlanningFragment()
    }
}