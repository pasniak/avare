package com.ds.avare;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/** NextWptActivity activity handles clicking on Next button in a notification
 *  and calls back the Service with advancePlanFrom
 */
public class NextWptActivity extends Activity {

    public static final String NOTIFICATION_ID_EXTRA = "NotificationId";
    public static final String DESTINATION_PLAN_INDEX_EXTRA = "DestinationPlanIndex";

    private StorageService mService;
    Intent mNextIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cancel the previous notification
        mNextIntent = getIntent();

        Intent newIntent = new Intent(this, StorageService.class);
        bindService(newIntent, mConnection, BIND_AUTO_CREATE);

        // remove the GUI; this along with Translucent.NoTitleBar makes the Activity invisible
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConnection != null) {
            unbindService(mConnection);
        }
    }
    /** Defines callbacks for service binding, passed to bindService() */
    /**
     *
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /*
         * (non-Javadoc)
         *
         * @see
         * android.content.ServiceConnection#onServiceConnected(android.content
         * .ComponentName, android.os.IBinder)
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            /*
             * We've bound to LocalService, cast the IBinder and get
             * LocalService instance
             */
            StorageService.LocalBinder binder = (StorageService.LocalBinder) service;
            mService = binder.getService();

            mService.advancePlanFrom(mNextIntent);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.content.ServiceConnection#onServiceDisconnected(android.content
         * .ComponentName)
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


}
