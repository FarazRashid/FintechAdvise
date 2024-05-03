/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.se.fintechadvise.linksample;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.plaid.link.Plaid
import com.plaid.link.PlaidHandler
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkResultHandler
import com.se.fintechadvise.Activities.OnboardingActivity
import com.se.fintechadvise.R
import com.se.fintechadvise.linksample.network.LinkTokenRequester

/**
 * Old approach to opening Plaid Link, we recommend switching over to the
 * OpenPlaidLink ActivityResultContract instead.
 */
class MainActivityStartActivityForResult : AppCompatActivity() {

  private lateinit var result: TextView
  private lateinit var tokenResult: TextView
  private var plaidHandler: PlaidHandler? = null
  private lateinit var actionButton: Button
  private lateinit var backButton:Button
  private lateinit var constraintLayout: ConstraintLayout


  private val myPlaidResultHandler by lazy {
    LinkResultHandler(
      onSuccess = {
        tokenResult.text = getString(R.string.public_token_result, it.publicToken)
        result.text = getString(R.string.content_success)
        constraintLayout.visibility=View.GONE
      },
      onExit = {
        tokenResult.text = ""
        if (it.error != null) {
          result.text = getString(
            R.string.content_exit,
            it.error?.displayMessage,
            it.error?.errorCode
          )
          constraintLayout.visibility=View.GONE
        } else {
          result.text = getString(
            R.string.content_cancel,
            it.metadata.status?.jsonValue ?: "unknown"
          )
          constraintLayout.visibility=View.GONE
        }
      }
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_connect_bank)
    result = findViewById(R.id.result)
    tokenResult = findViewById(R.id.public_token_result)
    backButton = findViewById(R.id.backButton)
    constraintLayout = findViewById(R.id.TrustTextView)

    actionButton = findViewById(R.id.action_button)
    actionButton.setOnClickListener {
      setOptionalEventListener()
      if (plaidHandler != null) {
        openLink()
      } else {
        prepareLink()
      }
    }

    backButton.setOnClickListener {
      val intent = Intent(this, OnboardingActivity::class.java)
      startActivity(intent)
    }
  }

  private fun prepareLink() {
    LinkTokenRequester.token.subscribe(::onLinkTokenSuccess, ::onLinkTokenError)
  }
  /**
   * Optional, set an [event listener](https://plaid.com/docs/link/android/#handling-onevent).
   */
  private fun setOptionalEventListener() = Plaid.setLinkEventListener { event ->
    Log.i("Event", event.toString())
  }

  /**
   * For all Link configuration options, have a look at the
   * [parameter reference](https://plaid.com/docs/link/android/#parameter-reference).
   */
  private fun openLink() {

    plaidHandler?.open(this)
  }

  private fun onLinkTokenSuccess(linkToken: String) {
    val tokenConfiguration = LinkTokenConfiguration.Builder()
      .token(linkToken)
      .build()
    plaidHandler = Plaid.create(
      this.application,
      tokenConfiguration
    )

  }

  private fun onLinkTokenError(error: Throwable) {
    if (error is java.net.ConnectException) {
      Toast.makeText(this, "Please run `sh start_server.sh <client_id> <sandbox_secret>`", Toast.LENGTH_LONG).show()
      return
    }
    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
  }

  @Suppress("DEPRECATION")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (!myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
      Log.i(OnboardingActivity::class.java.simpleName, "Not handled")
    }
  }

}
