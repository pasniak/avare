package com.ds.avare.test;

import com.ds.avare.AvareApplication;
import com.ds.avare.BuildConfig;
import com.ds.avare.StorageService;
import com.ds.avare.place.Destination;
import com.ds.avare.place.DestinationFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by pasniak on 4/24/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = AvareApplication.class)
@PowerMockIgnore({"org.mockito.", "org.robolectric."})
@PrepareForTest({StorageService.class})
public class DestinationGpsTest {
    private StorageService mStorageService;

    @Before
    public void setUp() {
        mStorageService = Robolectric.setupService(StorageService.class);
    }
    @Test
    public void Google() {
        Destination d1 = DestinationFactory.build(mStorageService, "40.4747&-74.1844", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d1);
        Destination d2 = DestinationFactory.build(mStorageService, "TEST@40.4747&-74.1844", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d2);
    }
    @Test
    public void ICAOLong() {
        Destination d1 = DestinationFactory.build(mStorageService, "402829N0741104W", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d1);
    }
    @Test
    public void ICAOLongWithSlash() {
        // format used by iFlightPlanner
        Destination d1 = DestinationFactory.build(mStorageService, "402829N/0741104W", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d1);
    }
    @Test
    public void ICAOShort() {
        Destination d1 = DestinationFactory.build(mStorageService, "4028N07411W", Destination.GPS);
        assertLatLon(40.4667, -74.1833, d1);
    }
    @Test
    public void ICAOShortWithLabel() {
        Destination d1 = DestinationFactory.build(mStorageService, "TEST@4028N07411W", Destination.GPS);
        assertLatLon(40.4667, -74.1833, d1);
    }    
    @Test
    public void ICAOWithDecimalSeconds() {
        Destination d1 = DestinationFactory.build(mStorageService, "4028290N07411040W", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d1);
        Destination d2 = DestinationFactory.build(mStorageService, "4028299N07411049W", Destination.GPS);
        assertLatLon(40.475, -74.1847, d2); // decimal seconds are truncated
    }
    private static void assertLatLon(double expectedLat, double expectedLon, Destination d) {
        double lat = d.getLocation().getLatitude();
        double lon = d.getLocation().getLongitude();
        assertEquals("Latitude test fails", expectedLat, lat);
        assertEquals("Longitude test fails", expectedLon, lon);
    }
}
