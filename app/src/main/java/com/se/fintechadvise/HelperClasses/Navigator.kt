package com.se.fintechadvise.HelperClasses

import android.content.Context
import android.content.Intent

class Navigator {


    companion object {
        fun navigateToActivity(context: Context, destination: Class<*>) {
            val intent = Intent(context, destination)
            context.startActivity(intent)
        }
    }
}