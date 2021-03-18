package com.example.myapplication;

import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.rt.messaging.Channel;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.data.Group;
import com.example.myapplication.data.Group_Place_User;
import com.example.myapplication.utility.TestApplication;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;


@RunWith(AndroidJUnit4.class)
public class ChatRoomTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class,true, false);


    @Before
    public void loadData() throws InterruptedException {
        Backendless.UserService.login("test@test.it", "test", new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                TestApplication.user = response;
                TestApplication.group_place_user = new ArrayList<>();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });

        Thread.sleep(5000);
        activityRule.launchActivity(null);
        //Thread.sleep(5000);
    }

    @Test
    public void testChatRoom() throws InterruptedException {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.etNameGroup)).perform(typeText("TestGroup"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnNewGroup)).perform(click());

        Thread.sleep(5000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_groups));
        Thread.sleep(3000);

        // check that the group has been created
        Assert.assertEquals(TestApplication.group.getName(), "TestGroup");

        onView(withId(R.id.lvList))
                .check(matches(isDisplayed()));


        onData(allOf())
                .inAdapterView(withId(R.id.lvList))
                .atPosition(0)
                .perform(click());


        Thread.sleep(4000);

        onView(withId(R.id.ivChat)).perform(click());

        Espresso.closeSoftKeyboard();

        Thread.sleep(7000);

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(0, R.id.text_message_body))
                .check(matches(withText(containsString("I joined"))));

        onView(withId(R.id.edittext_chatbox)).perform(click());
        onView(withId(R.id.edittext_chatbox)).perform(typeText("Hello!"));
        onView(withId(R.id.button_chatbox_send)).perform(click());
        Thread.sleep(3000);

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(1, R.id.text_message_body))
                .check(matches(withText(containsString("Hello!"))));

        Espresso.pressBack();
        Thread.sleep(1000);

        onView(withId(R.id.ivChat)).perform(click());

        Espresso.closeSoftKeyboard();

        Thread.sleep(3000);

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(0, R.id.text_message_body))
                .check(matches(withText(containsString("I joined"))));

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(1, R.id.text_message_body))
                .check(matches(withText(containsString("Hello!"))));


        final String channelName="chat "+ TestApplication.group.getName();
        Channel channel = Backendless.Messaging.subscribe(channelName);
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.setPublisherId("fabio");
        publishOptions.putHeader( "groupId", TestApplication.group.getObjectId() );

        Backendless.Messaging.publish(channelName, "Hello test!",publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        Thread.sleep(3000);

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(3, R.id.text_message_name))
                .check(matches(withText(containsString("fabio"))));

        onView(new RecyclerViewMatcher(R.id.reyclerview_message_list)
                .atPositionOnView(3, R.id.text_message_body))
                .check(matches(withText(containsString("Hello test!"))));

    }

    public class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)",
                                    new Object[] { Integer.valueOf
                                            (recyclerViewId) });
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        }
                        else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }

                }
            };
        }
    }

    @After
    public void deleteGroups() throws InterruptedException {


        Backendless.Data.of(Group.class).remove(TestApplication.group, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                Log.i("Group", response.toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
        Backendless.Data.of(Group_Place_User.class).remove(TestApplication.group_place_user.get(0), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                Log.i("Group_place_user", response.toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        Thread.sleep(3000);
    }

}
