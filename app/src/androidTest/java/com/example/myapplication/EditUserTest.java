package com.example.myapplication;

import android.view.Gravity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.utility.DataLoaderHelperTest;
import com.example.myapplication.utility.TestApplication;

import static android.view.KeyEvent.KEYCODE_B;
import static android.view.KeyEvent.KEYCODE_C;
import static android.view.KeyEvent.KEYCODE_I;
import static android.view.KeyEvent.KEYCODE_L;
import static android.view.KeyEvent.KEYCODE_M;
import static android.view.KeyEvent.KEYCODE_O;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditUserTest {

    private DataLoaderHelperTest test = new DataLoaderHelperTest();
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws InterruptedException {
        mDevice = UiDevice.getInstance(getInstrumentation());
        test.loadUserData("test@test.it", "test");
        Thread.sleep(5000);
        activityRule.launchActivity(null);
    }

    @Test
    public void testEdit() throws InterruptedException, UiObjectNotFoundException {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.ivSettings))
                .perform(click());

        onView(withId(R.id.etName))
                .check(matches(withText(TestApplication.user.getProperty("name").toString())))
                .perform(replaceText("testname"));
        onView(withId(R.id.etSurname))
                .check(matches(withText(TestApplication.user.getProperty("surname").toString())))
                .perform(replaceText("testsurname"));
        onView(withId(R.id.etMail))
                .check(matches(withText(TestApplication.user.getEmail())))
                .perform(replaceText("testmail@test.it"));
        onView(withId(R.id.etUsername))
                .check(matches(withText(TestApplication.user.getProperty("username").toString())))
                .perform(replaceText("testusername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etAddress))
                .perform(click());
        onView(withId(R.id.autocomplete_fragment))
                .perform(click());
        mDevice.pressKeyCode(KEYCODE_B);
        mDevice.pressKeyCode(KEYCODE_I);
        mDevice.pressKeyCode(KEYCODE_C);
        mDevice.pressKeyCode(KEYCODE_O);
        mDevice.findObject(new UiSelector().textContains("Bicocca")).click();
        Thread.sleep(1000);
        onView(withId(R.id.etNewPassword))
                .perform(typeText("testpsw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("testpsw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnEdit))
                .perform(click());

        Thread.sleep(8000);
    }

    @After
    public void backToOriginal() throws InterruptedException, UiObjectNotFoundException {

        onView(withId(R.id.etMail))
                .perform(typeText("testmail@test.it"));
        onView(withId(R.id.etPassword))
                .perform(typeText("testpsw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin))
                .perform(click());
        Thread.sleep(5000);

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.ivSettings))
                .perform(ViewActions.click());

        onView(withId(R.id.etName))
                .check(matches(withText(TestApplication.user.getProperty("name").toString())))
                .perform(replaceText("test"));
        onView(withId(R.id.etSurname))
                .check(matches(withText(TestApplication.user.getProperty("surname").toString())))
                .perform(replaceText("test"));
        onView(withId(R.id.etMail))
                .check(matches(withText(TestApplication.user.getEmail())))
                .perform(replaceText("test@test.it"));
        onView(withId(R.id.etUsername))
                .check(matches(withText(TestApplication.user.getProperty("username").toString())))
                .perform(replaceText("test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etAddress))
                .perform(click());
        onView(withId(R.id.autocomplete_fragment))
                .perform(click());
        mDevice.pressKeyCode(KEYCODE_M);
        mDevice.pressKeyCode(KEYCODE_I);
        mDevice.pressKeyCode(KEYCODE_L);
        mDevice.findObject(new UiSelector().textContains("Milano")).click();
        Thread.sleep(1000);
        onView(withId(R.id.etNewPassword))
                .perform(typeText("test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etConfirmPassword))
                .perform(typeText("test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnEdit))
                .perform(click());

        Thread.sleep(5000);

    }
}
