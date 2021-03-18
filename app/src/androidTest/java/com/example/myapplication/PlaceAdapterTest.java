package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.adapter.GroupAdapter;
import com.example.myapplication.adapter.PlaceAdapter;
import com.example.myapplication.data.Place;

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
public class PlaceAdapterTest {
    private Place p1,p2;
    private PlaceAdapter iAdapter;

    @Before
    public void setUp() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ArrayList<Place> data= new ArrayList<Place>();

        p1=new Place();
        p1.setName("Prova1");
        p1.setFull_address("Indirizzo1");

        p2=new Place();
        p2.setName("Prova2");
        p2.setFull_address("Indirizzo2");

        data.add(p1);
        data.add(p2);
        iAdapter= new PlaceAdapter(appContext, data);

    }

    @Test
    public void testGetItem() {
        assertEquals("Place was expected.", p2.getName(),
                ((Place) iAdapter.getItem(1)).getName());

        assertEquals("Proup was expected.", p1.getName(),
                ((Place) iAdapter.getItem(0)).getName());
    }

    @Test
    public void testGetCount() {
        assertEquals(2, iAdapter.getCount());
    }

    @Test
    public void testGetView(){
        View view = iAdapter.getView(0,null,null);
        TextView tvName = (TextView) view.findViewById(R.id.single_place_title);

        assertNotNull(tvName);

        assertEquals(p1.getName(),tvName.getText());

        view= iAdapter.getView(1,null,null);
        tvName = (TextView) view.findViewById(R.id.single_place_title);

        assertNotNull(tvName);

        assertEquals(p2.getName(),tvName.getText());
    }
}