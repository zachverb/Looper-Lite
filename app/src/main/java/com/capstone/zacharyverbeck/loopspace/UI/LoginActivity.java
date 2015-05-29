package com.capstone.zacharyverbeck.loopspace.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.capstone.zacharyverbeck.loopspace.API.ServerAPI;
import com.capstone.zacharyverbeck.loopspace.Java.GlobalFunctions;
import com.capstone.zacharyverbeck.loopspace.Models.Data;
import com.capstone.zacharyverbeck.loopspace.Models.User;
import com.capstone.zacharyverbeck.loopspace.R;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    public ButtonRectangle mSignUpButton;
    public ButtonRectangle mLoginButton;

    public EditText mEmailField;
    public EditText mPasswordField;

    public ProgressBar mLoadingBar;

    public ServerAPI service;

    public String TAG = "LoginActivity";

    public GlobalFunctions mGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init() {
        mSignUpButton = (ButtonRectangle) findViewById(R.id.signUpButton);
        mLoginButton = (ButtonRectangle) findViewById(R.id.logInButton);

        mSignUpButton.setRippleSpeed(100f);
        mLoginButton.setRippleSpeed(100f);

        mEmailField = (EditText) findViewById(R.id.usernameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mPasswordField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    login();
                    return true;
                }
                return false;
            }
        });

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://secret-spire-6485.herokuapp.com/")
                .build();

        service = restAdapter.create(ServerAPI.class);

        mGlobal = new GlobalFunctions(this);
        mGlobal.setupUI(findViewById(R.id.parent));


        //toolbar.inflateMenu(R.menu.menu_login);

    }

    public void login() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        final ProgressDialog loginDialog = new ProgressDialog(LoginActivity.this);
        loginDialog.setIndeterminate(true);
        loginDialog.setTitle("Please Wait");
        loginDialog.setMessage("Logging in");
        loginDialog.show();


        service.authenticate(new User(email, password), new Callback<Data>() {
            @Override
            public void success(Data data, Response response) {
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                Log.d(TAG, data.type + data.token);
                if (data.error == null && data.type) {
                    mGlobal.saveToken(data.token);
                    mGlobal.saveUserId(data.id);
                    Intent intent = new Intent(LoginActivity.this, TrackListActivity.class);
                    startActivity(intent);
                } else {
                    Dialog dialog = new Dialog(LoginActivity.this, "Error!", data.error);
                    dialog.show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                Dialog dialog = new Dialog(LoginActivity.this, "Error!", "Network error!");
                dialog.show();

                retrofitError.printStackTrace();
            }
        });
    }

}
