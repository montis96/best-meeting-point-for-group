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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.utility.TestApplication;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group_Place_User>
{
    private Context context;
    private List<Group_Place_User> gpu;

    /**
     * Constructor that receive a context object and a list of groups
     */
    public GroupAdapter(Context context, List<Group_Place_User> list) {
        super(context, R.layout.row_groups, list);
        this.context = context;
        this.gpu = list;
    }

    /**
     * it set the details of every group inside the list
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.row_groups, parent, false);

        TextView tvName = convertView.findViewById(R.id.tvName);
        ImageView ivParticipant = convertView.findViewById(R.id.ivParticipant);

        Drawable participant = ivParticipant.getResources().getDrawable(R.drawable.participant);
        Drawable participant_no = ivParticipant.getResources().getDrawable(R.drawable.participant_no);


        StringBuilder whereClause = new StringBuilder();
        whereClause.append("group_group");
        whereClause.append(".objectId='").append(gpu.get(position).getObjectId()).append("'");

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause.toString());
        Log.i("query_group_group", whereClause.toString());
        Backendless.Data.of(Group.class).find(queryBuilder, new AsyncCallback<List<Group>>() {
            @Override
            public void handleResponse(List<Group> response) {
                if (!response.isEmpty()) {
                    tvName.setText(response.get(0).getName());

                    if (gpu.get(position).isParticipating())
                        ivParticipant.setImageDrawable(participant);
                    else
                        ivParticipant.setImageDrawable(participant_no);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("error", "Error: " + fault.getMessage());
            }
        });

        ivParticipant.setOnClickListener(v -> {

            // ivParticipant changed firstly to speed up the visual change for the user
            if (ivParticipant.getDrawable().equals(participant))
                ivParticipant.setImageDrawable(participant_no);
            else
                ivParticipant.setImageDrawable(participant);

            Backendless.Data.of(Group_Place_User.class).findById(gpu.get(position).getObjectId(),
                    new AsyncCallback<Group_Place_User>() {
                        @Override
                        public void handleResponse(Group_Place_User response) {

                            // must pay attention that the if-clause is reverted since the ivParticipant
                            // is already setted few lines before here
                            if (ivParticipant.getDrawable().equals(participant)) {
                                TestApplication.group_place_user.get(position).setParticipating(true);
                                response.setParticipating(true);
                            } else {
                                TestApplication.group_place_user.get(position).setParticipating(false);
                                response.setParticipating(false);
                            }

                            Backendless.Data.of(Group_Place_User.class).save(response,
                                    new AsyncCallback<Group_Place_User>() {
                                        @Override
                                        public void handleResponse(Group_Place_User response) {

                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {

                                        }
                                    });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }

                    });

        });


        return convertView;
    }
}
