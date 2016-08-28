package com.ds.avare;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;


public class NextWptActivity extends Activity {

    private StorageService mService;
    int mNotificationId;
    int mDestinationPlanIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cancel the previous notification
        Intent originalIntent = getIntent();
        mNotificationId = originalIntent.getIntExtra("NotificationId", 0);
        mDestinationPlanIndex = originalIntent.getIntExtra("DestinationPlanIndex", 0);

        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(mNotificationId);

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

            mService.advancePlanFrom(mDestinationPlanIndex);
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
