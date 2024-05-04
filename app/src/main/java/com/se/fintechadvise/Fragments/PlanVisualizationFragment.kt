package com.se.fintechadvise.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.annotation.RequiresApi
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
 * Use the [PlanVisualizationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlanVisualizationFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_plan_visualization, container, false)

        getTransactionsList()




        setupSortSpinner(view)

        return view
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
    @RequiresApi(Build.VERSION_CODES.O)
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