package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.myapplication.R;
import com.example.myapplication.data.Group;
import com.example.myapplication.utility.TestApplication;

import java.util.ArrayList;
import java.util.List;

public class ForwardingInvitationAdapter extends BaseAdapter implements Filterable {

private Context context;
private List<BackendlessUser> users;
private List<BackendlessUser> filterUsers;

    /**
     * Adapter constructor. In Android, Adapter is a bridge between UI component and data source that
     * helps us to fill data in UI component.
     * To initialize the adapter I pass him the context of the activity and the list of users
     * that I have recovered
     *
     */
    public ForwardingInvitationAdapter (Context context, List <BackendlessUser> list){
        this.context=context;
        this.users=list;
        this.filterUsers=list;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     */
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     */
    @Override
    public long getItemId(int position) {
        return users.indexOf(getItem(position));
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //specify a root view
        assert inflater != null;
        convertView = inflater.inflate(R.layout.row_layout_forwarding_invitation, parent, false);

        Button btnInvite=convertView.findViewById(R.id.btnInvite);
        TextView tvUsername= convertView.findViewById(R.id.tvUsername);

        // I write in the TextView the username of the i-th user
        tvUsername.setText(users.get(position).getProperty("username").toString());

        /*
         * when I click the button I will have to invite the selected user.
         * Then I add a relation data object in the database in the "myInvitation" column to indicate
         * that the selected user has an invitation in that group.
         */
        btnInvite.setOnClickListener(v -> {
            ArrayList<Group> l= new ArrayList<>();
            l.add(TestApplication.group);

            Backendless.Data.of(BackendlessUser.class).addRelation(users.get(position), "myInvitation", l,
                    new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse(Integer response) {
                            Log.i( "MYAPP", "sending invitation");
                            Toast.makeText(v.getContext(),"invitation sent",Toast.LENGTH_LONG).show();
                            Log.i("MYAPPSEARCH",  "  position: " + position + "  username users:"+ users.get(position).getProperty("username").toString());
                            Log.i("MYAPPSEARCH",  "  position: " + position + "  username filters:"+ filterUsers.get(position).getProperty("username").toString());

                            for (int i=0; i<filterUsers.size(); i++) {
                            if (filterUsers.get(i).getProperty("username").toString().equals(users.get(position).getProperty("username").toString())) {
                                filterUsers.remove(i);
                                if (filterUsers.size() != users.size())
                                      users.remove(position);
                                Log.i("MYAPPSEARCH", "i: "+ i + "  position: " + position);
                                break;
                            }
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                        }
                    });

        });

        return convertView;
    }

    /**
     * This method returns a filter that allows you to filter users in the listView based on the text entered in the searchView
     */
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint != null && constraint.length() > 0)
                    {
                        // We perform filtering operation
                        List<BackendlessUser> nUsList = new ArrayList<>();
                        for (BackendlessUser us : filterUsers) {
                            if (us.getProperty("username").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                nUsList.add(us);
                            }
                        }
                        results.values = nUsList;
                        results.count = nUsList.size();
                    } else {
                        results.count=filterUsers.size();
                        results.values=filterUsers;
                    }
                    return results;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                // Now we have to inform the adapter about the new list filtered
                users=(ArrayList<BackendlessUser>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
