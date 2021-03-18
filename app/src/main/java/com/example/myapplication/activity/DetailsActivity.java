package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DetailsAdapter;
import com.example.myapplication.adapter.PlaceAdapter;
import com.example.myapplication.utility.TestApplication;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.libraries.places.api.Places.createClient;

/**
 * Activity that show all the details about a place
 */
public class DetailsActivity extends AppCompatActivity {

    ListView lvDetails;
    DetailsAdapter adapter;
    List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String placeId = getIntent().getStringExtra("id");

        System.out.println("ID: " + placeId);


        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.VIEWPORT, Place.Field.UTC_OFFSET, Place.Field.WEBSITE_URI, Place.Field.PLUS_CODE);

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        Places.initialize(getApplicationContext(), "AIzaSyByyPXoo6la_E0E5MR7kLUL6Vh_YKgrLLg");
        PlacesClient placesClient = Places.createClient(this);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            places = new ArrayList<>();
            places.add(place);

            lvDetails = findViewById(R.id.lvDetails);
            adapter = new DetailsAdapter(DetailsActivity.this, places);
            lvDetails.setAdapter(adapter);


            /*
            System.out.println("nome: " + place.getName());
            System.out.println("ore: " + place.getOpeningHours());
            OpeningHours open = place.getOpeningHours();
            System.out.println("periodo: " + open.getPeriods());
            System.out.println("weekday text: " + open.getWeekdayText().get(0));
            System.out.println("weekday text: " + open.getWeekdayText().get(1));
            System.out.println("weekday text: " + open.getWeekdayText().get(2));
            System.out.println("weekday text: " + open.getWeekdayText().get(3));
            System.out.println("weekday text: " + open.getWeekdayText().get(4));
            System.out.println("weekday text: " + open.getWeekdayText().get(5));
            System.out.println("weekday text: " + open.getWeekdayText().get(6));
            System.out.println("cel: " + place.getPlusCode());
            System.out.println("price level: " + place.getPriceLevel());
            System.out.println("rating: " + place.getRating());
            System.out.println("user rating: " + place.getUserRatingsTotal());
            System.out.println("UtcOffsetMinute: " + place.getUtcOffsetMinutes());
            System.out.println("viewport: " + place.getViewport());
            System.out.println("sito: " + place.getWebsiteUri());
            */

            //Log.i(TAG, "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                //Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });

    }
}
