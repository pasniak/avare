package com.ds.avare.test;

import android.content.Context;
import android.location.Location;
import android.view.MotionEvent;

import com.ds.avare.AvareApplication;
import com.ds.avare.BuildConfig;
import com.ds.avare.LocationActivity;
import com.ds.avare.R;
import com.ds.avare.gps.GpsParams;
import com.ds.avare.touch.LongTouchDestination;
import com.ds.avare.views.LocationView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Michal on 5/5/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = AvareApplication.class)
@PowerMockIgnore({"org.mockito.", "org.robolectric."})
public class LocationViewTest extends InterfaceTest {

    private LocationView mLocationView;

    @Test
    public void airportPressed() throws Exception {
        // set location
        Location current = new Location("N51 location");
        current.setLatitude(40.582744444);
        current.setLongitude(-74.736716667);
        GpsParams params = new GpsParams(current);
        mLocationView.initParams(params, mStorageService);

        // long press
        MotionEvent motionEvent = getLongPressEvent(0,0);
        mLocationView.dispatchTouchEvent(motionEvent);

        // assert destination pressed
        LongTouchDestination d =  mLocationView.getLongTouchDestination();
        assertEquals("Airport not found", "N51", d.airport);
        assertEquals("Info not found", "0nm(S  of) 013°", d.info);
        assertNotNull("SUA not found", d.sua);
        HtmlAsserts.assertRowCount(d.navaids, 4);
    }

    public void setupInterface(Context ctx) {
        final LocationActivity locationActivity =  Robolectric.buildActivity(LocationActivity.class).create().get();
        mLocationView = (LocationView) locationActivity.findViewById(R.id.location);

    }
}
