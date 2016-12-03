/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.ds.avare.utils;

import android.util.Pair;

import com.ds.avare.weather.Metar;

import java.util.LinkedList;
import java.util.Locale;


public class WeatherHelper {
        
    /**
     * 
     * @param type
     * @return
     */
    public static int metarColor(String type) {
        if(type.equals("VFR")) {
        	//green
            return(0xff78e825);
        }
        else if(type.equals("IFR")) {
        	// red
            return(0xffff2a00);                        
        }
        else if(type.equals("MVFR")) {
        	// blue
            return(0xff4884ff);                        
        }
        else if(type.equals("LIFR")) {
        	// magenta
            return(0xffff54f9);                       
        }
        return(0xffffffff);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public static String metarColorString(String type) {
        if(type.equals("VFR")) {
            return("#78e825");
        }
        else if(type.equals("IFR")) {
            return("#ff2a00");                        
        }
        else if(type.equals("MVFR")) {
            return("#008aff");                        
        }
        else if(type.equals("LIFR")) {
            return("#ff54f9");                       
        }
        return("white");
    }
    
    /**
     * 
     * @param weather
     * @return
     */
    public static String formatWeather(String weather) {
        weather = weather.replace("TAF ", "");
        weather = weather.replace("AMD ", "");
        weather = weather.replace("\n\n", "\n");
        weather = weather.replace(" FM", "\nFM");
        weather = weather.replace("TEMPO", "\nTEMPO");
        weather = weather.replace("BECMG", "\nBECMG");
        return weather;
    }
    
    /**
     * 
     * @param weather
     * @return
     */
    public static String formatWeatherHTML(String weather, boolean translate) {
        weather = weather.replace("TAF ", "");
        weather = weather.replace("AMD ", "");
        weather = weather.replace("\n", "<br>");
        if(translate) {
            weather = weather.replace(" FM", "</br>FM(From)<br>");
            weather = weather.replace("TEMPO", "</br>TEMPO(Temporarily)<br>");
            weather = weather.replace("BECMG", "</br>BECMG(Becoming)<br>");
        }
        else {
            weather = weather.replace(" FM", "</br>FM");
            weather = weather.replace("TEMPO", "</br>TEMPO");
            weather = weather.replace("BECMG", "</br>BECMG");
        }
        return weather;
    }

    /**
     * Color code weather type 
     * @param weatherAll
     * @param translate
     * @return
     */
    public static String formatTafHTML(String weatherAll, boolean translate) {

        String strip[] = weatherAll.split("RMK");
        String weather = strip[0];

        /*
         * Qualifiers
         */
        weather = weather.replaceAll("\\+", "<font color='#ff54f9'+>" + (translate ? "(Heavy)" : "") + "<font color='white'>");
        weather = weather.replaceAll("\\-", "<font color='#ff2a00'>-" + (translate ? "(Light)" : "") + "<font color='white'>");
        
        /*
         * Description
         */
        weather = weather.replaceAll("MI", "<font color='#008aff'>MI" + (translate ? "(Shallow)" : "") + "<font color='white'>");
        weather = weather.replaceAll("BC", "<font color='#008aff'>BC" + (translate ? "(Patches)" : "") + "<font color='white'>");
        weather = weather.replaceAll("DR", "<font color='#008aff'>DR" + (translate ? "(Low Drifting)" : "") + "<font color='white'>");
        weather = weather.replaceAll("BL", "<font color='#008aff'>BL" + (translate ? "(Blowing)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SH", "<font color='#008aff'>SH" + (translate ? "(Showers)" : "") + "<font color='white'>");
        weather = weather.replaceAll("TS", "<font color='#ff2a00'>TS" + (translate ? "(Thunderstorm)" : "") + "<font color='white'>");
        weather = weather.replaceAll("FZ", "<font color='#ff2a00'>FZ" + (translate ? "(Freezing)" : "") + "<font color='white'>");
        weather = weather.replaceAll("PR", "<font color='#008aff'>PR" + (translate ? "(Partial)" : "") + "<font color='white'>");
        weather = weather.replaceAll("AMD", "AMD" + (translate ? "(Amended)" : ""));
        weather = weather.replaceAll("WSCONDS", "WSCONDS" + (translate ? "(Wind Shear Possible)" : ""));
        
        /*
         * Precip
         */
        weather = weather.replaceAll("DZ", "<font color='#008aff'>DZ" + (translate ? "(Drizzle)" : "") + "<font color='white'>");
        weather = weather.replaceAll("RA", "<font color='#ff2a00'>RA" + (translate ? "(Rain)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SN", "<font color='#ff2a00'>SN" + (translate ? "(Snow)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SG", "<font color='#ff2a00'>SG" + (translate ? "(Snow Grains)" : "") + "<font color='white'>");
        weather = weather.replaceAll("IC", "<font color='#ff2a00'>IC" + (translate ? "(Ice Crystals)" : "") + "<font color='white'>");
        weather = weather.replaceAll("PL", "<font color='#ff2a00'>PL" + (translate ? "(Ice Pellets)" : "") + "<font color='white'>");
        weather = weather.replaceAll("GR", "<font color='#ff2a00'>GR" + (translate ? "(Hail)" : "") + "<font color='white'>");
        weather = weather.replaceAll("GS", "<font color='#008aff'>GS" + (translate ? "(Small Hail)" : "") + "<font color='white'>");
        weather = weather.replaceAll("UP", "<font color='#ff2a00'>UP" + (translate ? "(Unknown Precip.)" : "") + "<font color='white'>");

        /*
         * Obstruction
         */
        weather = weather.replaceAll("BR", "<font color='#ff2a00'>BR" + (translate ? "(Mist)" : "") + "<font color='white'>");
        weather = weather.replaceAll("FG", "<font color='#ff2a00'>FG" + (translate ? "(Fog)" : "") + "<font color='white'>");
        weather = weather.replaceAll("FU", "<font color='#ff2a00'>FU" + (translate ? "(Smoke)" : "") + "<font color='white'>");
        weather = weather.replaceAll("DU", "<font color='#ff2a00'>DU" + (translate ? "(Dust)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SA", "<font color='#ff2a00'>SA" + (translate ? "(Sand)" : "") + "<font color='white'>");
        weather = weather.replaceAll("HZ", "<font color='#ff2a00'>HZ" + (translate ? "(Haze)" : "") + "<font color='white'>");
        weather = weather.replaceAll("PY", "<font color='#ff2a00'>PY" + (translate ? "(Spray)" : "") + "<font color='white'>");
        weather = weather.replaceAll("VA", "<font color='#ff2a00'>VA" + (translate ? "(Volcanic Ash)" : "") + "<font color='white'>");

        /*
         * Other
         */
        weather = weather.replaceAll("P0", "<font color='#ff2a00'>P0" + (translate ? "(Dust Whirls)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SQ", "<font color='#ff2a00'>SQ" + (translate ? "(Squalls)" : "") + "<font color='white'>");
        weather = weather.replaceAll("FC", "<font color='#ff2a00'>FC" + (translate ? "(Funnel Cloud)" : "") + "<font color='white'>");
        weather = weather.replaceAll("SS", "<font color='#ff2a00'>SS" + (translate ? "(Sand Storm)" : "") + "<font color='white'>");
        weather = weather.replaceAll("DS", "<font color='#ff2a00'>DS" + (translate ? "(Dust Storm)" : "") + "<font color='white'>");
        weather = weather.replaceAll(" VC", "<font color='white'> VC" + (translate ? "(In Vicinity)" : "") + "<font color='white'>");

        weather = weather.replaceAll("SKC", "SKC" + (translate ? "(Sky Clear)" : ""));
        weather = weather.replaceAll("CLR", "CLR" + (translate ? "(Clear <12,000ft)" : "")); // see http://www.weather.gov/media/grb/misc/TAF_Card.pdf
        weather = weather.replaceAll("BKN", "BKN" + (translate ? "(Broken)" : ""));
        weather = weather.replaceAll("SCT", "SCT" + (translate ? "(Scattered)" : ""));
        weather = weather.replaceAll("OVC", "OVC" + (translate ? "(Overcast)" : ""));
        weather = weather.replaceAll("PROB", "PROB" + (translate ? "(Probability%)" : ""));
        weather = weather.replaceAll("VV", "<font color='#ff2a00'>VV" + (translate ? "(Vertical Visibility)" : "") + "<font color='white'>");
        weather = weather.replaceAll("CB", "<font color='#ff2a00'>CB" + (translate ? "(Cumulonimbus)" : "") + "<font color='white'>");
        weather = weather.replaceAll("WS", "<font color='#ff54f9'>WS" + (translate ? "(Wind Shear)" : "") + "<font color='white'>");

        weather = weather.replaceAll(" 9999 ", " 9999" + (translate ? "(Visibility > 7SM) " : ""));
        weather = weather.replaceAll("QNH", "QNH" + (translate ? "(Minimum Altimeter)" : ""));
        weather = weather.replaceAll("INS", "INS" + (translate ? "(Inches)" : ""));

        for(int i = 1 ; i < strip.length; i++) {

            String weather1 = strip[i];

            weather += " RMK" + (translate ? "(Remark) " : " ") + weather1;
        }

        return weather;
    }

    public enum DistantMetarFormat { NoStationId, WithStationId }

    /**
     * @param metar
     * @param format
     * @param airport
     * @return the metar decorated with the metar's position off airport name, if that metar is
     *         from a different field; for example for 3GV we'd print:
     *          METAR (8nm SW of 3GV)
     */
    public static String formatDistantMetarHeader(Metar metar, DistantMetarFormat format, String airport) {
        String stationId = format == DistantMetarFormat.WithStationId ?
                "using " + metar.stationId + " "
                : "";
        return metar.distance > 0 ?
                String.format(Locale.getDefault(),
                        "<font color=\"red\">(%s%s %s)</font> ",
                        stationId, metar.position, airport)
                : "";
    }

    /**
     * Split string on the second space
     * @param s
     * @return pair of strings split
     */
    private static Pair splitOnSecondSpace(String s) {
        int i = s.indexOf(' ', 1 + s.indexOf(' '));
        String firstPart = s.substring(0, i);
        String secondPart = s.substring(i+1);
        return Pair.create(firstPart, secondPart);
    }

    /**
     * Color code winds
     * @param weatherAll
     * @param translate
     * @return
     */
    public static String formatMetarHTML(String weatherAll, boolean translate) {
        
        String strip[] = weatherAll.split("RMK");

        // a bit of a simplification but typically first 2 items are identifier and time; they need no translation
        Pair<String,String> p = splitOnSecondSpace(strip[0]);
        String identAndTime = p.first;
        String weather = p.second;

        /*
         * Identifier
         */
        identAndTime = identAndTime.replaceAll("SPECI", "SPECI" + (translate ? "(Special/unscheduled)" : ""));


        /*
         * Remarks
         */
        weather = weather.replaceAll("\\+", "+" + (translate ? "(Heavy)" : ""));
        weather = weather.replaceAll("\\-", "-" + (translate ? "(Light)" : ""));
        weather = weather.replaceAll("IR", "IR" + (translate ? "(Runway Ice)" : ""));
        weather = weather.replaceAll("WR", "WR" + (translate ? "(Wet Runway)" : ""));
        weather = weather.replaceAll("LSR", "LSR" + (translate ? "(Loose Runway Snow)" : ""));
        weather = weather.replaceAll("PSR", "PSR" + (translate ? "(Packed Runway Snow)" : ""));
        weather = weather.replaceAll("LTG", "LTG" + (translate ? "(Lightning)" : ""));
        weather = weather.replaceAll("TCU","TCU" + (translate ? "(Towering Cumulus)" : ""));
        weather = weather.replaceAll("VRB", "VRB" + (translate ? "(Variable)" : ""));
        
        weather = weather.replaceAll("AUTO", "AUTO" + (translate ? "(Automated)" : ""));
        weather = weather.replaceAll("COR", "COR" + (translate ? "(Corrected)" : ""));
        weather = weather.replaceAll(" 9999 ", " 9999" + (translate ? "(Visibility > 7SM) " : ""));

        /*
         * Description
         */
        weather = weather.replaceAll("MI", "MI" + (translate ? "(Shallow)" : ""));
        weather = weather.replaceAll("BC", "BC" + (translate ? "(Patches)" : ""));
        weather = weather.replaceAll("DR", "DR" + (translate ? "(Low Drifting)" : ""));
        weather = weather.replaceAll("BL", "BL" + (translate ? "(Blowing)" : ""));
        weather = weather.replaceAll("SH", "SH" + (translate ? "(Showers)" : ""));
        weather = weather.replaceAll("TS", "TS" + (translate ? "(Thunderstorm)" : ""));
        weather = weather.replaceAll("FZ", "FZ" + (translate ? "(Freezing)" : ""));
        weather = weather.replaceAll("PR", "PR" + (translate ? "(Partial)" : ""));
        /*
         * Precip
         */
        weather = weather.replaceAll("DZ", "DZ" + (translate ? "(Drizzle)" : ""));
        weather = weather.replaceAll("RA ", "RA " + (translate ? "(Rain)" : ""));
        weather = weather.replaceAll("SN", "SN" + (translate ? "(Snow)" : ""));
        weather = weather.replaceAll("SG", "SG" + (translate ? "(Snow Grains)" : ""));
        weather = weather.replaceAll("IC", "IC" + (translate ? "(Ice Crystals)" : ""));
        weather = weather.replaceAll("PL", "PL" + (translate ? "(Ice Pellets)" : ""));
        weather = weather.replaceAll("GR", "GR" + (translate ? "(Hail)" : ""));
        weather = weather.replaceAll("GS", "GS" + (translate ? "(Small Hail)" : ""));
        weather = weather.replaceAll("UP", "UP" + (translate ? "(Unknown Precip.)" : ""));

        /*
         * Obscuration
         */
        weather = weather.replaceAll("BR", "BR" + (translate ? "(Mist)" : ""));
        weather = weather.replaceAll("FG", "FG" + (translate ? "(Fog)" : ""));
        weather = weather.replaceAll("FU", "FU" + (translate ? "(Smoke)" : ""));
        weather = weather.replaceAll("DU", "DU" + (translate ? "(Dust)" : ""));
        weather = weather.replaceAll("SA", "SA" + (translate ? "(Sand)" : ""));
        weather = weather.replaceAll("HZ", "HZ" + (translate ? "(Haze)" : ""));
        weather = weather.replaceAll("PY", "PY" + (translate ? "(Spray)" : ""));
        weather = weather.replaceAll("VA", "VA" + (translate ? "(Volcanic Ash)" : ""));

        /*
         * Other
         */
        weather = weather.replaceAll("SQ", "SQ" + (translate ? "(Squalls)" : ""));
        weather = weather.replaceAll("FC", "FC" + (translate ? "(Funnel Cloud)" : ""));
        weather = weather.replaceAll("SS", "SS" + (translate ? "(Sand Storm)" : ""));
        weather = weather.replaceAll("DS", "DS" + (translate ? "(Dust Storm)" : ""));
        weather = weather.replaceAll(" VC", " VC" + (translate ? "(In Vicinity)" : ""));

        weather = weather.replaceAll("SKC", "SKC" + (translate ? "(Sky Clear)" : ""));
        weather = weather.replaceAll("CLR", "CLR" + (translate ? "(Clear <12,000ft)" : "")); // see http://www.faraim.org/aim/aim-4-03-14-494.html
        weather = weather.replaceAll("BKN", "BKN" + (translate ? "(Broken)" : ""));
        weather = weather.replaceAll("SCT", "SCT" + (translate ? "(Scattered)" : ""));
        weather = weather.replaceAll("OVC", "OVC" + (translate ? "(Overcast)" : ""));
        weather = weather.replaceAll("PROB", "PROB" + (translate ? "(Probability%)" : ""));
        weather = weather.replaceAll("VV", "VV" + (translate ? "(Vertical Visibility)" : ""));
        weather = weather.replaceAll("CB", "CB" + (translate ? "(Cumulonimbus)" : ""));
        weather = weather.replaceAll("WS", "WS" + (translate ? "(Wind Shear)" : ""));

        /*
         * These are remarks
         */
        
        for(int i = 1 ; i < strip.length; i++) {
        
            String weather1 = strip[i];
            
            weather1 = weather1.replaceAll("AO", "AO" + (translate ? "(Station Type)" : ""));
            weather1 = weather1.replaceAll("RAB", "RAB" + (translate ? "(Rain Began)" : ""));
            weather1 = weather1.replaceAll("RAE", "RAE" + (translate ? "(Rain Ended)" : ""));
            weather1 = weather1.replaceAll("CIG", "CIG" + (translate ? "(Variable Ceiling)" : ""));
            weather1 = weather1.replaceAll("SLP", "SLP" + (translate ? "(Sea Level Pressure)" : ""));
            weather1 = weather1.replaceAll("RVRNO","RVRNO" + (translate ? "(No RVR reported)" : ""));
            weather1 = weather1.replaceAll("NOSIG", "NOSIG" + (translate ? "(No Significant Change Expected)" : ""));
            weather1 = weather1.replaceAll("TSNO", "TSNO" + (translate ? "(Thunderstorm Info Not Available)" : ""));
            weather1 = weather1.replaceAll("PK WND", "PK WND" + (translate ? "(Peak Wind)" : ""));
            weather1 = weather1.replaceAll("WSHFT", "WSHFT" + (translate ? "(Wind Shift)" : ""));
            weather1 = weather1.replaceAll("VIS", "VIS" + (translate ? "(Visibility)" : ""));
            weather1 = weather1.replaceAll("PRESRR", "PRESFR" + (translate ? "(Pressure Raising Rapidly)" : ""));
            weather1 = weather1.replaceAll("PRESFR", "PRESFR" + (translate ? "(Pressure Falling Rapidly)" : ""));
            weather1 = weather1.replaceAll("\\$", "\\$" + (translate ? "(Station Maintenance Needed)" : ""));
            
            weather += " RMK" + (translate ? "(Remark)" : " ") + weather1;
        }

        return identAndTime + " " + weather;
    }


    /**
     * Color code winds
     * @param weather
     * @return
     */
    public static String formatWindsHTML(String weather, boolean translate) {
        /*
         * Read wind as XXKT
         */
        int start;
        String output = "";
        String original = weather;
        /*
         * Cannot crash if transmission from HTTP has a bug
         */
        try {
            while((start = weather.indexOf("KT")) >= 0) {
                String portion = weather.substring(0, start);
                String windString = portion.substring(portion.length() - 2, portion.length());
                int winds = Integer.parseInt(windString);
                 
                if(winds < 10) {
                    portion = portion.substring(0, portion.length() - 2) + "<font color='#78e825'>" + windString + "<font color='white'>" + "KT";
                }
                else if(winds < 20) {
                    portion = portion.substring(0, portion.length() - 2) + "<font color='#008aff'>" + windString + "<font color='white'>" + "KT";
                }
                else if(winds < 30) {
                    portion = portion.substring(0, portion.length() - 2) + "<font color='#ff2a00'>" + windString + "<font color='white'>" + "KT";
                }
                else {
                    portion = portion.substring(0, portion.length() - 2) + "<font color='#ff54f9'>" + windString + "<font color='white'>" + "KT";
                }
                output += portion;
                weather = weather.substring(start + 2, weather.length());
            }
            output += weather;
        }
        catch (Exception e) {
            /*
             * Mark magenta as we do not know whats in it
             */
            output = "<font color='#ff54f9'>" + original + "<font color='white'>";
        }
        output = output.replaceAll("VRB", "VRB" + (translate ? "(Variable)" : ""));
        return output;
    }
    
    /**
     * Color code PIREPs
     * @param weather
     * @return
     */
    public static String formatPirepHTML(String weather, boolean translate) {
        weather = weather.replaceAll("UA", "UA" + (translate ? "(Upper Air)" : ""));
        weather = weather.replaceAll("UUA", "UUA" + (translate ? "(Urgent)" : ""));
        weather = weather.replaceAll("/OV", "/OV" + (translate ? "(Location)" : ""));
        weather = weather.replaceAll("/TM", "/TM" + (translate ? "(Time UTC)" : ""));
        weather = weather.replaceAll("/FL", "/FL" + (translate ? "(Altimeter MSL)" : ""));
        weather = weather.replaceAll("UNKN", "UNKN" + (translate ? "(Unknown)" : ""));
        weather = weather.replaceAll("DURC", "DURC" + (translate ? "(During Climb)" : ""));
        weather = weather.replaceAll("DURD", "DURD" + (translate ? "(During Descent)" : ""));
        weather = weather.replaceAll("/TP", "/TP" + (translate ? "(Aircraft Type)" : ""));
        weather = weather.replaceAll("/SK", "/SK" + (translate ? "(Sky Condition)" : ""));
        weather = weather.replaceAll("SKC", "SKC" + (translate ? "(Sky Clear)" : ""));
        weather = weather.replaceAll("BKN", "BKN" + (translate ? "(Broken)" : ""));
        weather = weather.replaceAll("SCT", "SCT" + (translate ? "(Scattered)" : ""));
        weather = weather.replaceAll("OVC", "OVC" + (translate ? "(Overcast)" : ""));
        weather = weather.replaceAll("/WX", "/WX" + (translate ? "(Weather)" : ""));
        weather = weather.replaceAll("/TA", "/TA" + (translate ? "(Temperature)" : ""));
        weather = weather.replaceAll("/WV", "/WV" + (translate ? "(Wind Velocity)" : ""));
        weather = weather.replaceAll("/IAS", "/IAS" + (translate ? "(Indicated Airspeed)" : ""));
        weather = weather.replaceAll("/IC", "/IC" + (translate ? "(Ice)" : ""));
        weather = weather.replaceAll("CLR", "CLR" + (translate ? "(Clear)" : ""));
        weather = weather.replaceAll("MXD", "MXD" + (translate ? "(Mixed)" : ""));
        weather = weather.replaceAll("RIM", "RIM" + (translate ? "(Rime)" : ""));
        weather = weather.replaceAll("TRC", "TRC" + (translate ? "(Trace)" : ""));
        weather = weather.replaceAll("MOD", "MOD" + (translate ? "(Moderate)" : ""));
        weather = weather.replaceAll("LGT", "LGT" + (translate ? "(Light)" : ""));
        weather = weather.replaceAll("SVR", "SVR" + (translate ? "(Severe)" : ""));
        weather = weather.replaceAll("HVY", "HVY" + (translate ? "(Heavy)" : ""));
        weather = weather.replaceAll("/RM", "/RM" + (translate ? "(Remarks)" : ""));
        weather = weather.replaceAll("/TB", "/TB" + (translate ? "(Turbulence)" : ""));

        return weather;
    }
    
    /**
     * Color code winds
     * @param weather
     * @return
     */
    public static String formatVisibilityHTML(String weather) {
        /*
         * Read wind as XXKT
         */
        int start;
        String output = "";
        String original = weather;
        /*
         * Cannot crash if transmission from HTTP has a bug
         */
        try {
            while((start = weather.indexOf("SM")) >= 0) {
                String portion = weather.substring(0, start);
                int portionlen = portion.length();
                int vis;
                int visstart;
                String visString;
                boolean P6 = false;
                String subportion = portion.substring(portionlen - 8);
                /*
                 * Too hard to parse this nonsense. Brute force.
                 */
                if(subportion.matches(".* P6")) {
                    visstart = portionlen - 2;
                    vis = 6;  
                    P6 = true;
                }
                else if(subportion.matches(".* [1-9]{1} [1-3]{1}\\/[2-4]{1}")) {
                    /*
                     * Like " 1 1/4"
                     */
                    visstart = portionlen - 5;
                    vis = Integer.parseInt(portion.substring(visstart, visstart + 1));  
                }
                else if(subportion.matches(".* [1]{1}[1-3]{1}\\/[2-4]{1}")) {
                    /*
                     * Like " 11/4"
                     */
                    visstart = portionlen - 4;
                    vis = Integer.parseInt(portion.substring(visstart, visstart + 1));  
                }
                else if(subportion.matches(".* [1-3]{1}\\/[2-4]{1}")) {
                    /*
                     * Like " 3/4"
                     */
                    visstart = portionlen - 3;
                    vis = 0;  
                }
                else {
                    /*
                     * Like " 3"
                     */
                    visstart = portionlen - 1;
                    vis = Integer.parseInt(portion.substring(visstart, visstart + 1));  
                }

                visString = portion.substring(visstart);
                
                if(P6) {
                    portion = portion.substring(0, visstart) + "<font color='#78e825'>" + "P6" + "<font color='white'>" + "SM";                    
                }
                else if(vis >= 5) {
                    portion = portion.substring(0, visstart) + "<font color='#78e825'>" + visString + "<font color='white'>" + "SM";
                }
                else if(vis >= 3) {
                    portion = portion.substring(0, visstart) + "<font color='#008aff'>" + visString + "<font color='white'>" + "SM";
                }
                else if(vis >= 1) {
                    portion = portion.substring(0, visstart) + "<font color='#ff2a00'>" + visString + "<font color='white'>" + "SM";
                }
                else {
                    portion = portion.substring(0, visstart) + "<font color='#ff54f9'>" + visString + "<font color='white'>" + "SM";
                }
                output += portion;
                weather = weather.substring(start + 2, weather.length());
            }
            output += weather;
        }
        catch (Exception e) {
            /*
             * Mark magenta as we do not know whats in it
             */
            output = "<font color='#ff54f9'>" + original + "<font color='white'>";
        }
        return output;
    }


    /**
     * Returns time from METAR
     * @param metar
     * @return
     */
    public static String getMetarTime(String metar) {
        String time = "";
        // parse time, temp, altitude setting 
        String tokens[] = metar.split(" ");
        if(tokens.length > 1) {
            time = tokens[1];
        }
        
        return time;
    }

    /**
     * Returns density altitude for a field from its METAR and elevation
     * @param metar
     * @param elev
     * @return
     */
    public static String getDensityAltitude(String metar, String elev) {
        
        if(null == elev || null == metar) {
            return "";
        }
        
        double da = 0;
        double temp = 0;
        double as = 0;
        
        double st = 0;
        double at = 0;
       
        double pa = 0;
        double elevation = 0;
        
        boolean tmpset = false;
        boolean aset = false;
        boolean melev = false;
        
        // parse time, temp, altitude setting 
        String tokens[] = metar.split(" ");
        
        try {
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i].equals("RMK")) {
                    break;
                }
                if(tokens[i].matches("M?[0-9]*/M?[0-9]*")) {
                    String t = tokens[i].split("/")[0];
                    if(t.startsWith("M")) {
                        t = t.substring(1);
                    }
                    temp = Double.parseDouble(t);
                    tmpset = true;
                    continue;
                }
                if(tokens[i].matches("A[0-9][0-9][0-9][0-9]")) {
                    as = Double.parseDouble(tokens[i].split("A")[1]) / 100;
                    aset = true;
                    continue;
                }
            }
            elevation = Double.parseDouble(elev);
            melev = true;
        }
        catch (Exception e) {
        }
        
        if(tmpset && aset && melev) {
            
            // pressure altitude, correct for non standard
            pa = elevation + (29.92 - as) * 1000.0;
            
            // standard temp Kelvin
            st = 273.15 - (15 - 0.0019812 * pa);

            // reported temp Kelvin
            at = 273.15 - temp;

            // density altitude, aviation formulary
            da = pa + 118.6 * (st - at);
            
            // round to nearest 100
            da = ((int)(da / 100)) * 100;
            
            return "" + (int)da + " ft";
        }

        return "";
    }
    
    /**
     * Returns best wind aligned runway from METAR
     * @param metar
     * @param runways
     * @return
     */
    public static String getBestRunway(String metar, LinkedList<String> runways) {
        
        if(null == runways || null == metar) {
            return "";
        }
        
        String wind = "";
        double dir = 0;
        double windspeed = 0;
        double gustspeed = 0;
        
        boolean windset = false;
        
        // parse time, temp, altitude setting 
        String tokens[] = metar.split(" ");
        
        try {
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i].equals("RMK")) {
                    break;
                }
                if(tokens[i].matches(".*KT")) {
                    wind = tokens[i];
                    // first 3 digits are direction true, or VRB.
                    String tmp = wind.substring(0, 3);
                    if(tmp.equals("VRB")) {
                        // variable, almost calm
                        tmp = "000";
                    }
                    dir = Double.parseDouble(tmp);
                    // next 2 digits are speed
                    windspeed = Double.parseDouble(wind.substring(3, 5));
                    // could be gusting
                    if(wind.contains("G")) {
                        // gusting to
                        gustspeed = Double.parseDouble(wind.substring(6, 8));
                    }
                    windset = true;
                    continue;
                }
            }
        }
        catch (Exception e) {
        }
        
        double headgusts = 0;
        double headwind = 0;
        double crossgusts = 0;
        double crosswind = 0;
        
        if(windset) {
            /*
             * Find best wind-aligned runway name
             */
            double maxWind = -1E10;
            String best_description = "";
            for(String s : runways) {
                String runway_name_and_heading[] = s.split(",");
                try {
                    double rwy_heading = runway_name_and_heading.length == 2
                            ? Double.parseDouble(runway_name_and_heading[1])  // parse rwy heading
                            : Double.parseDouble(runway_name_and_heading[0])*10; // parse rwy name - case of private fields which have no heading info
                    // find cross and head wind components
                    // aviation formulary
                    headwind = windspeed * Math.cos(Math.toRadians(dir - rwy_heading));
                    if(headwind > maxWind) {
                        // find runway with max headwind component
                        maxWind = headwind;
                        crosswind = windspeed * Math.sin(Math.toRadians(dir - rwy_heading));
                        if(gustspeed != 0) {
                            headgusts = gustspeed * Math.cos(Math.toRadians(dir - rwy_heading));
                            crossgusts = gustspeed * Math.sin(Math.toRadians(dir - rwy_heading));
                        }
                        best_description = runway_name_and_heading[0] // rwy name
                                         + "\n " + Math.abs((int)headwind)
                                         + (gustspeed != 0 ? "G" + Math.abs((int)headgusts) : "")
                                         // T = tail, H = head, L = left, R = right
                                         + (headwind < 0 ? "KT Tail" : "KT Head")
                                         + "\n " + Math.abs((int)crosswind)
                                         + (gustspeed != 0 ? "G" + Math.abs((int)crossgusts) : "")
                                         + (crosswind < 0 ? "KT Left X" : "KT Right X");
                    }
                }
                catch (Exception e) {
                    continue;
                }
                
            }
            return best_description;
        }

        return "";
    }

}
