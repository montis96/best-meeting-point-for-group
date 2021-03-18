package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.adapter.GroupAdapter;
import com.example.myapplication.data.Group_Place_User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GroupAdapterTest {

    private Group_Place_User gpu1, gpu2;
    private GroupAdapter iAdapter;

    @Before
    public void setUp() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        ArrayList<Group_Place_User> gpu = new ArrayList<>();

        gpu1 = new Group_Place_User();
        gpu2 = new Group_Place_User();

        gpu1.setParticipating(true);
        gpu2.setParticipating(false);

        gpu.add(gpu1);
        gpu.add(gpu2);
        iAdapter = new GroupAdapter(appContext, gpu);
    }

    @Test
    public void testGetItem() {
        assertEquals("Group was expected.", gpu2.isParticipating(),
                (Objects.requireNonNull(iAdapter.getItem(1))).isParticipating());

        assertEquals("Group was expected.", gpu1.isParticipating(),
                (Objects.requireNonNull(iAdapter.getItem(0))).isParticipating());
    }

    @Test
    public void testGetCount() {
        assertEquals(2, iAdapter.getCount());
    }

    @Test
    public void testGetView(){
        View view = iAdapter.getView(0,null,null);
        TextView tvName =  view.findViewById(R.id.tvName);

        assertNotNull(tvName);

        view= iAdapter.getView(1,null,null);
        tvName = view.findViewById(R.id.tvName);

        assertNotNull(tvName);
    }
}