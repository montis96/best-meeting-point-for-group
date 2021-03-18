package com.example.myapplication;

import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;


import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.utility.DataLoaderHelperTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogoutTest {
    private DataLoaderHelperTest test = new DataLoaderHelperTest();

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void loadData() throws InterruptedException {
        test.loadUserData("test@test.it", "test");
        Thread.sleep(5000);

        activityRule.launchActivity(null);
    }


    @Test
    public void testLogout() throws InterruptedException {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.ivLogout)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.btnLogin))
                .check(matches(withText("Login")));
        onView(withId(R.id.btnRegister))
                .check(matches(withText("Register")));
    }

}
