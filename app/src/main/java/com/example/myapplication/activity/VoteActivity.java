package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.adapter.PlaceAdapter;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.data.Place;
import com.example.myapplication.utility.TestApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Activity that allow the user to vote the best place
 */
public class VoteActivity extends AppCompatActivity {

    ListView lvList;
    PlaceAdapter adapter;

    public static int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);


        final int gpuIndex = getIntent().getIntExtra("gpuIndex", -1);
        FloatingActionButton confirm = findViewById(R.id.confirmVote);
        lvList = findViewById(R.id.lvPlaces);
        adapter = new PlaceAdapter(VoteActivity.this, TestApplication.best_places);
        lvList.setAdapter(adapter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place place = TestApplication.best_places.get(selectedPosition);
                place.setVotes(place.getVotes() + 1);
                place.saveAsync(new AsyncCallback<Place>() {
                    @Override
                    public void handleResponse(Place response) {
                        Log.i("vote", place.getName() + " added one vote");
                        TestApplication.group_place_user_groups.get(gpuIndex).setVoted(true);
                        TestApplication.group_place_user_groups.get(gpuIndex).saveAsync(new AsyncCallback<Group_Place_User>() {
                            @Override
                            public void handleResponse(Group_Place_User response) {
                                Log.i("group_place_user", "Voted successfully");
                                setResult(RESULT_OK, null);
                                VoteActivity.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e("error_vote", fault.getMessage());
                    }
                });
            }
        });


    }
}
