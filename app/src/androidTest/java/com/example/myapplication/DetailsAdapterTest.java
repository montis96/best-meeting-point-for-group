package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.activity.DetailsActivity;
import com.example.myapplication.adapter.DetailsAdapter;
import com.example.myapplication.adapter.GroupAdapter;
import com.example.myapplication.adapter.PlaceAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DetailsAdapterTest {
    private Place place;
    private DetailsAdapter iAdapter;
    private ArrayList<Place> data;
    private String placeId;
    private List<Place.Field> placeFields;
    private FetchPlaceRequest request;
    private PlacesClient placesClient;
    private Context appContext;

    @Before
    public void setUp() throws InterruptedException {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        data= new ArrayList<Place>();
        placeId = "ChIJfweAg8C-hkcRmpVyiMOsEhg";
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.VIEWPORT, Place.Field.UTC_OFFSET, Place.Field.WEBSITE_URI, Place.Field.PLUS_CODE);
        request = FetchPlaceRequest.newInstance(placeId, placeFields);

        Places.initialize(appContext.getApplicationContext(), "AIzaSyByyPXoo6la_E0E5MR7kLUL6Vh_YKgrLLg");
        placesClient = Places.createClient(appContext);
    }

    @Test
    public void testGetItem() {

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();
            data.add(place);
            iAdapter= new DetailsAdapter(appContext, data);

            assertEquals("name was expected.", place.getName(),
                    (iAdapter.getItem(0)).getName());

            assertEquals("rating was expected.", place.getRating(),
                    (iAdapter.getItem(0)).getRating());

            if (place.getPriceLevel() == null && iAdapter.getItem(0).getPriceLevel() == null)
                assertTrue(true);
            else
                assertTrue(false);

            assertEquals("hour was expected.", place.getOpeningHours().getWeekdayText(),
                    (iAdapter.getItem(0)).getOpeningHours().getWeekdayText());

            assertEquals("phone number was expected.", place.getPhoneNumber(),
                    (iAdapter.getItem(0)).getPhoneNumber());

            if (place.getWebsiteUri() == null && iAdapter.getItem(0).getWebsiteUri() == null)
                assertTrue(true);
            else
                assertTrue(false);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
            }
        });
    }


    @Test
    public void testGetView(){

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();
            data.add(place);
            iAdapter= new DetailsAdapter(appContext, data);

            View view = iAdapter.getView(0,null,null);
            TextView namePlace = view.findViewById(R.id.single_details_title);

            assertNotNull(namePlace);
            assertEquals(place.getName(),namePlace.getText());


            TextView ratingData = view.findViewById(R.id.ratingData);
            assertNotNull(ratingData);
            assertEquals(place.getRating().toString(),ratingData.getText());

            TextView numcell = view.findViewById(R.id.numCel);
            assertNotNull(numcell);
            assertEquals(place.getPhoneNumber(),numcell.getText());

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
            }
        });
    }
}