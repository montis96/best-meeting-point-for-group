package com.example.myapplication;

import android.util.Log;
import android.view.Gravity;

import androidx.test.espresso.Espresso;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditGroupTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void loadData() throws InterruptedException {
        test.loadUserData("test@test.it", "test");
        Thread.sleep(8000);


        activityRule.launchActivity(null);
    }

    @Test
    public void testParticipating() throws InterruptedException {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.etNameGroup)).perform(typeText("TestGroup"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.spnGroupType)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Restaurant")))
                .perform(click());
        onView(withId(R.id.spnGroupType))
                .check(matches(withSpinnerText(containsString("Restaurant"))));

        onView(withId(R.id.btnNewGroup)).perform(click());

        Thread.sleep(10000);

        Assert.assertEquals(TestApplication.group.getName(), "TestGroup");
        Assert.assertEquals(TestApplication.group.getType(), "restaurant");
        Assert.assertTrue(TestApplication.group_place_user.get(0).isParticipating());

        onData(allOf())
                .inAdapterView(withId(R.id.lvList))
                .atPosition(0)
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.ivEdit))
                .perform(click());

        onView(withId(R.id.etName)).perform(typeText("TestGroupSecond"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnSubmit)).perform(click());
        Thread.sleep(5000);
        Assert.assertEquals(TestApplication.group.getName(), "TestGroupSecond");







    }


    @After
    public void deleteData() throws InterruptedException {
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();
        test.deleteGroups();

        Thread.sleep(5000);
    }
}