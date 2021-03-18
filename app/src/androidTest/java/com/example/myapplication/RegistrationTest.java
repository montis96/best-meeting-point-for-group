package com.example.myapplication;

import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.myapplication.activity.Login;
import com.example.myapplication.data.Place;
import com.example.myapplication.utility.TestApplication;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.view.KeyEvent.KEYCODE_B;
import static android.view.KeyEvent.KEYCODE_C;
import static android.view.KeyEvent.KEYCODE_I;
import static android.view.KeyEvent.KEYCODE_O;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
public class RegistrationTest {
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<Login> activityRule = new ActivityTestRule<>(Login.class);

    @Test
    public void testRegistration() throws InterruptedException, UiObjectNotFoundException {
        mDevice = UiDevice.getInstance(getInstrumentation());
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.etName)).perform(typeText("TestName"));
        onView(withId(R.id.etSurname)).perform(typeText("TestSurname"));
        onView(withId(R.id.etMail)).perform(typeText("testmail@testmail.it"));
        onView(withId(R.id.etUsername)).perform(typeText("TestUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etAddress)).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.autocomplete_fragment)).perform(click());


        mDevice.pressKeyCode(KEYCODE_B);
        mDevice.pressKeyCode(KEYCODE_I);
        mDevice.pressKeyCode(KEYCODE_C);
        mDevice.pressKeyCode(KEYCODE_O);
        mDevice.findObject(new UiSelector().textContains("Bicocca")).click();
        Thread.sleep(1000);

        onView(withId(R.id.etPassword)).perform(typeText("psw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etConfirmPassword)).perform(typeText("psw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnRegister)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.etMail)).perform(typeText("testmail@testmail.it"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText("psw"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        Thread.sleep(3000);
        Assert.assertEquals("testmail@testmail.it", TestApplication.user.getProperty("email").toString());


    }

    @After
    public void deleteUser () throws InterruptedException {
        String where= "ownerId='"+TestApplication.user.getObjectId()+"'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(where);
        Backendless.Data.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
            @Override
            public void handleResponse(List<Place> response) {
                Backendless.Data.of(Place.class).remove(response.get(0), new AsyncCallback<Long>() {
                    @Override
                    public void handleResponse(Long response) {

                        Backendless.Data.of(BackendlessUser.class).remove(TestApplication.user, new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        });


        Thread.sleep(5000);
    }
}
