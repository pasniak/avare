package com.ds.avare.network;

/**
 * Created by pasniak on 5/24/2017.
 */


import android.os.AsyncTask;

import com.ds.avare.orientation.OrientationInterface;

public class StratuxTask extends AsyncTask<String, Void, String> {

    private StratuxWebSocket mStratuxWs;
    private Exception exception;

    public StratuxTask(String ip) {
        mStratuxWs = new StratuxWebSocket(ip);
    }
    
    public void registerListener(OrientationInterface oi) { mStratuxWs.registerListener(oi); }
    public void unregisterListener(OrientationInterface oi) { mStratuxWs.unregisterListener(oi); }

    protected String doInBackground(String... ws) {
        try {
            mStratuxWs.connectWebSocket();
            return "";
            
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

