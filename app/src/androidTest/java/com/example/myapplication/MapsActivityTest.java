package com.example.myapplication;

import android.util.Log;
import android.view.Gravity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

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
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapsActivityTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void loadData() throws InterruptedException {
        mDevice = UiDevice.getInstance(getInstrumentation());
        test.loadUserData("gianluca@app.com", "gianluca");
        Thread.sleep(5000);
        activityRule.launchActivity(null);
    }

    @Test
    public void testMaps() throws InterruptedException, UiObjectNotFoundException {

        mDevice.findObject(new UiSelector().textContains("Pizzata")).click();

        Thread.sleep(8000);

        onView(withId(R.id.ivNavigate))
                .perform(click());

        Thread.sleep(7000);

        onView(withId(R.id.cbTraffic))
                .perform(click());
        Thread.sleep(4000);
        onView(withId(R.id.cbTraffic))
                .perform(click());
        Thread.sleep(2000);

        //save the screen center position
        UiObject obj = mDevice.findObject(new UiSelector().descriptionContains("You"));
        int x = obj.getVisibleBounds().left;
        int y = obj.getVisibleBounds().top;

        onView(withId(R.id.spinnerMapType))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Hybrid")))
                .perform(click());
        onView(withId(R.id.spinnerMapType))
                .check(matches(withSpinnerText(containsString("Hybrid"))));

        onView(withId(R.id.btnBestpoint))
                .perform(click());

        Thread.sleep(3000);
        mDevice.click(x, y);

        Thread.sleep(3000);
        onView(withId(R.id.llTrip))
                .check(matches(isDisplayed()));
        onView(withId(R.id.distance))
                .check(matches(isDisplayed()));
        onView(withId(R.id.duration))
                .check(matches(isDisplayed()));

        mDevice.findObject(new UiSelector().descriptionContains("cimmo")).click();

    }

}
