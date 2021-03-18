package com.example.myapplication.utility;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.data.Place;

import java.util.List;

/**
 * Helper used to load data during the tests
 */
public class DataLoaderHelperTest {

    /**
     * Load the TestUser and instantiates groups and group_place_users as empty lists
     */
    public void loadUserData(String mail, String psw) {

        Backendless.UserService.login(mail, psw, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                TestApplication.user = response;
                TestApplication.place = new Place();
                String where= "ownerId='"+TestApplication.user.getObjectId()+"'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(where);

                Backendless.Data.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
                    @Override
                    public void handleResponse(List<Place> response) {
                        Log.i("posto", response.get(0).getFull_address());
                        TestApplication.place = response.get(0);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e( "login", "server reported an error - " + fault.getMessage() );
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });
    }

    /**
     * Delete the data (group and group_place_user) created during the tests
     */
    public void deleteGroups() {

        LoadRelationsQueryBuilder<Group_Place_User> loadRelationsQueryBuilder1;
        loadRelationsQueryBuilder1 = LoadRelationsQueryBuilder.of(Group_Place_User.class);
        loadRelationsQueryBuilder1.setRelationName("group_group");

        Backendless.Data.of(Group.class).loadRelations(TestApplication.group.getObjectId(),
                loadRelationsQueryBuilder1,
                new AsyncCallback<List<Group_Place_User>>() {
                    @Override
                    public void handleResponse(List<Group_Place_User> response) {
                        for (final Group_Place_User toDelete : response) {
                            Backendless.Data.of(Group_Place_User.class).remove(toDelete, new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long response) {

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });
                        }
                        Backendless.Data.of(Group.class).remove(TestApplication.group, new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {

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
    }
}