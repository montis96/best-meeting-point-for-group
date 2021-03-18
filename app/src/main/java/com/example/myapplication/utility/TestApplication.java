package com.example.myapplication.utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.data.Place;

import java.util.List;

public class TestApplication extends Application {
    public static final String APPLICATION_ID = "<APPLICATION ID HERE>";
    public static final String API_KEY = "<API KEY HERE>";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user; // logged user
    public static List<BackendlessUser> users_active; // users taken into account in bmp calculation in a specified group
    public static List<BackendlessUser> usersAll; // all users in a specified group

    public static List<Group_Place_User> group_place_user; // group_place_user of the logged user
    public static List<Group_Place_User> group_place_user_groups; //group_place_user of the group selected

    public static List<Group> invitation_group; // invitation of the logged user
    public static Group group; // group selected
    public static List<Group> groups;

    public static Place place; // place of the logged user
    public static List<Place> placesAll; // all places in a specified group
    public static List<Place> places_active; // places taken into account in bmp calculation in a specified group
    public static List<Place> best_places; //bmp places
    public static Place final_group_place; //best place voted

    public static final String[] kind_codes = {"bar", "cafe", "movie_theater", "night_club", "park", "restaurant"};
    public static final String[] kinds = {"Bar", "Cafe", "Movie Theater", "Night Club", "Park", "Restaurant"};

    /**
     * it creates the link with the database
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(),
                APPLICATION_ID,
                API_KEY);
    }

    /**
     * it hides the keyboard
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Check if every user has voted the best place
     * @return true = yes, false = no
     */
    public static boolean check_best_place() {
        boolean flag = true;
        for (Group_Place_User grTemp : group_place_user_groups) {
            flag = flag && grTemp.getVoted();
            Log.i("voto", "" + grTemp.getVoted());

        }
        if (flag) {
            int max = -1;
            for (Place placeTemp : best_places) {
                if (placeTemp.getVotes() > max) {
                    TestApplication.final_group_place = placeTemp;
                    max = placeTemp.getVotes();
                }
            }
        }
        return flag;
    }

}

