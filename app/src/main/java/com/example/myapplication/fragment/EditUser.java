package com.example.myapplication.fragment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.activity.Login;
import com.example.myapplication.utility.TestApplication;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

/**
 * this class is responsible for all the management of the edit user phase
 */
public class EditUser extends Fragment {

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    private EditText etName,etMail,etNewPassword,etConfirmPassword,etUsername,etSurname, etAddress;
    private AutocompleteSupportFragment autocompleteFragment;
    private final String[] address = new String[2];

    /**
     * It handles the initialization of the needed objects
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);

        //components needed for the loading bar
        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        tvLoad = view.findViewById(R.id.tvLoad);

        //identify all the elements of the layout where the user enters their data
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
        etMail = view.findViewById(R.id.etMail);
        etUsername = view.findViewById(R.id.etUsername);
        etAddress = view.findViewById(R.id.etAddress);
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword.setVisibility(View.GONE);
        Button btnEdit = view.findViewById(R.id.btnEdit);

        etName.setText(TestApplication.user.getProperty("name").toString());
        etSurname.setText((TestApplication.user.getProperty("surname").toString()));
        etMail.setText(TestApplication.user.getEmail());
        etUsername.setText((TestApplication.user.getProperty("username").toString()));
        etAddress.setText(TestApplication.place.getFull_address());
        // not possible to get password from backendless
        etNewPassword.setText("password");

        Places.initialize(requireContext(), getString(R.string.google_maps_key));
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
                .hide(autocompleteFragment)
                .commit();

        // when the input field of the address is clicked, I start the Google widget
        // (through the appropriate API) to search for the address
        etAddress.setOnClickListener(v -> {
            FragmentManager fm1 = getChildFragmentManager();
            fm1.beginTransaction()
                    .show(autocompleteFragment)
                    .commit();
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS));
            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    etAddress.setText(place.getAddress());
                    address[0] = place.getId();
                    address[1] = place.getAddress();
                    FragmentManager fm1 = getChildFragmentManager();
                    fm1.beginTransaction()
                            .hide(autocompleteFragment)
                            .commit();
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // if the button confirming the edit is clicked, it will be checked that the user
        // has entered all the data correctly and then the user will enter the relevant data in the database
        btnEdit.setOnClickListener(v -> {
            // if the users has typed wrong values
            if (etName.getText().toString().isEmpty()
                    || etMail.getText().toString().isEmpty()
                    || etUsername.getText().toString().isEmpty()
                    || etSurname.getText().toString().isEmpty()
                    || etAddress.getText().toString().isEmpty())
            {
                Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_LONG).show();
            } else {
                // if the users has typed a correct corrispondence of password or has not changed
                if ((etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())
                        && !etNewPassword.getText().toString().isEmpty())
                        || (etNewPassword.getText().toString().equals("password")
                        && etConfirmPassword.getText().toString().equals(""))) {

                    String name = etName.getText().toString().trim();
                    String surname = etSurname.getText().toString().trim();
                    String email = etMail.getText().toString().trim();
                    String username = etUsername.getText().toString().trim();

                    BackendlessUser user = TestApplication.user;
                    user.setEmail(email);
                    user.setProperty("name", name);
                    user.setProperty("username", username);
                    user.setProperty("surname", surname);

                    // if password has been replaced
                    String password;
                    if (!etConfirmPassword.getText().toString().equals("")) {
                        password = etNewPassword.getText().toString().trim();
                        user.setPassword(password);
                    }

                    com.example.myapplication.data.Place p1 = TestApplication.place;
                    p1.setFull_address(address[1]);
                    p1.setId_google_place(address[0]);

                    TestApplication.hideSoftKeyboard(requireActivity());
                    showProgress(true);

                    StringBuilder whereClause = new StringBuilder();
                    whereClause.append("objectId='").append(TestApplication.place.getObjectId()).append("'");
                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause.toString());
                    Log.i("query_user_place", whereClause.toString());
                    Backendless.Data.of(com.example.myapplication.data.Place.class)
                            .find(queryBuilder, new AsyncCallback<List<com.example.myapplication.data.Place>>() {
                                @Override
                                public void handleResponse(List<com.example.myapplication.data.Place> place) {
                                    Log.i("update", "Loaded object. Address- " + place.get(0).getFull_address());

                                    Backendless.Data.of(com.example.myapplication.data.Place.class)
                                            .save(p1, new AsyncCallback<com.example.myapplication.data.Place>() {
                                                @Override
                                                public void handleResponse(com.example.myapplication.data.Place updatedPlace) {
                                                    TestApplication.place = updatedPlace;
                                                    Log.i("updateplace", "place address after update " + updatedPlace.getFull_address());
                                                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void handleResponse(BackendlessUser response) {
                                                            TestApplication.user = response;
                                                            Intent intent = new Intent(getContext(), Login.class);
                                                            startActivity(intent);
                                                            Toast.makeText(getContext(), "User successfully updated", Toast.LENGTH_LONG).show();
                                                            showProgress(false);
                                                        }

                                                        @Override
                                                        public void handleFault(BackendlessFault backendlessFault) {
                                                            System.out.println("Server reported an error - " + backendlessFault.getMessage());
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Log.e("errorplace", fault.getMessage());
                                                }
                                            });
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e("erroruserwrong", fault.getMessage());
                                }
                            });
                }
                else {
                    Toast.makeText(getContext(), "Please make sure new password and confirm password are the same", Toast.LENGTH_LONG).show();
                }
            }

            TestApplication.hideSoftKeyboard(requireActivity());
        });

        // layout change password
        etNewPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etNewPassword.setText("");
                etConfirmPassword.setVisibility(View.VISIBLE);
            } else {
                if(etNewPassword.getText().toString().equals("")) {
                    etNewPassword.setText("password");
                    etConfirmPassword.setText("");
                    etConfirmPassword.setVisibility(View.GONE);
                }

            }
        });

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                view.setVisibility(View.GONE);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
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


