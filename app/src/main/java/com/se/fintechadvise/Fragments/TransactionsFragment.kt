package com.se.fintechadvise.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.sort_options)
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position >= 0
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item)

        sortSpinner.adapter = adapter

    }

    private fun setupTransactionsRecyclerView(view: View?) {

        val transactionsList = getTransactionsList()
        var adapter : TransactionsAdapter? = null
        adapter = TransactionsAdapter(transactionsList,this)
        recyclerView = view?.findViewById(R.id.transactionsAllRecyclerView)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        filterTransactionClickListener(view, transactionsList, adapter)
    }

    private fun filterTransactionClickListener(view: View?, transactionsList: List<Transaction>, adapter: TransactionsAdapter?) {

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
            adapter!!.updateData(transactionsList)
        }

        spendingImageView?.setOnClickListener {
            filterTransactions("Spending", transactionsList, adapter!!)
        }
        spendingTextView?.setOnClickListener {
            filterTransactions("Spending", transactionsList, adapter!!)
        }

        incomeTextView?.setOnClickListener {
            filterTransactions("Income", transactionsList, adapter!!)
        }
        incomeImageView?.setOnClickListener {
            filterTransactions("Income", transactionsList, adapter!!)
        }

        billsTextView?.setOnClickListener {
            filterTransactions("Bills", transactionsList, adapter!!)
        }
        billsImageView?.setOnClickListener {
            filterTransactions("Bills", transactionsList, adapter!!)
        }

        savingsTextView?.setOnClickListener {
            filterTransactions("Savings", transactionsList, adapter!!)
        }
        savingsImageView?.setOnClickListener {
            filterTransactions("Savings", transactionsList, adapter!!)
        }
    }

    private fun filterTransactions(category: String, transactionsList: List<Transaction>, adapter: TransactionsAdapter) {
        val filteredList = transactionsList.filter { it.transactionCategory == category }
        adapter.updateData(filteredList)
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
                return position != 0
            }
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item)

        spinner.adapter = adapter

        builder.setPositiveButton("Confirm") { dialog, which ->
            val selectedCategory = spinner.selectedItem.toString()
            transaction.transactionCategory = selectedCategory
            // Notify the adapter that the data has changed
            recyclerView?.adapter?.notifyItemChanged(position)
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun getTransactionsList(): List<Transaction> {
        val transactionsList = mutableListOf<Transaction>()
        transactionsList.add(Transaction("1","Breaker", "", "+$100", "2021-09-01"))
        transactionsList.add(Transaction("2","Booking" ,"", "-$50", "2021-09-02"))
        transactionsList.add(Transaction("3","Booking" ,"", "+$100", "2021-09-03"))
        transactionsList.add(Transaction("4","Amir" ,"", "-$50", "2021-09-04"))
        transactionsList.add(Transaction("5", "son","", "+$100", "2021-09-05"))
        transactionsList.add(Transaction("6","ahmad" ,"", "-$50", "2021-09-06"))
        transactionsList.add(Transaction("7","Ali" ,"", "+$100", "2021-09-07"))
        transactionsList.add(Transaction("8","Open Ai" ,"", "-$50", "2021-09-08"))
        transactionsList.add(Transaction("9", "Facebook","", "+$4100", "2021-09-09"))
        transactionsList.add(Transaction("10","Netflix" ,"", "-$50", "2021-09-10"))
        return transactionsList.toList()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}