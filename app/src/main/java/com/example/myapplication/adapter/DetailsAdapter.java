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
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * adapter for the details of a place
 */
public class DetailsAdapter extends ArrayAdapter<Place> {

    private Context context;
    private List<Place> places;

    /**
     * Constructor that receives a Context object and a place
     */
    public DetailsAdapter(Context context, List<Place> list) {
        super(context, R.layout.row_details, list);
        this.context = context;
        this.places = list;

    }

    /**
     * It set the details of the place
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_details, parent, false);

        TextView title = convertView.findViewById(R.id.single_details_title);
        TextView rating = convertView.findViewById(R.id.rating);
        TextView price = convertView.findViewById(R.id.prezzo);
        TextView orario = convertView.findViewById(R.id.orario);
        TextView cel = convertView.findViewById(R.id.cel);
        TextView url = convertView.findViewById(R.id.url);

        TextView ratingData = convertView.findViewById(R.id.ratingData);
        TextView priceData = convertView.findViewById(R.id.prezzoData);
        TextView lunedi = convertView.findViewById(R.id.lunedi);
        TextView martedi = convertView.findViewById(R.id.martedi);
        TextView mercoledi = convertView.findViewById(R.id.mercoledi);
        TextView giovedi = convertView.findViewById(R.id.giovedi);
        TextView venerdi = convertView.findViewById(R.id.venerdi);
        TextView sabato = convertView.findViewById(R.id.sabato);
        TextView domenica = convertView.findViewById(R.id.domenica);
        TextView urlData = convertView.findViewById(R.id.urlData);
        TextView celData = convertView.findViewById(R.id.numCel);


        title.setText(places.get(position).getName());

        rating.setText("Rating:");
        if (places.get(position).getRating() == null)
            ratingData.setText("rating not available");
        else
            ratingData.setText(places.get(position).getRating().toString());

        price.setText("Level price::");
        if (places.get(position).getPriceLevel() == null)
            priceData.setText("level price not available");
        else if (places.get(position).getPriceLevel() == 0)
            priceData.setText("free");
        else if (places.get(position).getPriceLevel() == 1)
            priceData.setText("$");
        else if (places.get(position).getPriceLevel() == 2)
            priceData.setText("$$");
        else if (places.get(position).getPriceLevel() == 3)
            priceData.setText("$$$");
        else
            priceData.setText("$$$$");

        orario.setText("Opening Hours:");
        if (places.get(position).getOpeningHours() == null) {
            lunedi.setText("Monday: not available");
            martedi.setText("Tuesday: not available");
            mercoledi.setText("Wednesday: not available");
            giovedi.setText("Thursday: not available");
            venerdi.setText("Friday: not available");
            sabato.setText("Saturday: not available");
            domenica.setText("Sunday: not available");
        } else {
            lunedi.setText(places.get(position).getOpeningHours().getWeekdayText().get(0));
            martedi.setText(places.get(position).getOpeningHours().getWeekdayText().get(1));
            mercoledi.setText(places.get(position).getOpeningHours().getWeekdayText().get(2));
            giovedi.setText(places.get(position).getOpeningHours().getWeekdayText().get(3));
            venerdi.setText(places.get(position).getOpeningHours().getWeekdayText().get(4));
            sabato.setText(places.get(position).getOpeningHours().getWeekdayText().get(5));
            domenica.setText(places.get(position).getOpeningHours().getWeekdayText().get(6));
        }


        cel.setText("Phone number:");
        if (places.get(position).getPhoneNumber() == null)
            celData.setText("phone number not available");
        else
            celData.setText(places.get(position).getPhoneNumber());


        url.setText("Website:");
        if (places.get(position).getWebsiteUri() == null)
            urlData.setText("website not available");
        else
            urlData.setText(places.get(position).getWebsiteUri().toString());


        return convertView;
    }
}
