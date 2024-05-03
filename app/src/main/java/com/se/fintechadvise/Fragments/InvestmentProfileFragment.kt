package com.smd.surmaiya.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.se.fintechadvise.AdapterClasses.InvestmentHistoryAdapter
import com.se.fintechadvise.DataClasses.InvestmentPerformance
import com.se.fintechadvise.HelperClasses.CustomToastMaker
import com.se.fintechadvise.R

class InvestmentProfileFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }
    private fun getInvestmentPerformances(): List<InvestmentPerformance> {
        return listOf(
            InvestmentPerformance("22/10/2024", 25.0),
            InvestmentPerformance("10/10/2024", 23.0),
            InvestmentPerformance("01/10/2024", 20.0),
            // Add more InvestmentPerformance objects as needed
        )
    }
    private fun setProfileData(view: View) {
        // Set the profile data in the view

    }

    private fun handleInvestNowButtonClick(view:View) {
        // Handle the Invest Now button click
        val investButton = view.findViewById<Button>(R.id.investButton)
        investButton?.setOnClickListener {
                val amount = view.findViewById<EditText>(R.id.investmentAmount).text.toString()
            if(amount.isEmpty()||amount.isBlank()){
                CustomToastMaker().showToast(requireContext(),"Please Enter the Amount to Invest")
            }
            else if(amount.toDouble()<0.5){
                CustomToastMaker().showToast(requireContext(),"Minimum Investment is $0.50")
            }
            else{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm Investment")
                builder.setMessage("Are you sure you want to invest $${amount} in this stock?")
                builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                    CustomToastMaker().showToast(requireContext(),"Investment Successful")
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_investment_profile, container, false)
        val investmentPerformances: List<InvestmentPerformance> = getInvestmentPerformances()

        setProfileData(view)
        handleInvestNowButtonClick(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.investmentHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = InvestmentHistoryAdapter(investmentPerformances)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dropdownButton = view.findViewById<Button>(R.id.dropdownButton)
        dropdownButton.setOnClickListener {
            dismiss() // This will close the bottom sheet when the dropdown button is clicked
        }

        var bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED

        //set minimimum height to parent height
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}