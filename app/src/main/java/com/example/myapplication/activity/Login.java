package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.myapplication.R;

import com.example.myapplication.data.Place;
import com.example.myapplication.utility.TestApplication;

import java.util.List;

public class Login extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    EditText etMail, etPassword;
    Button btnLogin, btnRegister;
    TextView tvReset;

    /**
     * This is the first activity that starts when you open the app. You will be presented with
     * the possibility of logging in (after entering your email and password),
     * of switching to the activity of registering a new account or of requesting a new password
     * (after entering your email).
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvReset = findViewById(R.id.tvReset);

        // the following line is for automatic login with the loading bar
        showProgress(true);

        /*
         *if the login button is clicked, it will be made with the relative API call
         * (after checking that mail and password are present)
         */
        btnLogin.setOnClickListener(view -> {
            if (etMail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                Toast.makeText(Login.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
            } else {

                String email = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                TestApplication.hideSoftKeyboard(Login.this);
                showProgress(true);
                tvLoad.setText("Busy logging you in...please wait...");

                Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        TestApplication.user = response;
                        String where= "ownerId='"+TestApplication.user.getObjectId()+"'";
                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                        queryBuilder.setWhereClause(where);

                        Backendless.Persistence.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
                            @Override
                            public void handleResponse(List<Place> response) {
                                Log.i("posto",response.get(0).getFull_address());
                                TestApplication.place = response.get(0);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                            }
                        });

                        Toast.makeText(Login.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Login.this.finish();

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                        showProgress(false);

                    }
                }, true);

            }
        });

        /*
         * if the registration button is clicked, it will go to the relative "Register" activity.
         */
        btnRegister.setOnClickListener(view -> startActivity(new Intent(Login.this, Register.class)));

        /*
         * if the reset password link is clicked, after checking that the email has been entered,
         * the reset email will be sent for the appropriate API call.
         */
        tvReset.setOnClickListener(view -> {
            if (etMail.getText().toString().isEmpty())
            {
                Toast.makeText(Login.this, "Please enter your email address in the email field!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String email = etMail.getText().toString().trim();

                showProgress(true);
                tvLoad.setText("Busy sending reset instructions...please wait...");
                TestApplication.hideSoftKeyboard(Login.this);

                Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {

                        Toast.makeText(Login.this, "Reset instructions sent to email address!", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                        showProgress(false);

                    }
                });
            }

        });


        tvLoad.setText("Checking login credentials...please wait...");
        /*
         * check if the last login is still valid. If yes, I login directly without requesting insertion
         */
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void handleResponse(Boolean response) {

                if (response)
                {
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();

                    tvLoad.setText("Logging you in...please wait...");
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {

                            TestApplication.user = response;
                            String where= "ownerId='"+TestApplication.user.getObjectId()+"'";
                            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                            queryBuilder.setWhereClause(where);

                            Backendless.Persistence.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
                                @Override
                                public void handleResponse(List<Place> response) {
                                    Log.i("posto", response.get(0).getFull_address());
                                    TestApplication.place = response.get(0);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                                }
                            });
                            startActivity(new Intent(Login.this, MainActivity.class));
                            Login.this.finish();

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);

                        }
                    });
                }
                else
                {
                    showProgress(false);
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(Login.this, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

        //fine login automatico
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

        tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
        tvLoad.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
