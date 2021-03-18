package com.example.myapplication.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    ArrayList<Marker> bestMarkers;
    LatLng bestpoint;
    int radius;

    /**
     * executes the url request using the read method of the Http class, returns the place data in json format
     */
    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            bestMarkers = (ArrayList<Marker>) inputObj[2];
            bestpoint = (LatLng) inputObj[3];
            radius = (int) inputObj[4];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    /**
     * runs the PlacesDisplayTask class
     */
    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[5];
        toPass[0] = googleMap;
        toPass[1] = result;
        toPass[2] = bestMarkers;
        toPass[3] = bestpoint;
        toPass[4] = radius;
        placesDisplayTask.execute(toPass);
    }
}