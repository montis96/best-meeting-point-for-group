package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.data.Group;
import com.example.myapplication.parser.DirectionsJSONParser;
import com.example.myapplication.utility.GooglePlacesReadTask;
import com.example.myapplication.R;
import com.example.myapplication.utility.TestApplication;
import com.example.myapplication.data.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

/**
 * Allows the user to see other departure points and one or more
 * best meeting points. Furthermore they can have a detailed look of their routes.
 */
public class MapsActivity extends FragmentActivity implements OnMyLocationButtonClickListener,
        OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap; // A generic Google's map

    /**
     * Inner class used for storing trip information.
     */
    static class Trip {
        private int duration;
        private int distance;

        Trip() {
            this.duration = 0;
            this.distance = 0;
        }

        int getDuration() {
            return duration;
        }

        void setDuration(int duration) {
            this.duration = duration;
        }

        int getDistance() {
            return distance;
        }

        void setDistance(int distance) {
            this.distance = distance;
        }

    }

    private ArrayList<Trip> trips; // A list of trip
    private ArrayList<Marker> departureMarkers; // A list of departure markers of all the people
    private ArrayList<MarkerOptions> departureMarkersBMP; // A list of departure markers of the people partecipating in BMP
    private ArrayList<Marker> bestMarkers; // A list of best meeting point markers
    private ArrayList<Polyline> polylines; // A list of polylines (used to draw routes)
    private HashMap<Marker, Polyline> markersPolylines; // A map between a marker and its polyline
    private HashMap<Marker, Trip> markersTrips; // A map between a marker and its trip information

    private Spinner spinnerMapType; // A spinner used to select the map style
    private LinearLayout llTrip; // A linear layout used to show trip's information
    private TextView tvDistance; // A textview used to show the distance of a trip
    private TextView tvDuration; // A textview used to show the duration of a trip
    private Button btnBestPoint; // A button used to calculate the best point
    private Button btnVote; // A button used to vote among the best points
    private static boolean first_click = false; //Flag to know if we need to save the relation

    public static boolean ultimo_passaggio = false;


    /**
     * It handles the creation of the activity initializating the needed objects
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        trips = new ArrayList<>();
        departureMarkers = new ArrayList<>();
        departureMarkersBMP = new ArrayList<>();
        bestMarkers = new ArrayList<>();
        polylines = new ArrayList<>();
        markersPolylines = new HashMap<>();
        markersTrips = new HashMap<>();
        TestApplication.best_places = new ArrayList<>();
        btnVote = findViewById(R.id.btnVote);
        btnVote.setBackgroundResource(R.drawable.buttons_disabled);

        final int gpuIndex = getIntent().getIntExtra("gpuIndex", -1);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        llTrip = findViewById(R.id.llTrip);
        llTrip.setVisibility(View.GONE);

        // textviews for viewing data related to a route
        tvDistance = findViewById(R.id.distance);
        tvDuration = findViewById(R.id.duration);

        spinnerMapType = findViewById(R.id.spinnerMapType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMapType.setAdapter(adapter);
        spinnerMapType.setOnItemSelectedListener(this);

        /*
         * Calculate best meeting point
         */
        btnBestPoint = findViewById(R.id.btnBestpoint);
