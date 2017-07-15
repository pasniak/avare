/****************************************************************************************
 * Copyright (c) 2011 Norbert Nagold <norbert.nagold@gmail.com>                         *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/

package com.ds.avare.voice;

import android.content.Context;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.ds.avare.R;
import com.ds.avare.storage.Preferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class ReadText {
    private static TextToSpeech mTts;
    private static ArrayList<Locale> availableTtsLocales = new ArrayList<>();
    private static String mTextToSpeak;
    private static WeakReference<Context> mContext;
    private static Preferences mPref;

    public static final String NO_TTS = "0";
    public static ArrayList<String[]> sTextQueue = new ArrayList<>();
    public static HashMap<String, String> mTtsParams;


    private static void textMessage(String text) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            // On UI thread.
            Toast.makeText(mContext.get(), text, Toast.LENGTH_LONG).show();
        }
    }

    private static void speak(String text, String loc) {
        if (!mPref.isTalkEnabled()) return;

        int result = mTts.setLanguage(new Locale(loc));
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            textMessage( mContext.get().getString(R.string.no_tts_available_message)+" ("+loc+")");
        } else {
            if (mTts.isSpeaking()) {
                stopTts();
            }
            mTts.speak(mTextToSpeak, TextToSpeech.QUEUE_FLUSH, mTtsParams);
        }
    }


    public static void textToSpeech(String text) {
        if (!mPref.isTalkEnabled()) return;
        if (mTextToSpeak!=null && mTextToSpeak.equals(text)) return;

        mTextToSpeak = text;
        // get the user's existing language preference
        String language = "eng";

        // rebuild the language list if it's empty
        if (availableTtsLocales.isEmpty()) {
            buildAvailableLanguages();
        }
        // Check, if stored language is available
        for (int i = 0; i < availableTtsLocales.size(); i++) {
            if (language.equals(NO_TTS)) {
                // user has chosen not to read the text
                return;
            } else if (language.equals(availableTtsLocales.get(i).getISO3Language())) {
                speak(mTextToSpeak, language);
                return;
            }
        }
    }


    public static void initializeTts(Context context) {
        mPref = new Preferences(context);
        if (!mPref.isTalkEnabled()) return;

        if (mTts!=null) return; // TTS was already initialized

        // Store weak reference to Activity to prevent memory leak
        mContext = new WeakReference<>(context);

        // Create new TTS object and setup its onInit Listener
        mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // build list of available languages
                    buildAvailableLanguages();
                    if (availableTtsLocales.size() > 0) {
                        // notify the reviewer that TTS has been initialized
                        ///((AbstractFlashcardViewer) mContext.get()).ttsInitialized(); TODO
                    } else {
                        textMessage(mContext.get().getString(R.string.no_tts_available_message));
                    }
                } else {
                    textMessage(mContext.get().getString(R.string.no_tts_available_message));
                }
                Compat.setTtsOnUtteranceProgressListener(mTts);
            }
        });
        mTtsParams = new HashMap<>();
        mTtsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
        // Show toast that it's getting initialized, as it can take a while before the sound plays the first time
        Toast.makeText(context, context.getString(R.string.initializing_tts), Toast.LENGTH_LONG).show();
    }

    public static void buildAvailableLanguages() {
        if (!mPref.isTalkEnabled()) return;

        availableTtsLocales.clear();
        Locale[] systemLocales = Locale.getAvailableLocales();
        for (Locale loc : systemLocales) {
            try {
                int retCode = mTts.isLanguageAvailable(loc);
                if (retCode >= TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    availableTtsLocales.add(loc);
                } else {
                    //Timber.v("ReadText.buildAvailableLanguages() :: %s  not available (error code %d)", loc.getDisplayName(), retCode);
                }
            } catch (IllegalArgumentException e) {
                //Timber.e("Error checking if language " + loc.getDisplayName() + " available");
            }
        }
    }


    public static void releaseTts() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
    }


    public static void stopTts() {
        if (mTts != null) {
            if (sTextQueue != null) {
                sTextQueue.clear();
            }
            mTts.stop();
        }
    }

    static class Compat {
        static public void setTtsOnUtteranceProgressListener(TextToSpeech tts) {
            tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    if (ReadText.sTextQueue.size() > 0) {
                        String[] text = ReadText.sTextQueue.remove(0);
                        ReadText.speak(text[0], text[1]);
                    }
                }
            });
        }
    }

    static private String sayHeading(double heading) {
        String headingStr = Integer.toString((int)Math.round(heading));
        headingStr = headingStr.length() == 1 ? "00"+headingStr 
                   : headingStr.length() == 2 ? "0"+headingStr 
                   : headingStr;  
        String headingText = headingStr.replaceAll("(.)", "$1 "); // add spaces to read separate digits
        return ICAOPhoneticAlphabet.convert(headingText);
    }

    static public void navigateToLast(String destination, double heading, int patternAlt) {
        if (!mPref.isTalkEnabled()) return;
        if (destination == null) return;

        String announce = String.format(
                mContext.get().getString(R.string.ttsDescendTo),
                ICAOPhoneticAlphabet.convert(destination),
                sayHeading(heading),
                Integer.toString(patternAlt));

        ReadText.textToSpeech(announce);
    }

    static public void navigateTo(String destination, double heading) {
        if (!mPref.isTalkEnabled()) return;
        if (destination==null) return;

        String announce = String.format(
                mContext.get().getString(R.string.ttsNavigateTo),
                ICAOPhoneticAlphabet.convert(destination),
                sayHeading(heading));

        ReadText.textToSpeech(announce);
    }

    static public void destinationSetTo(String destination, double heading) {
        if (!mPref.isTalkEnabled()) return;
        if (destination==null) return;

        String announce = String.format(
                mContext.get().getString(R.string.ttsDestinationSet),
                ICAOPhoneticAlphabet.convert(destination),
                sayHeading(heading));

        ReadText.textToSpeech(announce);
    }
}