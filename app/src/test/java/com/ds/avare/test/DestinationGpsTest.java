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
 * Created by Michal on 4/24/2017.
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
    public void testGPSDestGoogle() {
        Destination d1 = DestinationFactory.build(mStorageService, "40.4747&-74.1844", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d1);
        Destination d2 = DestinationFactory.build(mStorageService, "TEST@40.4747&-74.1844", Destination.GPS);
        assertLatLon(40.4747, -74.1844, d2);
    }
    private static void assertLatLon(double expectedLat, double expectedLon, Destination d) {
        double lat = d.getLocation().getLatitude();
        double lon = d.getLocation().getLongitude();
        assertEquals(expectedLat, lat);
        assertEquals(expectedLon, lon);
    }
}
