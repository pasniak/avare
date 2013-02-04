/*
Copyright (c) 2012, Zubair Khan (governer@gmail.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.ds.avare;

/**
 * @author zkhan
 * A class that deals with projected distances and bearings between two points
 * Uses rhumb line
 */
public class Projection {

    /**
     *
     */
    private double mBearing;
    /**
     * 
     */
    private double mDistance;

    private double mLon1;
    private double mLon2;
    private double mLat1;
    private double mLat2;       

    /**
     * @param lon1 Longitude of point 1
     * @param lat1 Latitude of point 1
     * @param lon2 Longitude of point 2
     * @param lat2 Latitude of point 2
     *  Great circle
     */
    public Projection(double lon1, double lat1, double lon2, double lat2) {
        
        /*
         * Save these for track
         */
        mLon1 = Math.toRadians(lon2);
        mLon2 = Math.toRadians(lon1);
        mLat1 = Math.toRadians(lat2);
        mLat2 = Math.toRadians(lat1);       
        
        //http://www.movable-type.co.uk/scripts/latlong.html
        lat1 = mLat2;
        lat2 = mLat1;
        lon2 = mLon1;
        lon1 = mLon2;
        double dLon = lon2 - lon1;
                   
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
       
        double dLat = lat2 - lat1;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        mDistance = Preferences.earthRadiusConversion * c;
        mBearing = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /**
     * Given distance and lon/lat pair, find points on great circle.
     * @return
     */
    public Coordinate[] findPoints(int num) {
        
        Coordinate mCoords[];
        mCoords = new Coordinate[num];
        double d = mDistance / Preferences.earthRadiusConversion;
        double step = num / ((double)num - 1);

        for(int i = 0; i < num; i++) {

            //  http://williams.best.vwh.net/avform.htm
            double f = ((double)i) * step / ((double)(num));
            double A = Math.sin((1 - f) * d) / Math.sin(d);
            double B = Math.sin(f * d) / Math.sin(d);
            double x = A * Math.cos(mLat1) * Math.cos(mLon1) + B * Math.cos(mLat2) * Math.cos(mLon2);
            double y = A * Math.cos(mLat1) * Math.sin(mLon1) + B * Math.cos(mLat2) * Math.sin(mLon2);
            double z = A * Math.sin(mLat1) + B * Math.sin(mLat2);
            double ilat = Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y)));
            double ilon = Math.toDegrees(Math.atan2(y , x));

            mCoords[i] = new Coordinate(ilon, ilat);
        }

        return(mCoords);
    }

    /**
     * @return
     */
    public double getBearing() {
        return(mBearing);
    }

    /**
     * @return
     */
    public double getDistance() {
        return(mDistance);
    }

    /**
     * @return
     */
    public String getGeneralDirectionFrom() {
        
        String dir;
        final double DIR_DEGREES = 15;
        
        /*
         * These are important to show for traffic controller communications.
         */
        if(mBearing > (DIR_DEGREES) && mBearing <= (90 - DIR_DEGREES)) {
            dir = "SW of";
        }
        else if(mBearing > (90 - DIR_DEGREES) && mBearing <= (90 + DIR_DEGREES)) {
            dir = "W  of";
        }
        else if(mBearing > (90 + DIR_DEGREES) && mBearing <= (180 - DIR_DEGREES)) {
            dir = "NW of";
        }
        else if(mBearing > (180 - DIR_DEGREES) && mBearing <= (180 + DIR_DEGREES)) {
            dir = "N  of";
        }
        else if(mBearing > (180 + DIR_DEGREES) && mBearing <= (270 - DIR_DEGREES)) {
            dir = "NE of";
        }
        else if(mBearing > (270 - DIR_DEGREES) && mBearing <= (270 + DIR_DEGREES)) {
            dir = "E  of";
        }
        else if(mBearing > (270 + DIR_DEGREES) && mBearing <= (360 - DIR_DEGREES)) {
            dir = "SE of";
        }
        else {
            dir = "S  of";
        }

        return(dir);
    }    
}
