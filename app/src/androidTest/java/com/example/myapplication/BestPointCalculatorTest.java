package com.example.myapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.utility.DataLoaderHelperTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BestPointCalculatorTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void loadData() throws InterruptedException {
        mDevice = UiDevice.getInstance(getInstrumentation());
        test.loadUserData("roberto@app.com", "roberto");
        Thread.sleep(5000);
        mActivityRule.launchActivity(null);
    }

    @Test
    public void testBestPoint() throws InterruptedException, UiObjectNotFoundException {

        mDevice.findObject(new UiSelector().textContains("Pizzata")).click();

        Thread.sleep(6000);

        onView(withId(R.id.ivNavigate))
                .perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.btnBestpoint))
                .perform(click());


    }
}