//        btnBestPoint.setEnabled(false);


        // Only if every user already accepted the invitation load the best places
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("myInvitation.objectId='" + TestApplication.group.getObjectId() + "'");
        Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> response) {
                if (response.isEmpty()) {
                    LoadRelationsQueryBuilder<Place> loadRelationsQueryBuilder;
                    loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Place.class);
                    loadRelationsQueryBuilder.setRelationName("places");
                    Backendless.Data.of(Group.class).loadRelations(TestApplication.group.getObjectId(),
                            loadRelationsQueryBuilder,
                            new AsyncCallback<List<Place>>() {
                                @Override
                                public void handleResponse(List<Place> response) {
                                    TestApplication.best_places = response;
                                    if (response.isEmpty()) {
                                        btnBestPoint.setEnabled(true);
                                        btnBestPoint.setBackgroundResource(R.drawable.buttons_orange);
                                        btnVote.setEnabled(false);
                                        btnVote.setBackgroundResource(R.drawable.buttons_disabled);
                                        first_click = true;
                                    } else {
                                        if (TestApplication.check_best_place()) {
                                            btnBestPoint.setEnabled(false);
                                            btnBestPoint.setBackgroundResource(R.drawable.buttons_disabled);
                                            btnVote.setEnabled(false);
                                            btnVote.setBackgroundResource(R.drawable.buttons_disabled);
                                            calculateBestMeetingPoint();
                                            ultimo_passaggio = true;
                                        } else {
                                            if (!TestApplication.group_place_user_groups.get(gpuIndex).getVoted()){
                                                btnVote.setEnabled(true);
                                                btnVote.setBackgroundResource(R.drawable.buttons_orange);
                                            }
                                            btnBestPoint.setEnabled(false);
                                            btnBestPoint.setBackgroundResource(R.drawable.buttons_disabled);
                                            calculateBestMeetingPoint();
                                        }
                                    }
                                    Log.i("place_count", "Invitations number:" + response.size());
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e("error_places", fault.getMessage());
                                }
                            });


                } else {
                    Toast.makeText(MapsActivity.this, "There are still invitations unresolved", Toast.LENGTH_LONG).show();
                }
                Log.i("inv_count", "Invitations number:" + response.size());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("Error_invitation", fault.getMessage());
            }
        });


        btnBestPoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btnBestPoint.isEnabled()) {
                    calculateBestMeetingPoint();
                    btnBestPoint.setEnabled(false);
                    btnBestPoint.setBackgroundResource(R.drawable.buttons_disabled);
                    btnVote.setEnabled(true);
                    btnVote.setBackgroundResource(R.drawable.buttons_orange);
                } else
                    Toast.makeText(MapsActivity.this, "Disabled", Toast.LENGTH_LONG).show();
            }
        });


        /*
         * Open the Vote Activity
         */
        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (first_click) {
                    Backendless.Data.of(Group.class).addRelation(TestApplication.group, "places", TestApplication.best_places, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            first_click = false;
                            Log.i("relation_group_places", "DONE");
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i("relation_group_places", fault.getMessage());
                        }
                    });
                }

                Intent intent = new Intent(MapsActivity.this, VoteActivity.class).putExtra("gpuIndex", gpuIndex);
                startActivityForResult(intent, 2);


            }
        });
    }

    /**
     * It handles the map itself on all of its features
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        updateMapType();

        createDepartureMarkers();

        /*
         * enabling localization and localization button
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        /*
         * disabling toolbar (it is used to continue using this app without having to
         * use the official google maps app)
         */
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnPolylineClickListener(this);
    }

    /**
     * Takes the addresses of the people in the group and transforms them into a LatLng object
     * using the getLocationFromAddress() function.
     * Create departure markers adding the username as infoWindow
     * Marker's caption:
     * 0.4f --> other users
     * 0.5f --> other best points
     * 0.99f --> logged user
     * 1f --> current best point
     */
    public void createDepartureMarkers() {

        int index = 0;

        List<String> usersAll = new ArrayList<>();
        List<String> placesAll = new ArrayList<>();

        List<String> users_active = new ArrayList<>();
        List<String> places_active = new ArrayList<>();

        for (BackendlessUser user : TestApplication.usersAll)
            usersAll.add(user.getProperty("username").toString());

        for (Place place : TestApplication.placesAll)
            placesAll.add(place.getFull_address());

        for (BackendlessUser user : TestApplication.users_active)
            users_active.add(user.getProperty("username").toString());

        for (Place place : TestApplication.places_active)
            places_active.add(place.getFull_address());

        /*
         * For every user add a marker setting its title to the username and alpha to 0.4f
         */
        int i = 0;
        for (String user : usersAll) {
            if (!user.equals(TestApplication.user.getProperty("username").toString())) {
                departureMarkers.add(mMap.addMarker(new MarkerOptions()
                        .position(getLocationFromAddress(placesAll.get(i)))
                        .title(user)
                        .alpha(0.4f)));
            } else {
                index = i;
            }
            i++;
        }

        /*
         * For every user partecipating in BMP add a marker setting its title to the username and alpha to 0.4f
         */
        i = 0;
        for (String user : users_active) {
            if (!user.equals(TestApplication.user.getProperty("username").toString())) {
                departureMarkersBMP.add(new MarkerOptions()
                        .position(getLocationFromAddress(places_active.get(i)))
                        .title(user)
                        .alpha(0.4f));
            } else {
                index = i;
            }
            i++;
        }

        /*
         * marker "You" added later in order to better visualize polylines on the map
         */
        for (String user : usersAll) {
            if (user.equals(TestApplication.user.getProperty("username").toString())) {
                departureMarkers.add(mMap.addMarker(new MarkerOptions()
                        .position(getLocationFromAddress(placesAll.get(index)))
                        .title("You")
                        .alpha(0.99f)));
            }
        }

        /*
         * marker "You" added later in order to better visualize polylines on the map
         */
        for (String user : users_active) {
            if (user.equals(TestApplication.user.getProperty("username").toString())) {
                departureMarkersBMP.add(new MarkerOptions()
                        .position(getLocationFromAddress(places_active.get(index)))
                        .title("You")
                        .alpha(0.99f));
            }
        }


        departureMarkers.get(departureMarkers.size() - 1).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                departureMarkers.get(departureMarkers.size() - 1).getPosition()));
        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(
                        departureMarkers.get(departureMarkers.size() - 1).getPosition(), 13);
        mMap.animateCamera(cameraUpdate);
    }

    /**
     * Calculate the best meeting point and retrieve close points to it.
     */
    public void calculateBestMeetingPoint() {
        try {
            searchBestPoints(bestPointCalculator());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * once it has all the coordinates in Latlng format of each participant in the group it
     * calculates the latitude and longitude at the center (best point).
     */
    private LatLng bestPointCalculator() {

        double latitude = 0.0;
        double longitude = 0.0;

        for (int i = 0; i < departureMarkersBMP.size(); i++) {
            latitude = latitude + departureMarkersBMP.get(i).getPosition().latitude;
            longitude = longitude + departureMarkersBMP.get(i).getPosition().longitude;
        }

        latitude = latitude / departureMarkersBMP.size();
        longitude = longitude / departureMarkersBMP.size();


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13);
        mMap.animateCamera(cameraUpdate);

        return new LatLng(latitude, longitude);
    }

    /**
     * Search close points to best meeting point and create markers of them.
     * the function makes a url request to look for "restaurant" type places
     * close to the coordinated one within 3km.
     * after which, through the GooglePlacesReadTask class, it displays the Places found on the map,
     * displaying the name of the restaurant and the street.
     */
    private void searchBestPoints(LatLng best) throws IOException, JSONException {

        // creo la custom string per la richiesta url dei places
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + best.latitude + "," + best.longitude);
        googlePlacesUrl.append("&radius=" + 500);
        googlePlacesUrl.append("&types=" + TestApplication.group.getType());
        googlePlacesUrl.append("&key=" + getString(R.string.google_maps_key));

        // eseguo la classe GooglePlacesReadTask per visualizzare i place sulla mappa
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[5];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        toPass[2] = bestMarkers;
        toPass[3] = best;
        toPass[4] = 500;
        googlePlacesReadTask.execute(toPass);
    }

    /**
     * For every departure it asks Google Maps for getting the routes to best
     */
    public void drawDirections(ArrayList<Marker> departures, Marker best) {

        llTrip.setVisibility(View.VISIBLE);
        for (Marker departure : departures) {
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(departure.getPosition(), best.getPosition());

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    /**
     * Show an info text on click of my location button
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Localizzazione..", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Change layout of routes and markers on click of a marker and show the info
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getAlpha() == 0.5f) { // if is other best points
            for (Marker mark : bestMarkers) {
                mark.setAlpha(0.5f);
            }
            marker.setAlpha(1f);
            for (Polyline poly : polylines)
                poly.remove();
            polylines.clear();
            markersPolylines.clear();
            trips.clear();
            drawDirections(departureMarkers, marker);
        } else if (marker.getAlpha() == 0.4f || marker.getAlpha() == 0.99f) { // if is user departure
            if (!btnBestPoint.isClickable()) {
                changePolyline(markersPolylines.get(marker));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    setTextView(Objects.requireNonNull(markersTrips.get(marker)));
                }
            }
        }
        marker.showInfoWindow();
        return true;
    }

    /**
     * Show the StreetView of the position
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Bundle b_out = new Bundle();
        b_out.putDouble("lat", marker.getPosition().latitude);
        b_out.putDouble("lng", marker.getPosition().longitude);
        Intent intent = new Intent(this, StreetViewActivity.class);
        intent.putExtras(b_out);

        try {
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Change layout of routes and markers on click of a polyline and show the info
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        changePolyline(polyline);
        Marker m = getKeyByValue(markersPolylines, polyline);
        if (m != null) {
            m.showInfoWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTextView(Objects.requireNonNull(markersTrips.get(m)));
            }
        }
    }

    /**
     * method that takes as input the string "strAddress" which represents a person's address and
     * returns a LatLng object containing the latitude and longitude of that address
     */
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    /**
     * Get the directions from origin to dest
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Mode type
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";

        String key = "key=" + getString(R.string.google_maps_key);

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + key;
    }

    /**
     * Perform an URL call and store the data obtained
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Downloading url", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    /**
     * It allows the URL connection to be asynchronous and invokes a thread to parse JSON data
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        ParserTask parserTask = null;

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

    }

    /**
     * It allows the parsing to be asynchronous and parse JSON data. It sets all the needed objects
     * in order to draw polylines subsequently
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions lineOptions = null;
            Trip trip = new Trip();
            int duration = 0, distance = 0;

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "Direction not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                ArrayList<LatLng> points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    try {
                        double lat = 0;
                        double lng = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                            lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                        }
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);

                    } catch (NullPointerException ignored) {
                    }

                    try {
                        duration += Integer.parseInt(path.get(j).get("duration"));
                        distance += Integer.parseInt(path.get(j).get("distance"));
                    } catch (NumberFormatException ignored) {
                    }
                }

                // Adding all the points in the route to LineOptions
                trip.setDuration(duration);
                trip.setDistance(distance);
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.geodesic(true);
                lineOptions.clickable(true);
            }

            setTextView(trip);
            trips.add(trip);

            // Drawing polyline in the Google Map for the i-th route
            if (polylines.size() == departureMarkers.size() - 1) // if you
                polylines.add(mMap.addPolyline(lineOptions.color(Color.BLUE).width(13)));
            else
                polylines.add(mMap.addPolyline(lineOptions.color(Color.GRAY)));

            if (!polylines.isEmpty())
                markersPolylines.put(departureMarkers.get(polylines.size() - 1), polylines.get(polylines.size() - 1));
            if (!trips.isEmpty())
                markersTrips.put(departureMarkers.get(trips.size() - 1), trips.get(trips.size() - 1));
        }

    }

    /**
     * Change layout of polylines
     */
    public void changePolyline(Polyline polyline) {
        for (Polyline poly : polylines)
            if (poly.getColor() == Color.RED) {
                poly.setColor(Color.GRAY);
                poly.setWidth(8);
            }
        if (polyline.getColor() != Color.BLUE) {
            polyline.setColor(Color.RED);
            polyline.setWidth(11);
        }
    }

    /**
     * Returns the key of a specific value in a map object
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(value, entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Set the trip info
     */
    public void setTextView(Trip trip) {
        int duration = trip.getDuration();
        int distance = trip.getDistance();

        int day = duration / (24 * 60 * 60);
        int hour = (duration - (day * 24 * 60 * 60)) / (60 * 60);
        int min = (duration - (day * 24 * 60 * 60 + hour * 60 * 60)) / 60;
        double dist = (double) distance / 1000;

        if (day > 0) {
            tvDuration.setText(Integer.toString(day) + "d "
                    + Integer.toString(hour) + "hr ");
        } else if (hour > 0)
            tvDuration.setText(Integer.toString(hour) + "hr "
                    + Integer.toString(min) + "min ");
        else if (min > 0)
            tvDuration.setText(Integer.toString(min) + "min ");
        else
            tvDuration.setText("1min ");

        if (distance >= 1000) { //>1km
            if (dist >= 100) //>100km
                tvDistance.setText("(" + Integer.toString((int) dist) + " km)");
            else
                tvDistance.setText("(" + new DecimalFormat("#.#").format(dist) + " km)");
        } else
            tvDistance.setText("(" + Integer.toString(distance) + " m)");
    }

    /**
     * Set the traffic layout on click
     */
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked)
            mMap.setTrafficEnabled(true);
        else
            mMap.setTrafficEnabled(false);
    }

    /**
     * Set the map style in base of the item selected of its spinner
     */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateMapType();
    }

    /**
     * Update the map style
     */
    private void updateMapType() {
        // No toast because this can also be called by the Android framework in onResume() at which
        // point mMap may not be ready yet.
        if (mMap == null) {
            return;
        }

        String layerName = ((String) spinnerMapType.getSelectedItem());
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);
        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static boolean getFirst_click() {
        return first_click;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }

}
