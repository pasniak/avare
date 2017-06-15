package com.ds.avare.network;

import android.util.Log;

import com.ds.avare.orientation.OrientationInterface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by pasniak on 5/23/2017.
 */

public class StratuxWebSocket {
    private String mSituationWsIp;
    private WebSocketClient mWebSocketClient;

    private OrientationInterface mListener;

    public StratuxWebSocket(String ip) {
        mSituationWsIp = ip;
    }
    
    public void registerListener(OrientationInterface listener) {mListener = listener;}    
    public void unregisterListener(OrientationInterface listener) {mListener = null;}


    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://"+mSituationWsIp+"/situation");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                //mWebSocketClient.send("status");
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                //Log.i("Websocket", "Received " + message);
                //{"GPSLastFixSinceMidnightUTC":38198.1,"GPSLatitude":0,"GPSLongitude":0,"GPSFixQuality":0,
                // "GPSHeightAboveEllipsoid":0,"GPSGeoidSep":0,"GPSSatellites":0,"GPSSatellitesTracked":3,"GPSSatellitesSeen":3,
                // "GPSHorizontalAccuracy":999999,"GPSNACp":0,"GPSAltitudeMSL":0,"GPSVerticalAccuracy":999999,"GPSVerticalSpeed":0,
                // "GPSLastFixLocalTime":"0001-01-01T00:00:00Z","GPSTrueCourse":0,"GPSTurnRate":0,
                // "GPSGroundSpeed":0,"GPSLastGroundTrackTime":"0001-01-01T00:00:00Z","GPSTime":"2017-05-24T10:36:38.1Z",
                // "GPSLastGPSTimeStratuxTime":"0001-01-02T01:12:31.45Z","GPSLastValidNMEAMessageTime":"0001-01-02T01:12:31.55Z",
                // "GPSLastValidNMEAMessage":"$PUBX,00,103638.20,1656.83450,N,01606.01379,W,1803.087,NF,10000000,10000000,0.000,0.00,0.000,,99.99,99.99,99.99,0,0,0*05",
                // "GPSPositionSampleRate":0,"BaroTemperature":39.24,"BaroPressureAltitude":185.12881,
                // "BaroVerticalSpeed":0.020066196,"BaroLastMeasurementTime":"0001-01-02T01:12:31.57Z","AHRSPitch":-5.631848,
                // "AHRSRoll":4.486928,"AHRSGyroHeading":359.92215,"AHRSMagHeading":317.7203,"AHRSSlipSkid":-4.3147,
                // "AHRSTurnRate":0,"AHRSGLoad":1.086166,"AHRSLastAttitudeTime":"0001-01-02T01:12:31.56Z","AHRSStatus":6}
                try {
                    JSONObject o = new JSONObject(message);
                    String gyroTime = o.getString("AHRSLastAttitudeTime");
                    String baroTime = o.getString("BaroLastMeasurementTime");
                    String both = gyroTime + " " + baroTime;
                    if (null != mListener) {
                        mListener.onStratuxSituationChange(
                                Double.parseDouble(o.getString("AHRSMagHeading")),
                                Double.parseDouble(o.getString("AHRSPitch")),
                                Double.parseDouble(o.getString("AHRSRoll")),
                                Double.parseDouble(o.getString("AHRSGLoad")) * 10.0,
                                Double.parseDouble(o.getString("AHRSSlipSkid")),
                                Double.parseDouble(o.getString("BaroPressureAltitude"))
                        );
                    }
                } catch (JSONException e) {}
                
            }
            
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
    public void disconnect() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }
}
