/*
Copyright (c) 2016, Apps4Av Inc. (apps4av.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.ds.avare;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.ds.avare.adsb.Traffic;
import com.ds.avare.gps.GpsInterface;
import com.ds.avare.network.StratuxTask;
import com.ds.avare.orientation.OrientationInterface;
import com.ds.avare.storage.Preferences;
import com.ds.avare.utils.Helper;
import com.ds.avare.views.PfdView;

import static com.ds.avare.place.Destination.GPS;

/**
 * @author zkhan
 */
public class PfdActivity extends Activity {

    /**
     * Service that keeps state even when activity is dead
     */
    private StorageService mService;

    /**
     * App preferences
     */
    private Preferences mPref;

    private Context mContext;

    private PfdView mPfdView;

    private StratuxTask mStratuxTask;

    /**
     * GPS calls
     */
    private GpsInterface mGpsInfc = new GpsInterface() {

        @Override
        public void statusCallback(GpsStatus gpsStatus) {
        }

        @Override
        public void locationCallback(Location location) {

            if(mService == null) {
                return;
            }
            if(mService.getGpsParams() == null || mService.getExtendedGpsParams() == null) {
                return;
            }
            double bearing = 0;
            double cdi = 0;
            double vdi = 0;
            String dst = "", distance = "";

            if(mService.getVNAV() != null) {
                vdi = mService.getVNAV().getGlideSlope();
            }
            if(mService.getCDI() != null) {
                cdi = mService.getCDI().getDeviation();
                if (!mService.getCDI().isLeft()) {
                    cdi = -cdi;
                }
            }
            if(mService.getDestination() != null) {
                bearing = mService.getDestination().getBearing();
                dst = mService.getDestination().getDbType()==GPS ? "GPS" : mService.getDestination().getID();
                distance = Math.round(mService.getDestination().getDistance()*10.0) / 10.0 + mPref.distanceConversionUnit;
            }
            mPfdView.setParams(mService.getGpsParams(), mService.getExtendedGpsParams(), bearing, cdi, vdi, dst, distance);

            if(mService.getTrafficCache() != null) {
                SparseArray<Traffic> traffic = mService.getTrafficCache().getTraffic();
                mPfdView.setTraffic(traffic);
            }
            mPfdView.postInvalidate();
        }

        @Override
        public void timeoutCallback(boolean timeout) {
        }

        @Override
        public void enabledCallback(boolean enabled) {
        }

    };

    private OrientationInterface mOrientationInfc = new OrientationInterface() {

        @Override
        public void onSensorChanged(double yaw, double pitch, double roll, double acceleration) {
            if (mPref.isPfdUsingInternalAhrs()) {
                mPfdView.setAhrsSourceName("Internal");
                // if we are using external pitch/roll source it does not make sense to use internal
                if (!mPref.isPfdUsingExternalAhrs() && !mPref.isPfdUsingStratuxAhrs()) {
                    mPfdView.setPitch(-(float) pitch);
                    mPfdView.setRoll(-(float) roll);
                }
                if (mPref.isPfdUsingInternalMagneticHeading()) {
                    mPfdView.setYaw((float) yaw);
                }
                mPfdView.postInvalidate();
            }
        }
        
        @Override
        public void onAhrsReport(double yaw, double pitch, double roll, double acceleration, double slip) {
            if (mPref.isPfdUsingExternalAhrs()) {
                mPfdView.setAhrsSourceName("External");
                mPfdView.setPitch((float) pitch);
                mPfdView.setRoll(-(float) roll);
                mPfdView.setSlip((float) slip);
                if (!mPref.isPfdUsingInternalMagneticHeading()) {
                    mPfdView.setYaw((float) yaw);
                }
                //mPfdView.setAcceleration(acceleration);  // not needed as slip is calculated externally
                mPfdView.postInvalidate();
            }
        }
        
        @Override
        public void onStratuxSituationChange(double yaw, double pitch, double roll, double acceleration, double slip, double pa) {
            if (mPref.isPfdUsingStratuxAhrs()) {
                mPfdView.setAhrsSourceName("Stratux");
                if (!mPref.isPfdUsingInternalMagneticHeading()) {
                    mPfdView.setYaw((float) yaw);
                }
                mPfdView.setPitch((float) pitch);
                mPfdView.setRoll(-(float) roll);
                //mPfdView.setAcceleration((float) acceleration); // not needed as slip is calculated externally
                mPfdView.setSlip(-(float) slip);
                mPfdView.setPressureAltitude((float) pa);
                mPfdView.postInvalidate();
            }
        }
    };

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        ((MainActivity) this.getParent()).showMapTab();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Helper.setTheme(this);
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPref = new Preferences(this);

        mContext = this;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pfd, null);
        setContentView(view);

        mPfdView = (PfdView) view.findViewById(R.id.pfd_view);
        
        if (mPref.isPfdUsingStratuxAhrs()) {
            mStratuxTask = new StratuxTask(mPref.getStratuxIp());
            mStratuxTask.registerListener(mOrientationInfc);
            mStratuxTask.execute();
        }
    }


    /** Defines callbacks for service binding, passed to bindService() */
    /**
     *
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /* (non-Javadoc)
         * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
         */
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            /*
             * We've bound to LocalService, cast the IBinder and get LocalService instance
             */
            StorageService.LocalBinder binder = (StorageService.LocalBinder) service;
            mService = binder.getService();
            mService.registerGpsListener(mGpsInfc);
            mService.registerOrientationListener(mOrientationInfc);
        }

        /* (non-Javadoc)
         * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Helper.setOrientationAndOn(this);

        /*
         * Registering our receiver
         * Bind now.
         */
        Intent intent = new Intent(this, StorageService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (null != mStratuxTask) {
            mStratuxTask.registerListener(mOrientationInfc);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (null != mService) {
            mService.unregisterGpsListener(mGpsInfc);
            mService.unregisterOrientationListener(mOrientationInfc);
        }
        if (null != mStratuxTask) {
            mStratuxTask.unregisterListener(mOrientationInfc);
        }
        /*
         * Clean up on pause that was started in on resume
         */
        getApplicationContext().unbindService(mConnection);

    }

}