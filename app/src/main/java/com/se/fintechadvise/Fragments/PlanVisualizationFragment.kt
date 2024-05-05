package com.se.fintechadvise.Fragments

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
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
    private var overallbalance: TextView? = null
    private var categoryTextView: TextView? = null

    private var activeFilters = mutableMapOf<String, String>()
    private lateinit var barChart: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    class CustomValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val weeks = arrayOf("week1", "week2", "week3", "week4")
            val spannableString = SpannableString(weeks[value.toInt()])
            spannableString.setSpan(TypefaceSpan("your_font_family"), 0, spannableString.length, 0)
            return spannableString.toString()
        }

        override fun getBarLabel(barEntry: BarEntry?): String {
            return "$" + super.getBarLabel(barEntry)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_plan_visualization, container, false)
        overallbalance = view.findViewById<TextView>(R.id.bankBalanceTextView)
        categoryTextView = view.findViewById<TextView>(R.id.textView17)

        getTransactionsList()
        barChart = view.findViewById<BarChart>(R.id.barChart)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 5f))
        entries.add(BarEntry(1f, 2f))
        entries.add(BarEntry(2f, 3f))
        entries.add(BarEntry(3f, 4f))

        val dataSet = BarDataSet(entries, "Label") // add entries to dataset
        val colors = intArrayOf(
            resources.getColor(R.color.spending),
            resources.getColor(R.color.income),
            resources.getColor(R.color.bills),
            resources.getColor(R.color.savings)
        )
        dataSet.colors = colors.toList()
        dataSet.valueTextSize = 18f // increase value text size
        dataSet.valueTextColor = resources.getColor(R.color.icon_secondary) // change value text color
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f // set custom bar width
        barChart.data = barData
        barChart.invalidate() // refresh

        // Customize the chart
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(50)
        barChart.setPinchZoom(false)

        // Disable grid lines
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)

        // Set grid line color to icon_secondary
        barChart.xAxis.gridColor = resources.getColor(R.color.icon_secondary)
        barChart.axisLeft.gridColor = resources.getColor(R.color.icon_secondary)
        barChart.axisRight.gridColor = resources.getColor(R.color.icon_secondary)

        // Set labels for X-axis
        barChart.xAxis.valueFormatter = CustomValueFormatter()

        // Set granularity for X-axis
        barChart.xAxis.granularity = 1f

        // Remove X-axis line on top
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisRight.isEnabled = false

        barChart.legend.isEnabled = false

        // Add rounded bar effect and animation
        barChart.animateY(1000) // animate the Y axis for 1000 milliseconds

        dataSet.setColors(R.color.icon_secondary)
        barChart.xAxis.textColor = resources.getColor(R.color.icon_secondary)
        barChart.axisLeft.textColor = resources.getColor(R.color.icon_secondary)
        barChart.axisRight.textColor = resources.getColor(R.color.icon_secondary)

        // Set the color of the sidebar

        setupSortSpinner(view)


        filterTransactionClickListener(view)

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
                filterTransactionsByMonthAndCalculateTotal(selectedMonth)
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
    private fun filterTransactions(category: String) {
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

        //now we need to draw the bar chart

        // Divide the transactions into 4 weeks
        val weeks = arrayOfNulls<List<Transaction>>(4)

        for (i in 0..3) {
            weeks[i] = filteredList?.filter {
                val transactionDate = it.transactionDate
                val transactionDay = transactionDate.split("/")[0].toInt()
                transactionDay in (i * 7 + 1)..((i + 1) * 7)
            }
        }

        val amounts = FloatArray(4)

        for (i in 0..3) {
            amounts[i] = weeks[i]?.map { it.transactionAmount.replace( "+$", "").replace( "-$", "").toFloat() }?.sum() ?: 0f
        }

        // Display each week on the bar chart with the total amount
        val entries = ArrayList<BarEntry>()

        for (i in 0..3) {
            entries.add(BarEntry(i.toFloat(), amounts[i]))
        }

        val dataSet = BarDataSet(entries, "Label") // add entries to dataset

        //set colours from         dataSet.setColors(R.color.spending,R.color.bills,R.color.income,R.color.savings) // set color template
        val colors = intArrayOf(
            resources.getColor(R.color.spending),
            resources.getColor(R.color.income),
            resources.getColor(R.color.bills),
            resources.getColor(R.color.savings)
        )
        dataSet.colors = colors.toList()
        dataSet.valueTextSize = 18f // increase value text size
        dataSet.valueTextColor = resources.getColor(R.color.icon_secondary) // change value text color
        val barData = BarData(dataSet)
        barChart.data = barData
        overallbalance?.text = "$${amounts.sum()}"
        categoryTextView?.text = "$category Balance"
        barChart.invalidate() // refresh


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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterTransactionsByMonthAndCalculateTotal(month: String) {
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

        Log.d("TransactionsFragment", "filteredList: $filteredList")

        // Divide the transactions into 4 weeks
        val weeks = arrayOfNulls<List<Transaction>>(4)
        for (i in 0..3) {
            weeks[i] = filteredList?.filter {
                val transactionDate = it.transactionDate
                val transactionDay = transactionDate.split("/")[0].toInt()
                transactionDay in (i * 7 + 1)..((i + 1) * 7)
            }
        }

        Log.d("TransactionsFragment", "weeks: $weeks")

        val amounts = FloatArray(4)
        for (i in 0..3) {
            amounts[i] = weeks[i]?.map { it.transactionAmount.replace( "+$", "").replace( "-$", "").toFloat() }?.sum() ?: 0f
        }

        Log.d("TransactionsFragment", "amounts: $amounts")

        // Display each week on the bar chart with the total amount
        val entries = ArrayList<BarEntry>()
        for (i in 0..3) {
            entries.add(BarEntry(i.toFloat(), amounts[i]))
        }

        Log.d("TransactionsFragment", "entries: $entries")

        val dataSet = BarDataSet(entries, "Label") // add entries to dataset

        //set colours from         dataSet.setColors(R.color.spending,R.color.bills,R.color.income,R.color.savings) // set color template
        val colors = intArrayOf(
            resources.getColor(R.color.spending),
            resources.getColor(R.color.income),
            resources.getColor(R.color.bills),
            resources.getColor(R.color.savings)
        )
        dataSet.colors = colors.toList()
        dataSet.valueTextSize = 18f // increase value text size
        dataSet.valueTextColor = resources.getColor(R.color.icon_secondary) // change value text color
        val barData = BarData(dataSet)
        barChart.data = barData
        overallbalance?.text = "$${amounts.sum()}"
        categoryTextView?.text = "Overall Balance"
        barChart.invalidate() // refresh
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun filterTransactionClickListener(view: View?) {
        val spendingImageView = view?.findViewById<ImageView>(R.id.spendingImageView)
        val spendingTextView = view?.findViewById<TextView>(R.id.spendingTextView)
        val incomeImageView = view?.findViewById<ImageView>(R.id.incomeImageView)
        val incomeTextView = view?.findViewById<TextView>(R.id.incomeTextView)
        val billsImageView = view?.findViewById<ImageView>(R.id.billsImageView)
        val billsTextView = view?.findViewById<TextView>(R.id.billsTextView)
        val savingsImageView = view?.findViewById<ImageView>(R.id.savingsImageView)
        val savingsTextView = view?.findViewById<TextView>(R.id.savingsTextView)
        val viewAllTextView = view?.findViewById<TextView>(R.id.viewAllTextView)



        spendingImageView?.setOnClickListener {
            filterTransactions("Spending")
        }
        spendingTextView?.setOnClickListener {
            filterTransactions("Spending")
        }

        incomeTextView?.setOnClickListener {
            filterTransactions("Income")
        }
        incomeImageView?.setOnClickListener {
            filterTransactions("Income")
        }

        billsTextView?.setOnClickListener {
            filterTransactions("Bills")
        }
        billsImageView?.setOnClickListener {
            filterTransactions("Bills")
        }

        savingsTextView?.setOnClickListener {
            filterTransactions("Savings")
        }
        savingsImageView?.setOnClickListener {
            filterTransactions("Savings")
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