package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.R;
import com.example.myapplication.activity.ChatRoomActivity;
import com.example.myapplication.activity.MapsActivity;
import com.example.myapplication.adapter.ParticipantAdapter;
import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.data.Place;
import com.example.myapplication.utility.TestApplication;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo extends Fragment {

    private TextView tvName;
    private TextView tvParticipants;
    private ParticipantAdapter adapter;
    private ListView lvParticipants;
    private EditText etName;
    private Button btnSubmit;
    private LinearLayout llEdit;

    /**
     * it creates a view where there are the group details, it also contains the list of the participants
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_info, container, false);

        ImageView ivInvite, ivNavigate, ivDelete, ivEdit, ivChat;

        final int[] gpuIndex = new int[1];
        tvName = view.findViewById(R.id.tvName);
        ivInvite = view.findViewById(R.id.ivInvite);
        ivNavigate = view.findViewById(R.id.ivNavigate);
        ivDelete = view.findViewById(R.id.ivDelete);
        lvParticipants = view.findViewById(R.id.lvParticipants);
        etName = view.findViewById(R.id.etName);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ivEdit = view.findViewById(R.id.ivEdit);
        tvParticipants = view.findViewById(R.id.tvParticipants);
        TextView tvType = view.findViewById(R.id.tvType);
        llEdit = view.findViewById(R.id.llEdit);
        ivChat = view.findViewById(R.id.ivChat);

        final int index = requireArguments().getInt("index");


        StringBuilder whereClause = new StringBuilder();
        whereClause.append("group_group");
        whereClause.append(".objectId='").append(TestApplication.group_place_user.get(index).getObjectId()).append("'");

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause.toString());
        Log.i("query_group_group", whereClause.toString());
        Backendless.Data.of(Group.class).find(queryBuilder, new AsyncCallback<List<Group>>() {

            @Override
            public void handleResponse(List<Group> response) {
                TestApplication.group = response.get(0);
                tvName.setText(TestApplication.group.getName());
                tvType.setText(TestApplication.kinds[java.util.Arrays.binarySearch(TestApplication.kind_codes,
                        TestApplication.group.getType())]);

                LoadRelationsQueryBuilder<Group_Place_User> loadRelationsQueryBuilder;
                loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Group_Place_User.class);
                loadRelationsQueryBuilder.setRelationName("group_group");
                loadRelationsQueryBuilder.setSortBy("ownerId");

                Backendless.Data.of(Group.class).loadRelations(TestApplication.group.getObjectId(),
                        loadRelationsQueryBuilder,
                        new AsyncCallback<List<Group_Place_User>>() {
                            @Override
                            public void handleResponse(List<Group_Place_User> response) {

                                TestApplication.group_place_user_groups = response;
                                for (int n = 0; n< response.size(); n++) {
                                    if (response.get(n).getOwnerId().equals(TestApplication.user.getObjectId()))
                                        gpuIndex[0] = n;
                                }
                                List<Boolean> participating = new ArrayList<>();
                                StringBuilder whereClause = new StringBuilder();

                                for (int i = 0; i < response.size(); i++) {
                                    whereClause.append("group_user");
                                    whereClause.append(".objectId='").append(response.get(i).getObjectId()).append("'");
                                    if (i != response.size() - 1) {
                                        whereClause.append(" or ");
                                    }
                                    participating.add(response.get(i).isParticipating());
                                    Log.i("participating", participating.get(i).toString());
                                }

                                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                queryBuilder.setWhereClause(whereClause.toString());
                                Log.i("query utenti", whereClause.toString());
                                Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                                    @Override
                                    public void handleResponse(List<BackendlessUser> response) {
                                        for (BackendlessUser user : response)
                                            Log.i("response", String.valueOf(user.getEmail()));
                                        TestApplication.users_active = new ArrayList<>();
                                        TestApplication.places_active = new ArrayList<>();
                                        TestApplication.usersAll = new ArrayList<>();
                                        TestApplication.placesAll = new ArrayList<>();
                                        adapter = new ParticipantAdapter(getContext(), response, participating);
                                        lvParticipants.setAdapter(adapter);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("error", "Error: " + fault.getMessage());
            }
        });


        ivDelete.setOnClickListener(v -> {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
            dialog.setMessage("Do you want to delete this group?");
            dialog.setPositiveButton("Yes", (dialog1, which) -> {

                LoadRelationsQueryBuilder<Group_Place_User> loadRelationsQueryBuilder1;
                loadRelationsQueryBuilder1 = LoadRelationsQueryBuilder.of(Group_Place_User.class);
                loadRelationsQueryBuilder1.setRelationName("group_group");

                Backendless.Data.of(Group.class).loadRelations(TestApplication.group.getObjectId(),
                        loadRelationsQueryBuilder1,
                        new AsyncCallback<List<Group_Place_User>>() {

                            @Override
                            public void handleResponse(List<Group_Place_User> response) {

                                for (Group_Place_User toDelete : response) {
                                    Backendless.Data.of(Group_Place_User.class).remove(toDelete,
                                            new AsyncCallback<Long>() {
                                                @Override
                                                public void handleResponse(Long response) {

                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                Backendless.Data.of(Group.class).remove(TestApplication.group, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {
                                        TestApplication.group_place_user.remove(index);

                                        Toast.makeText(getContext(), "Group deleted", Toast.LENGTH_SHORT).show();
                                        view.setVisibility(View.GONE);

                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();

                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getContext(), "Error:" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
            dialog.setNegativeButton("No", (dialog12, which) -> {

            });
            dialog.show();
        });


        ivEdit.setOnClickListener(v -> {
            if (llEdit.getVisibility() == View.GONE) {

                etName.setText(TestApplication.group.getName());
                tvParticipants.setText(R.string.editname);
                lvParticipants.setVisibility(View.GONE);
                llEdit.setVisibility(View.VISIBLE);

                btnSubmit.setOnClickListener(v1 -> {
                    TestApplication.hideSoftKeyboard(requireActivity());
                    if (etName.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Insert all the details requested!", Toast.LENGTH_SHORT).show();
                    } else {
                        TestApplication.group.setName(etName.getText().toString().trim());
                        Backendless.Persistence.save(TestApplication.group, new AsyncCallback<Group>() {
                            @Override
                            public void handleResponse(Group response) {
                                tvName.setText(TestApplication.group.getName());
                                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                lvParticipants.setVisibility(View.VISIBLE);
                                tvParticipants.setText(R.string.participants);
                                llEdit.setVisibility(View.GONE);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getContext(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });
            } else {
                TestApplication.hideSoftKeyboard(requireActivity());
                tvParticipants.setText(R.string.participants);
                lvParticipants.setVisibility(View.VISIBLE);
                llEdit.setVisibility(View.GONE);
            }
        });


        ivNavigate.setOnClickListener(v -> {
            for (BackendlessUser user : TestApplication.users_active)
                Log.i("USERS", String.valueOf(user.getEmail()));

            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("gpuIndex", gpuIndex[0]);
            startActivityForResult(intent, 1);
        });


        ivInvite.setOnClickListener(v -> {
            view.setVisibility(View.GONE);
            ForwardingInvitations dest = new ForwardingInvitations();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(getId(), dest);
            fragmentTransaction.commit();
        });

        ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatRoomActivity.class);
            intent.putExtra("name", TestApplication.user.getProperty("username").toString());
            startActivityForResult(intent, 1);
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (llEdit.getVisibility() == View.VISIBLE)
                    ivEdit.performClick();
                else {
                    view.setVisibility(View.GONE);

                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }
}
