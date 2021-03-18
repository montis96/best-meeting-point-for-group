package com.example.myapplication;

import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.utility.DataLoaderHelperTest;
import com.example.myapplication.utility.EspressoIdlingResource;
import com.example.myapplication.utility.TestApplication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsNot.not;


@RunWith(AndroidJUnit4.class)
public class ForwardingInvitationTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class,true, false);


    @Before
    public void loadData() throws InterruptedException {
        test.loadUserData("test@test.it", "test");
        Thread.sleep(5000);
        activityRule.launchActivity(null);
    }

    @Test
    public void ForwardingInvitation() throws InterruptedException {

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.etNameGroup)).perform(typeText("TestGroup"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnNewGroup)).perform(click());

        Thread.sleep(7000);

        // check that the group has been created
        Assert.assertEquals(TestApplication.group.getName(), "TestGroup");

        onData(allOf())
                .inAdapterView(withId(R.id.lvList))
                .atPosition(0)
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(4000);

        onView(withId(R.id.ivInvite)).perform(click());

        closeSoftKeyboard();
        Thread.sleep(4000);

        onView(withId(R.id.searchView)).perform(click());

        // Type the text in the search field and submit the query
        onView(isAssignableFrom(EditText.class)).perform(typeText("cimmo"), 	closeSoftKeyboard());

        Thread.sleep(2000);

        // check that the user you are looking for is in the list
        onData(anything())
                .inAdapterView(withId(R.id.lvForwarding))
                .atPosition(0)
                .onChildView(withId(R.id.tvUsername))
                .check(matches(withText(containsString("cimmo"))));

        onView(withId(R.id.lvForwarding))
                .check(matches(hasDescendant(withText("cimmo"))));


        // I invite the user
        onView(withId(R.id.btnInvite)).perform(click());

        Thread.sleep(3000);

        // check that the searched user has been deleted from the list
        onView(withId(R.id.lvForwarding))
                .check(matches(not(hasDescendant(withText("cimmo")))));

        LoadRelationsQueryBuilder<Group> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of( Group.class );
        loadRelationsQueryBuilder.setRelationName( "myInvitation" );

        Backendless.Data.of("Users").loadRelations("FD8C5190-FB82-75B5-FF48-E5D285306F00", loadRelationsQueryBuilder,
                new AsyncCallback<List<Group>>() {
                    @Override
                    public void handleResponse(List<Group> response) {

                        Assert.assertEquals("TestGroup",response.get(0).getName());

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                    }
                });

            Thread.sleep(3000);
    }

    @After
    public void deleteGroups() throws InterruptedException {

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();
        test.deleteGroups();
        Thread.sleep(5000);
    }
}
