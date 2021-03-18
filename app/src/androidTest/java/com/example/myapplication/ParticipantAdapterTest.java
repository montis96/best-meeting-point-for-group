package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.backendless.BackendlessUser;
import com.example.myapplication.adapter.ParticipantAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ParticipantAdapterTest {
    private BackendlessUser u1, u2;
    private ParticipantAdapter iAdapter;

    @Before
    public void setUp() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ArrayList<BackendlessUser> data = new ArrayList<>();
        ArrayList<Boolean> b = new ArrayList<>();


        u1 = new BackendlessUser();
        u1.setEmail("prova1@mail.it");
        u1.setPassword("password");
        u1.setProperty("name", "name1");
        u1.setProperty("username", "username1");
        u1.setProperty("surname", "surname2");
        u1.setProperty("objectId", "ID");

        u2 = new BackendlessUser();
        u2.setEmail("prova1@mail.it");
        u2.setPassword("password");
        u2.setProperty("name", "name1");
        u2.setProperty("username", "username2");
        u2.setProperty("surname", "surname2");
        u2.setProperty("objectId", "ID");

        data.add(u1);
        data.add(u2);

        b.add(true);
        b.add(true);

        iAdapter = new ParticipantAdapter(appContext, data, b);
    }

    @Test
    public void testGetItem() {
        assertEquals("User was expected.", u2.getEmail(),
                (iAdapter.getItem(1)).getEmail());
    }

    @Test
    public void testGetCount() {
        assertEquals(2, iAdapter.getCount());
    }

    @Test
    public void testGetView() {
        View view = iAdapter.getView(0, null, null);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvSurname = view.findViewById(R.id.tvSurname);

        assertNotNull(tvUsername);
        assertNotNull(tvName);
        assertNotNull(tvEmail);
        assertNotNull(tvSurname);

        assertEquals(u1.getProperty("username"), tvUsername.getText());
        assertEquals(u1.getProperty("name"), tvName.getText());
        assertEquals(u1.getEmail(), tvEmail.getText());
        assertEquals(u1.getProperty("surname"), tvSurname.getText());


         view = iAdapter.getView(1, null, null);
          tvUsername = view.findViewById(R.id.tvUsername);
          tvName = view.findViewById(R.id.tvName);
          tvEmail = view.findViewById(R.id.tvEmail);
          tvSurname = view.findViewById(R.id.tvSurname);

        assertNotNull(tvUsername);
        assertNotNull(tvName);
        assertNotNull(tvEmail);
        assertNotNull(tvSurname);

        assertEquals(u2.getProperty("username"), tvUsername.getText());
        assertEquals(u2.getProperty("name"), tvName.getText());
        assertEquals(u2.getEmail(), tvEmail.getText());
        assertEquals(u2.getProperty("surname"), tvSurname.getText());
    }
}