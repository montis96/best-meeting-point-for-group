package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.data.Place;
import com.example.myapplication.utility.TestApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * It handles the visualization of the users in a group
 */
public class ParticipantAdapter extends ArrayAdapter<BackendlessUser> {

    private Context context;
    private List<BackendlessUser> users;
    private List<Boolean> participating;

    /**
     * Constructor that receives a Context object, a list of users and a list of boolean which indicate
     * who want to be taken into account in the group
     */
    public ParticipantAdapter(Context context, List<BackendlessUser> list, List<Boolean> participating) {
        super(context, R.layout.row_groups, list);
        this.context = context;
        this.users = list;
        this.participating = participating;
    }

    /**
     * It set the details of every user inside the list
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.row_participant, parent, false);

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvSurname = convertView.findViewById(R.id.tvSurname);
        ImageView ivParticipant = convertView.findViewById(R.id.ivParticipantUser);

        tvUsername.setText(users.get(position).getProperty("username").toString());
        tvEmail.setText(users.get(position).getEmail());
        tvName.setText(users.get(position).getProperty("name").toString());
        tvSurname.setText(users.get(position).getProperty("surname").toString());

        Drawable participant = ivParticipant.getResources().getDrawable(R.drawable.participant);
        Drawable participant_no = ivParticipant.getResources().getDrawable(R.drawable.participant_no);

        // useful only for testing
        if (TestApplication.usersAll == null)
            TestApplication.usersAll = new ArrayList<>();
        if (TestApplication.placesAll == null)
            TestApplication.placesAll = new ArrayList<>();
        if (TestApplication.users_active == null)
            TestApplication.users_active = new ArrayList<>();
        if (TestApplication.places_active == null)
            TestApplication.places_active = new ArrayList<>();


        LoadRelationsQueryBuilder<Place> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Place.class);
        loadRelationsQueryBuilder.setRelationName("user_place");

        Log.i("position", String.valueOf(position));
        Backendless.Data.of(BackendlessUser.class).loadRelations(users.get(position).getObjectId(),
                loadRelationsQueryBuilder,
                new AsyncCallback<List<Place>>() {
                    @Override
                    public void handleResponse(List<Place> response) {

                        if (participating.get(position)) {
                            ivParticipant.setImageDrawable(participant);
                            TestApplication.users_active.add(users.get(position));
                            TestApplication.places_active.add(response.get(0));
                        }
                        else
                            ivParticipant.setImageDrawable(participant_no);


                        TestApplication.usersAll.add(users.get(position));
                        TestApplication.placesAll.add(response.get(0));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return convertView;
    }
}
