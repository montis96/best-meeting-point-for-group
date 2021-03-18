package com.example.myapplication.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.myapplication.R;
import com.example.myapplication.activity.MapsActivity;
import com.example.myapplication.data.Place;
import com.example.myapplication.parser.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    ArrayList<Marker> bestMarkers;
    LatLng bestpoint;
    int radius;

    /**
     * takes as input the data in json format and through the Places class it appears
     */
    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            bestMarkers = (ArrayList<Marker>) inputObj[2];
            bestpoint = (LatLng) inputObj[3];
            radius = (int) inputObj[4];
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }


    /**
     * put a marker on the map for all the Places found, writing the name and street for each one
     */
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        if (list.size()>2 && list.size()<11){

            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.alpha(0.5f);

                if (MapsActivity.getFirst_click()) {
                    //Creation of place for database
                    Place temp_place = new Place();
                    temp_place.setName(googlePlace.get("place_name"));
                    temp_place.setFull_address(googlePlace.get("vicinity"));
                    temp_place.setId_google_place(googlePlace.get("reference"));
                    temp_place.setVotes(0);
                    temp_place.saveAsync(new AsyncCallback<Place>() {
                        @Override
                        public void handleResponse(Place response) {
                            TestApplication.best_places.add(response);
                            Log.i("place_saved", response.getFull_address());
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e("place_saved", fault.getMessage());
                        }
                    });
                }
                if (!MapsActivity.ultimo_passaggio)
                    bestMarkers.add(googleMap.addMarker(markerOptions));
                else if (TestApplication.final_group_place.getName().equals(googlePlace.get("place_name")))
                    bestMarkers.add(googleMap.addMarker(markerOptions));
            }

        } else if (list.size()<3){
            radius = (int) Math.floor(radius + (radius/2));

            StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + bestpoint.latitude + "," + bestpoint.longitude);
            googlePlacesUrl.append("&radius=" + radius);
            googlePlacesUrl.append("&types=" + TestApplication.group.getType());
            googlePlacesUrl.append("&key=" + "AIzaSyByyPXoo6la_E0E5MR7kLUL6Vh_YKgrLLg");

            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            Object[] toPass = new Object[5];
            toPass[0] = googleMap;
            toPass[1] = googlePlacesUrl.toString();
            toPass[2] = bestMarkers;
            toPass[3] = bestpoint;
            toPass[4] = radius;
            googlePlacesReadTask.execute(toPass);

        } else {
            radius = (int) Math.floor(radius - (radius/2));

            StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + bestpoint.latitude + "," + bestpoint.longitude);
            googlePlacesUrl.append("&radius=" + radius);
            googlePlacesUrl.append("&types=" + TestApplication.group.getType());
            googlePlacesUrl.append("&key=" + "AIzaSyByyPXoo6la_E0E5MR7kLUL6Vh_YKgrLLg");

            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            Object[] toPass = new Object[5];
            toPass[0] = googleMap;
            toPass[1] = googlePlacesUrl.toString();
            toPass[2] = bestMarkers;
            toPass[3] = bestpoint;
            toPass[4] = radius;
            googlePlacesReadTask.execute(toPass);
        }


    }
}