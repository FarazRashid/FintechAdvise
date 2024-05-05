package com.se.fintechadvise.Fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.AdapterClasses.TransactionsAdapter
import com.se.fintechadvise.DataClasses.Transaction
import com.se.fintechadvise.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionsFragment : Fragment(), TransactionsAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView? = null
    private var transactionsList: List<Transaction>? = null
    private var currentList: List<Transaction>? = null

    private var activeFilters = mutableMapOf<String, String>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)


        setupSortSpinner(view)
        setupTransactionsRecyclerView(view)
        setupBackButton(view)



        return view
    }

    private fun setupBackButton(view: View?) {
        val backButton = view?.findViewById<ImageView>(R.id.backButtonTran)
        backButton?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSortSpinner(view: View) {
        val sortSpinner = view.findViewById<Spinner>(R.id.sortSpinner)
        val months = resources.getStringArray(R.array.sort_options)
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            months
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position >= 0
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item)

        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = months[position]
                filterTransactionsByMonth(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun filterTransactionsByMonth(month: String) {
        var filteredList = if (month == "All") {
            transactionsList
        } else {
            val monthNumber = monthToNumber(month)
            transactionsList?.filter {
                val transactionDate = it.transactionDate
                val transactionMonth = transactionDate.split("/")[1].toInt()
                transactionMonth == monthNumber
            }
        }
        for ((category, _) in activeFilters) {
            filteredList = filteredList?.filter { it.transactionCategory == category }
        }
        (recyclerView?.adapter as? TransactionsAdapter)?.updateData(filteredList!!)
        currentList = filteredList
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthToNumber(month: String): Int {
        return when (month) {
            "January" -> 1
            "February" -> 2
            "March" -> 3
            "April" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "August" -> 8
            "September" -> 9
            "October" -> 10
            "November" -> 11
            "December" -> 12
            "This Month" -> getCurrentMonth()
            else -> 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentMonth(): Int {
        val currentMonth = java.time.LocalDate.now().monthValue
        return currentMonth
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupTransactionsRecyclerView(view: View?) {
        getTransactionsList()
        val adapter = TransactionsAdapter(currentList!!,this)
        recyclerView = view?.findViewById(R.id.transactionsAllRecyclerView)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterTransactionClickListener(view, adapter)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun filterTransactionClickListener(view: View?, adapter: TransactionsAdapter?) {

        val spendingImageView = view?.findViewById<ImageView>(R.id.spendingImageView)
        val spendingTextView = view?.findViewById<TextView>(R.id.spendingTextView)
        val incomeImageView = view?.findViewById<ImageView>(R.id.incomeImageView)
        val incomeTextView = view?.findViewById<TextView>(R.id.incomeTextView)
        val billsImageView = view?.findViewById<ImageView>(R.id.billsImageView)
        val billsTextView = view?.findViewById<TextView>(R.id.billsTextView)
        val savingsImageView = view?.findViewById<ImageView>(R.id.savingsImageView)
        val savingsTextView = view?.findViewById<TextView>(R.id.savingsTextView)
        val viewAllTextView = view?.findViewById<TextView>(R.id.viewAllTextView)

        viewAllTextView?.setOnClickListener {
           adapter!!.updateData(transactionsList!!)
            currentList = transactionsList!!
        }

        spendingImageView?.setOnClickListener {
            filterTransactions("Spending", adapter!!)
        }
        spendingTextView?.setOnClickListener {
            filterTransactions("Spending", adapter!!)
        }

        incomeTextView?.setOnClickListener {
            filterTransactions("Income", adapter!!)
        }
        incomeImageView?.setOnClickListener {
            filterTransactions("Income", adapter!!)
        }

        billsTextView?.setOnClickListener {
            filterTransactions("Bills", adapter!!)
        }
        billsImageView?.setOnClickListener {
            filterTransactions("Bills", adapter!!)
        }

        savingsTextView?.setOnClickListener {
            filterTransactions("Savings", adapter!!)
        }
        savingsImageView?.setOnClickListener {
            filterTransactions("Savings", adapter!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterTransactions(category: String, adapter: TransactionsAdapter) {
        val month = (view?.findViewById<Spinner>(R.id.sortSpinner)?.selectedItem ?: "All") as String
        var filteredList = if (month == "All") {
            transactionsList
        } else {
            val monthNumber = monthToNumber(month)
            transactionsList?.filter {
                val transactionDate = it.transactionDate
                val transactionMonth = transactionDate.split("/")[1].toInt()
                transactionMonth == monthNumber
            }
        }
        filteredList = filteredList?.filter { it.transactionCategory == category }
        if (filteredList != null) {
            adapter.updateData(filteredList)
        }
        currentList = filteredList
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
                return position >= 0
            }
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item)

        spinner.adapter = adapter

        builder.setPositiveButton("Confirm") { _, _ ->
            val selectedCategory = spinner.selectedItem.toString()
            transaction.transactionCategory = selectedCategory
            // Notify the adapter that the data has changed
            recyclerView?.adapter?.notifyItemChanged(position)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getTransactionsList() {
        if(transactionsList == null) {
            transactionsList =
                arguments?.getParcelableArrayList("transactionsList", Transaction::class.java)
            currentList = transactionsList
            val filteredlist = currentList?.filter {
                val transactionDate = it.transactionDate
                val transactionMonth = transactionDate.split("/")[1].toInt()
                transactionMonth == getCurrentMonth()
            }
            currentList = filteredlist
        }
        else{
            currentList = transactionsList
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") =
            TransactionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}