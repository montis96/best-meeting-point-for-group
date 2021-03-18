package com.example.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.backendless.Backendless;
import com.example.myapplication.activity.Login;
import com.example.myapplication.utility.TestApplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Rule
    public ActivityTestRule<Login> activityRule = new ActivityTestRule<>(Login.class, true, false);

    @Before
    public void setUp() {
        activityRule.launchActivity(null);
    }

    @Test
    public void testLogin() throws InterruptedException {
        onView(withId(R.id.etMail)).perform(typeText("test@test.it"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText("test"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(4000);
        Assert.assertEquals("test@test.it", TestApplication.user.getProperty("email").toString());
        Thread.sleep(2000);
    }
}
