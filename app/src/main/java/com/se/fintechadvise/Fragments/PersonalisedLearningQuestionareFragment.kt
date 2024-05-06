package com.se.fintechadvise.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import com.se.fintechadvise.HelperClasses.CustomToastMaker
import com.se.fintechadvise.HelperClasses.FragmentHelper
import com.se.fintechadvise.ManagerClasses.PersonalisedLearningManager
import com.se.fintechadvise.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalisedLearningQuestionareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalisedLearningQuestionareFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(
            R.layout.fragment_personalised_learning_questionare,
            container,
            false
        )

        setupBackButton(view)
        setupSubmitButton(view)


        return view
    }

    private fun isFormValid(view: View): Boolean {
        val goalsEditText: EditText = view.findViewById(R.id.goalsEditText)
        val compoundInterestAnswer: EditText = view.findViewById(R.id.compoundInterestAnswer)
        val diversificationAnswer: EditText = view.findViewById(R.id.diversificationAnswer)
        val riskReturnTradeoffAnswer: EditText = view.findViewById(R.id.riskReturnTradeoffAnswer)
        val riskToleranceRadioGroup: RadioGroup = view.findViewById(R.id.riskToleranceRadioGroup)

        // Check if the EditText fields are not empty
        if (goalsEditText.text.toString().trim().isEmpty()) {
            goalsEditText.error = "This field cannot be empty"
            return false
        }

        if (compoundInterestAnswer.text.toString().trim().isEmpty()) {
            compoundInterestAnswer.error = "This field cannot be empty"
            return false
        }

        if (diversificationAnswer.text.toString().trim().isEmpty()) {
            diversificationAnswer.error = "This field cannot be empty"
            return false
        }

        if (riskReturnTradeoffAnswer.text.toString().trim().isEmpty()) {
            riskReturnTradeoffAnswer.error = "This field cannot be empty"
            return false
        }

        // Check if a RadioButton is selected in the RadioGroup
        if (riskToleranceRadioGroup.checkedRadioButtonId == -1) {
            CustomToastMaker().showToast(requireContext(),"Please select an option")
            return false
        }

        return true
    }

    private fun setupSubmitButton(view: View?) {
        view?.findViewById<Button>(R.id.submitFormButton)?.setOnClickListener {
            if (isFormValid(view)) {
                PersonalisedLearningManager.setPersonalisedLearningEnabled(true)
                FragmentHelper(requireActivity().supportFragmentManager,requireContext()).loadFragment(EducationHomeFragment())
            } else {
                CustomToastMaker().showToast(requireContext(),"Please fill all the fields")
            }

           }
    }

    private fun setupBackButton(view: View?) {
        view?.findViewById<ImageView>(R.id.backButton)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalisedLearningQuestionareFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonalisedLearningQuestionareFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}