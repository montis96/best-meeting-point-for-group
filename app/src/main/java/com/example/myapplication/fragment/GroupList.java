package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.adapter.GroupAdapter;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.utility.TestApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GroupList extends Fragment {

    private ListView lvList;
    private GroupAdapter adapter;

    /**
     * It creates an activity where there is the user groups list
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        lvList = view.findViewById(R.id.lvList);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            view.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        });

        lvList.setOnItemClickListener((parent, view1, position, id) -> {
            view.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putInt("index", position);
            GroupInfo dest = new GroupInfo();
            dest.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, dest);
            fragmentTransaction.commit();
        });

        fab.setOnClickListener(view12 -> {
            view.setVisibility(View.GONE);
            CreateGroup dest = new CreateGroup();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, dest);
            fragmentTransaction.commit();
        });


        class CreatedComparator implements Comparator<Group_Place_User> {
            @Override
            public int compare(Group_Place_User o1, Group_Place_User o2) {
                return o2.getCreated().compareTo(o1.getCreated());
            }
        }

        LoadRelationsQueryBuilder<Group_Place_User> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Group_Place_User.class);
        loadRelationsQueryBuilder.setRelationName("group_user");

        Backendless.Data.of(BackendlessUser.class).loadRelations(TestApplication.user.getObjectId(),
                loadRelationsQueryBuilder,
                new AsyncCallback<List<Group_Place_User>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void handleResponse(List<Group_Place_User> response) {
                        TestApplication.group_place_user = new ArrayList<>();

                        if (!response.isEmpty()) {
                            TestApplication.group_place_user.addAll(response);
                            TestApplication.group_place_user.sort(new CreatedComparator());
                        }

                        adapter = new GroupAdapter(getContext(), TestApplication.group_place_user);
                        lvList.setAdapter(adapter);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e("error", "Error: " + fault.getMessage());
                    }
                });

        return view;
    }

    /**
     * if a group is modified it changes the data of the list
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            adapter.notifyDataSetChanged();
        }
    }
}
