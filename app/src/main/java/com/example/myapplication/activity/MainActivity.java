package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.myapplication.R;
import com.example.myapplication.utility.TestApplication;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import com.example.myapplication.fragment.EditUser;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Allows the user to see the menu on the left. It contains the user details and a list
 * of the fragments available
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    /**
     * It handles the creation of the activity initializating the needed objects
     */
    @SuppressLint("RtlHardcoded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        TextView username = header.findViewById(R.id.tvUsername);
        TextView name = header.findViewById(R.id.tvName);
        TextView surname = header.findViewById(R.id.tvSurname);
        TextView email = header.findViewById(R.id.tvEmail);
        ImageView settings = header.findViewById(R.id.ivSettings);
        ImageView logout = header.findViewById(R.id.ivLogout);

        username.setText(TestApplication.user.getProperty("username").toString());
        name.setText(TestApplication.user.getProperty("name").toString());
        surname.setText(TestApplication.user.getProperty("surname").toString());
        email.setText(TestApplication.user.getProperty("email").toString());

        settings.setOnClickListener(v -> {
            EditUser dest = new EditUser();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, dest);
            fragmentTransaction.commit();
            drawer.closeDrawer(Gravity.LEFT);
        });

        logout.setOnClickListener(v -> Backendless.UserService.logout(new AsyncCallback<Void>()
        {
            public void handleResponse( Void response )
            {
                // user has been logged out.
                Intent i = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(i);
            }

            public void handleFault( BackendlessFault fault )
            {
                Log.e("logout", fault.getMessage());
            }
        }));

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TestApplication.hideSoftKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_groups, R.id.nav_invitations)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * It handles the navigation among the fragments
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
