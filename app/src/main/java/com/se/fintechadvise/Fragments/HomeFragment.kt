package com.se.fintechadvise.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.se.fintechadvise.AdapterClasses.BudgetAdapter
import com.se.fintechadvise.AdapterClasses.TransactionsAdapter
import com.se.fintechadvise.DataClasses.Budget
import com.se.fintechadvise.DataClasses.Transaction
import com.se.fintechadvise.HelperClasses.BottomNavigationHelper
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), TransactionsAdapter.OnItemClickListener  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView? = null
    private var budgetingRecyclerView: RecyclerView? = null
    private var budgetAdapter: BudgetAdapter? = null

    private var transactionsList = listOf<Transaction>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private fun setupMenuOpener(view: View) {
        val menuOpener = view.findViewById<ImageView>(R.id.menu_opener)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)

        menuOpener.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            setupNavigationView()
        }
    }

    private fun setupNavigationView() {
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.side_nav)
        val fragmentHelper = FragmentHelper(requireActivity().supportFragmentManager, requireContext())
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingsButton -> true
                R.id.monthlyRankingsButton -> true
                R.id.popularPLaylistsButton -> {
                    fragmentHelper.closeDrawerWithDelay(drawerLayout, 300) // delay in milliseconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        fragmentHelper.loadFragment(SettingsFragment())
                    }, 300)

                    true
                }
                R.id.notificataionsButton -> true
                else -> false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        getTransactionsList()
        setupPlanButton(view)

        setupTransactionsRecyclerView(view)
        setupBudgetRecyclerView(view)
        setupSeeAllTransactions(view)
        setupMenuOpener(view)

        return view
    }

    private fun setupSeeAllTransactions(view: View?) {
        val seeAllTransactions = view?.findViewById<TextView>(R.id.seeAllTransactionsTextView)
        seeAllTransactions?.setOnClickListener {

            val transactionsFragment = TransactionsFragment.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArrayList("transactionsList", ArrayList(transactionsList))
            transactionsFragment.arguments = bundle

            val fragmentHelper = FragmentHelper(requireActivity().supportFragmentManager, requireContext())
            fragmentHelper.loadFragment(transactionsFragment)
        }
    }
    private fun setupPlanButton(view: View?) {
        val planButton = view?.findViewById<ImageButton>(R.id.viewPlanButton)
        planButton?.setOnClickListener {
            val planFragment = PlanVisualizationFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList("transactionsList",
                transactionsList?.let { it1 -> ArrayList(it1) })
            planFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, planFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupTransactionsRecyclerView(view: View?) {
        recyclerView = view?.findViewById(R.id.transactionsRecyclerView)
        val adapter = TransactionsAdapter(transactionsList,this)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
    override fun onItemClick(position: Int, transaction: Transaction) {
        // Create and show the dialog
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_transaction_category, null)
        builder.setView(dialogView)

        val spinner = dialogView.findViewById<Spinner>(R.id.transactionTypeEditText)
        val categories = arrayOf("Income", "Spending", "Bills", "Savings") // Add your categories here
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position >=0
            }
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item)

        spinner.adapter = adapter

        builder.setPositiveButton("Confirm") { dialog, which ->
            val selectedCategory = spinner.selectedItem.toString()

            // If the transaction already has a category, subtract the transaction amount from the old budget item
            if (transaction.transactionCategory.isNotEmpty()) {
                val oldBudgetCategory = transaction.transactionCategory
                val transactionAmount = transaction.transactionAmount.replace("$", "").toDoubleOrNull() ?: 0.0
                budgetAdapter?.let { adapter ->
                    val oldBudget = adapter.budgets.find { it.category == oldBudgetCategory }
                    oldBudget?.let {
                        it.currentAmount -= transactionAmount
                    }
                }
            }

            //increment the new budget item

            val transactionAmount = transaction.transactionAmount.replace("$", "").toDoubleOrNull() ?: 0.0
            budgetAdapter?.let { adapter ->
                val newBudget = adapter.budgets.find { it.category == selectedCategory }
                newBudget?.let {
                    it.currentAmount += transactionAmount
                }
            }

            // Update the transaction's category
            transaction.transactionCategory = selectedCategory

            // Find the transaction in transactionsList and update it
            val index = transactionsList.indexOfFirst { it.transactionId == transaction.transactionId }
            if (index != -1) {
                transactionsList[index].transactionCategory = selectedCategory
            }

            // Notify the adapter that the data has changed
            recyclerView?.adapter?.notifyItemChanged(position)
            budgetAdapter?.notifyDataSetChanged()

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }
    private fun getTransactionsList() {
        val transactionList = mutableListOf<Transaction>()
        val categories = arrayOf("Income", "Spending", "Bills", "Savings") // Add your categories here
        val names = arrayOf("Booking", "Breaker", "Transfer", "Deposit", "Withdrawal") // Add your transaction names here
        val random = java.util.Random()


        for (i in 1..365) {
            val id = i.toString()

            // Select a random name
            val name = names[random.nextInt(names.size)]

            // Generate a random amount between -100 and 100
            val randomAmount = random.nextInt(200) - 100
            val amount = if (randomAmount >= 0) "+$$randomAmount" else "-$$randomAmount"

            // Select a random category
            val category = categories[random.nextInt(categories.size)]

            // Calculate the month and day for the current iteration
            val month = (i / 30 % 12) + 1  // Approximate day of the month, not accurate
            val day = (i % 30) + 1  // Approximate day of the month, not accurate

            val date = "$day/$month/2021"
            Log.d("date",date)
            transactionList.add(Transaction(id, name, category, amount, date))
        }
        transactionsList = transactionList
    }

    interface BudgetClickListener {
        fun onBudgetClick(budget: Budget)
    }




    private fun getBudgetsList(): List<Budget> {
        val budgetsList = mutableListOf<Budget>()
        budgetsList.add(Budget("Spending", R.color.spending, 100.0, 200.0))
        budgetsList.add(Budget("Bills", R.color.bills, 50.0, 100.0))
        budgetsList.add(Budget("Income", R.color.income, 150.0, 200.0))
        budgetsList.add(Budget("Savings", R.color.savings, 50.0, 100.0))
        return budgetsList.toList()
    }

    private fun setupBudgetRecyclerView(view: View?) {
        val budgetsList = getBudgetsList() // This function should return a list of Budget objects
        budgetingRecyclerView = view?.findViewById<RecyclerView>(R.id.budgetingRecyclerView) // Replace with your RecyclerView id
        budgetAdapter = BudgetAdapter(budgetsList, object : BudgetAdapter.BudgetClickListener {
            override fun onBudgetClick(budget: Budget) {
                // Create and show the dialog
                val builder = AlertDialog.Builder(requireContext())
                val inflater = requireActivity().layoutInflater
                val dialogView = inflater.inflate(R.layout.dialog_set_budget, null)
                builder.setView(dialogView)

                val budgetAmountEditText = dialogView.findViewById<EditText>(R.id.phoneOtpEditText)

                builder.setPositiveButton("Confirm", null)
                builder.setNegativeButton("Cancel", null)

                val dialog = builder.create()
                dialog.show()

                // Change the color of the "Confirm" and "Cancel" text
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.icon_secondary)) // Replace 'your_color' with the actual color resource
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.icon_secondary)) // Replace 'your_color' with the actual color resource

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newMaxBudget = budgetAmountEditText.text.toString().toDoubleOrNull()
                    if (newMaxBudget != null) {
                        budget.maxAmount = newMaxBudget
                        // Notify the adapter that the data has changed
                        budgetingRecyclerView?.adapter?.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }

                //set the hint for the edit text to the current budget amount

                budgetAmountEditText.hint = "Currently : $" + budget.maxAmount.toString()

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    dialog.dismiss()
                }
            }
        })

        budgetingRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 2) // 2 spans for 2x2 grid
        budgetingRecyclerView?.adapter = budgetAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}