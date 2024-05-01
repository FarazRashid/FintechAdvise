package com.se.fintechadvise.Activities
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.se.fintechadvise.R

class PrivacyPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_privacy_policy)

        val layout = findViewById<LinearLayout>(R.id.linearLayout2)
        animateChildren(layout)
    }

    private fun animateChildren(viewGroup: ViewGroup) {
        val delay = 500L // delay in ms before the next view starts animating
        var currentDelay = 0L // start delay for the first view

        for (child in viewGroup.children) {
            child.alpha = 0f // make the view initially invisible
            child.animate()
                .alpha(1f) // animate to fully visible
                .setStartDelay(currentDelay)
                .setDuration(1500) // duration of the animation
                .start()

            currentDelay += delay
        }
    }
}