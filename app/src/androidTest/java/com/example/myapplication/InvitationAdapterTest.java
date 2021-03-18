package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.adapter.InvitationAdapter;
import com.example.myapplication.data.Group;

import org.junit.After;
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
public class InvitationAdapterTest {
    private Group g1,g2;
    private InvitationAdapter iAdapter;

    @Before
    public void setUp() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

      //  assertEquals("com.example.myapplication", appContext.getOpPackageName());

        ArrayList<Group> data= new ArrayList<Group>();

        g1=new Group();
        g2=new Group();
        g1.setName("birra");
        g2.setName("pizzata");

        data.add(g1);
        data.add(g2);
        iAdapter= new InvitationAdapter(appContext,data);

    }

    @Test
    public void testGetItem() {
        assertEquals("Group was expected.", g2.getName(),
                ((Group) iAdapter.getItem(1)).getName());

        assertEquals("Group was expected.", g1.getName(),
                ((Group) iAdapter.getItem(0)).getName());
    }

    @Test
    public void testGetCount() {
        assertEquals(2, iAdapter.getCount());
    }

    @Test
    public void testGetView(){
        View view = iAdapter.getView(0,null,null);


        TextView tvChar = (TextView) view.findViewById(R.id.tvChar);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvCreator = (TextView) view.findViewById(R.id.tvCreator);


        assertNotNull(tvChar);
        assertNotNull(tvName);
        //assertNotNull(tvCreator);

        assertEquals(g1.getName(),tvName.getText());
        assertEquals(g1.getName().toUpperCase().charAt(0)+"",tvChar.getText());
        //assertEquals("invited by: steve",tvCreator.getText());

        view= iAdapter.getView(1,null,null);
        tvChar = (TextView) view.findViewById(R.id.tvChar);
        tvName = (TextView) view.findViewById(R.id.tvName);

        assertNotNull(tvChar);
        assertNotNull(tvName);

        assertEquals(g2.getName(),tvName.getText());
        assertEquals(g2.getName().toUpperCase().charAt(0)+"",tvChar.getText());

    }
}
