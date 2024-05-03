/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.se.fintechadvise.linksample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.plaid.link.FastOpenPlaidLink;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidHandler;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkSuccess;
import com.se.fintechadvise.Activities.OnboardingActivity;
import com.se.fintechadvise.R;
import com.se.fintechadvise.linksample.network.LinkTokenRequester;
import kotlin.Unit;


public class MainActivityJava extends AppCompatActivity {

  private TextView result;
  private TextView tokenResult;
  private Button actionButton;

  private PlaidHandler plaidHandler = null;

  private ImageView backbutton;

  private ActivityResultLauncher<PlaidHandler> linkAccountToPlaid = registerForActivityResult(
      new FastOpenPlaidLink(),
      result -> {
        if (result instanceof LinkSuccess) {
          showSuccess((LinkSuccess) result);
        } else {
          showFailure((LinkExit) result);
        }
      });

  private void showSuccess(LinkSuccess success) {
    tokenResult.setText(getString(R.string.public_token_result, success.getPublicToken()));
    result.setText(getString(R.string.content_success));
  }

  private void showFailure(LinkExit exit) {
    tokenResult.setText("");
    if (exit.getError() != null) {
      result.setText(getString(
          R.string.content_exit,
          exit.getError().getDisplayMessage(),
          exit.getError().getErrorCode()));
    } else {
      result.setText(getString(
          R.string.content_cancel,
          exit.getMetadata().getStatus() != null ? exit.getMetadata().getStatus().getJsonValue() : "unknown"));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect_bank);
    result = findViewById(R.id.result);
    tokenResult = findViewById(R.id.public_token_result);
    backbutton = findViewById(R.id.backButton);

    actionButton = findViewById(R.id.action_button);
    actionButton.setOnClickListener(v -> {
      setOptionalEventListener();
      prepareLink();
      openLink();
    });

    backbutton.setOnClickListener(v -> {
      onBackPressed();
    });
  }

  private void prepareLink() {
    LinkTokenRequester.INSTANCE.getToken()
        .subscribe(this::onLinkTokenSuccess, this::onLinkTokenError);
  }

  /**
   * Optional, set an <a href="https://plaid.com/docs/link/android/#handling-onevent">event listener</a>.
   */
  private void setOptionalEventListener() {
    Plaid.setLinkEventListener(linkEvent -> {
      Log.i("Event", linkEvent.toString());
      return Unit.INSTANCE;
    });
  }

  /**
   * For all Link configuration options, have a look at the
   * <a href="https://plaid.com/docs/link/android/#parameter-reference">parameter reference</>
   */
  private void openLink() {

    linkAccountToPlaid.launch(plaidHandler);
  }

  private void onLinkTokenSuccess(String token) {

    LinkTokenConfiguration configuration = new LinkTokenConfiguration.Builder()
        .token(token)
        .build();
    plaidHandler = Plaid.create(this.getApplication(), configuration);
  }

  private void onLinkTokenError(Throwable error) {
    if (error instanceof java.net.ConnectException) {
      Toast.makeText(
          this,
          "Please run `sh start_server.sh <client_id> <sandbox_secret>`",
          Toast.LENGTH_LONG).show();
      return;
    }
    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_java, menu);
    return true;
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
//      case R.id.show_kotlin:
//        Intent intent = new Intent(this, OnboardingActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
