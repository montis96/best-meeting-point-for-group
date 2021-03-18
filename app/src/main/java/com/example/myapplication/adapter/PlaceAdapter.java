package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.activity.DetailsActivity;
import com.example.myapplication.activity.VoteActivity;
import com.example.myapplication.data.Place;

import java.util.List;

/**
 * adapter of a list of place
 */
public class PlaceAdapter extends ArrayAdapter<Place> {

    private Context context;
    private List<Place> places;

    /**
     * Constructor that receives a Context object and a list of places
     */
    public PlaceAdapter(Context context, List<Place> list) {
        super(context, R.layout.row_places, list);
        this.context = context;
        this.places = list;

    }

    /**
     * It set the details of every place inside the list
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_places, parent, false);

        TextView title = convertView.findViewById(R.id.single_place_title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailsActivity.class).putExtra("id", places.get(position).getId_google_place());
                context.startActivity(i);
            }
        });
        TextView address = convertView.findViewById(R.id.single_place_address);
        title.setText(places.get(position).getName());
        address.setText(places.get(position).getFull_address());
        RadioButton r = (RadioButton)convertView.findViewById(R.id.radioButton);
        r.setChecked(position == VoteActivity.selectedPosition);
        r.setTag(position);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteActivity.selectedPosition = (Integer)view.getTag();
                notifyDataSetChanged();
            }
        });



        return convertView;
    }
}
