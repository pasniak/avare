/*
Copyright (c) 2016, Apps4Av Inc. (apps4av.com)

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.ds.avare.message;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.ds.avare.NextWptActivity;
import com.ds.avare.R;
import com.ds.avare.adapters.SearchAdapter;
import com.ds.avare.gps.GpsParams;
import com.ds.avare.place.Destination;
import com.ds.avare.place.Plan;

/**
 * Created by Michal on 9/2/2016.
 */
public class Notification {

    Context mContext;
    Plan mPlan;
    GpsParams mGps;

    SearchAdapter mBitmapSearchAdapterNoId;

    public Notification (Context context, Plan plan, GpsParams gps) {
        mContext = context; mPlan = plan; mGps = gps;
        String[] EMPTY_ARRAY = new String[0];
        mBitmapSearchAdapterNoId = new SearchAdapter(mContext, EMPTY_ARRAY);
    }

    /** Create a notification with the destination distance, angular position
     *  and NextWptActivity activity
     *
     * @param d destination which will be featured in the notification
     */
    public void create(Destination d) {
        int destinationHash = d.getID().hashCode();

        //prepare next action and next intent
        NextActionBuilder nextBuilder = new NextActionBuilder(destinationHash).build();

        //build the visuals of the notification
        NotificationCompat.Builder builder = buildNotificationVisuals(d);

        // add the intent and actions
        builder.setContentIntent(nextBuilder.getPendingNextWptIntent()); // notification is clicked: go Next
        builder.addAction(nextBuilder.getNextAction());                  // make "Next" action appear on phone
        builder.extend(new NotificationCompat.WearableExtender()         // make "Next" action appear on watch
                .addAction(nextBuilder.getNextAction()));

        // use a notification manager creates notifications compatible with android versions
        NotificationManagerCompat.from(mContext).notify(destinationHash, builder.build()); // send the notification
    }

    String cleanup(String name) { return name.replaceAll("@.*",""); } // remove @ character & coordinates

    /// builds the visual part of a notification
    public NotificationCompat.Builder buildNotificationVisuals(Destination d) {

        //contents of the notification; the same on the phone and Wear device
        String notificationTitle = cleanup(d.getID());
        int oClock = com.ds.avare.utils.Helper.getOClockPosition(mGps.getBearing(), d.getBearing());
        String notificationText = d.toString().trim() + " @" + Integer.toString(oClock);

        // Add 'big view' content to display the long description that may not fit the normal content text.
        // this will be displayed on a Watch
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle()
                .bigText(notificationText);

        // convert the waypoint type into the notification image
        Bitmap notificationIcon = mBitmapSearchAdapterNoId.getBitmap(d.getDbType());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.plane_green)
                .setLargeIcon(notificationIcon)       // show some icon in the notification
                .setContentTitle(notificationTitle)     // first line:  ongoing destination name
                .setContentText(notificationText)       // second line: destination heading etc.
                .setOnlyAlertOnce(true)                 // update without annoying the user
                .setStyle(bigStyle);                     // looks better on watches
        return builder;
    }

    /// builds actions part of the notification
    private class NextActionBuilder {
        private int destinationHash; // "next wpt" intents are made unique by keying them with the destination hash

        // results of the build
        private PendingIntent pendingNextWptIntent;
        private NotificationCompat.Action nextAction;

        public NextActionBuilder(int destinationHash) { this.destinationHash = destinationHash; }

        public PendingIntent getPendingNextWptIntent() { return pendingNextWptIntent; }

        public NotificationCompat.Action getNextAction() { return nextAction; }


        public NextActionBuilder build() {

            // Explicit intent to wrap; clicking "-> [next wpt]" will advance the plan
            Intent nextIntent = new Intent(mContext, NextWptActivity.class);
            // pass the id so the notification can be cancelled
            nextIntent.putExtra(NextWptActivity.NOTIFICATION_ID_EXTRA, destinationHash);
            String nextActionName = "->";

            if (mPlan.isActive()) {
                int destinationIndex = mPlan.findNextNotPassed();  // current destination
                nextIntent.putExtra(NextWptActivity.DESTINATION_PLAN_INDEX_EXTRA, destinationIndex);

                Destination nextDestination = mPlan.getDestination(destinationIndex + 1);
                nextActionName = "->" + (nextDestination != null ? cleanup(nextDestination.getID()) : "");
            }

            // Create pending intent and wrap our "Next" intent
            pendingNextWptIntent = PendingIntent.getActivity(mContext, 1, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            //see https://developer.android.com/training/wearables/notifications/creating.html
            // make notifications compatible with all android versions
            // NOTE: I was unable to figure out how to add a custom icon for the action
            nextAction = new NotificationCompat.Action.Builder(R.drawable.plane_green, nextActionName, pendingNextWptIntent).build();
            return this;
        }
    }
}
