package com.example.myapplication.activity;

import com.example.myapplication.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

/**
 * Allows the user to see StreetView of a location points
 */
public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private LatLng latLng;

    /**
     * It handles the creation of the activity initializating the needed objects
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Bundle b;
        b = getIntent().getExtras();
        if (b != null) {
            latLng = new LatLng(b.getDouble("lat"), b.getDouble("lng"));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewactivity);

        if (streetViewPanoramaFragment != null) {
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        }
    }

    /**
     * It handles the streetview itself on all of its features
     */
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        streetViewPanorama.setPosition(latLng);
        streetViewPanorama.setStreetNamesEnabled(false);
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaLocation -> {
            if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
                Toast.makeText(StreetViewActivity.this,
                        "StreetView non presente per questo luogo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}