package com.example.myapplication.fragment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.data.Group;
import com.example.myapplication.utility.TestApplication;
import com.example.myapplication.adapter.ForwardingInvitationAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForwardingInvitations extends Fragment {

    private ListView listView;
    private ForwardingInvitationAdapter adapter;

    /**
     * This method is performed when the ForwardingInvitations activity is created and allows you to load
     * the list of users from the database with the possibility to invite them.
     * The users returned are those not belonging to the group or not yet invited. it is possible to filter them
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forwarding_invitations, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.lvForwarding);

        StringBuilder where = new StringBuilder();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();

        for (int i = 0; i < TestApplication.users_active.size(); i++){
            where.append("objectId != '")
                    .append(TestApplication.users_active.get(i).getObjectId())
                    .append("'");
            if (i < TestApplication.users_active.size() - 1)
                where.append(" and ");
        }
        queryBuilder.setWhereClause(where.toString());

        // I prepare the query and set the name of the relationship (foreign key)
        LoadRelationsQueryBuilder<Group> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of( Group.class );
        loadRelationsQueryBuilder.setRelationName( "myInvitation" );
        List<BackendlessUser> listuser= new ArrayList<>();

        // I search for users (excluding those already belonging to the group), after which I filter
        // them further excluding users who have already been invited
        Backendless.Persistence.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> response) {

                for ( BackendlessUser us : response) {
                    Backendless.Data.of("Users").loadRelations(us.getObjectId(), loadRelationsQueryBuilder,
                            new AsyncCallback<List<Group>>() {
                                @Override
                                public void handleResponse(List<Group> response) {

                                    boolean flag = false;
                                    for (int i = 0; i < response.size(); i++) {
                                        if (response.get(i).getName().equals(TestApplication.group.getName()))
                                            flag = true;
                                    }
                                    if (!flag)
                                        listuser.add(us);

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                                }
                            });
                }

                //I set the adapter to use in the ListView
                adapter = new ForwardingInvitationAdapter(getContext(), listuser);
                listView.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });

        // When the query in the search bar changes, I filter the users contained in the listView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty())
                    adapter.getFilter().filter(newText);
                else
                    adapter.getFilter().filter("######");

                return false;
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                view.setVisibility(View.GONE);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }
}
