package com.example.myapplication;

import android.view.Gravity;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.backendless.Backendless;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.utility.DataLoaderHelperTest;
import com.example.myapplication.utility.EspressoIdlingResource;
import com.example.myapplication.utility.TestApplication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class AcceptingInvitationsTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class,true, false);

    @Before
    public void sendInvitation () throws InterruptedException {

        Backendless.UserService.logout();
        test.loadUserData("fabio@app.com", "fabio");
        Thread.sleep(5000);
        activityRule.launchActivity(null);
        Thread.sleep(5000);
    }


    @Test
    public void acceptingInvitation () throws InterruptedException {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.etNameGroup)).perform(typeText("TestGroup"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnNewGroup)).perform(click());

        Thread.sleep(10000);

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

        onView(isAssignableFrom(EditText.class)).perform(typeText("test"), 	closeSoftKeyboard());

        onView(withId(R.id.btnInvite)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());

        // Start the screen of your activity.
        onView(withId(R.id.ivLogout))
                .perform(click());

        Thread.sleep(2500);

        //verifico che ci sia l'invito
        onView(withId(R.id.etMail)).perform(typeText("test@test.it"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText("test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_invitations));

        Thread.sleep(2000);

        //check the invitation
        onView(withId(R.id.lvListInvitation))
                .check(matches(hasDescendant(withText("TestGroup"))));

        onView(withId(R.id.btnConfirm)).perform(click());

        Thread.sleep(2500);

        onView(withId(R.id.lvListInvitation))
                .check(matches(not(hasDescendant(withText("TestGroup")))));

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_groups));
        Thread.sleep(3000);

        onView(withId(R.id.lvList))
                .check(matches(hasDescendant(withText("TestGroup"))));

        Thread.sleep(2000);


    }

    @After
    public void deleteGroups() throws InterruptedException {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();

        test.deleteGroups();
        Thread.sleep(5000);
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

}
