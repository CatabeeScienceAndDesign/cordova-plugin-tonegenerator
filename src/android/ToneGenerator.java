/**
*   ToneGenerator.java
*
*   A Java Class for the Cordova Tone Generator Plugin
*
*   @by Steven de Salas (desalasworks.com | github/sdesalas)
*   @licence MIT
*
*   @see https://github.com/sdesalas/cordova-plugin-tonegenerator
*   @see https://audioprograming.wordpress.com/2012/10/18/a-simple-synth-in-android-step-by-step-guide-using-the-java-sdk/
*   @see http://stackoverflow.com/questions/2413426/playing-an-arbitrary-tone-with-android
*   @see http://stackoverflow.com/questions/25684821/how-create-a-50hz-square-wave-on-android-and-play-it
*/

package org.apache.cordova.tonegenerator;

import java.lang.Math;
import java.lang.Thread;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;


public class ToneGenerator extends CordovaPlugin  {

    private static final String TAG = "ToneGenerator";

    // general fields
    private static final int SAMPLE_RATE = 8000;
    private static final int START_VOLUME = 0;
    private static final int START_FREQUENCY = 440;
    private static final int MAX_CHANNELS = 8;

    Thread t;
    private static boolean isRunning = false;

    private static int[] volumes = new int[MAX_CHANNELS];
    private static int[] frequencies = new int[MAX_CHANNELS];
    private static int[] newFrequencies = new int[MAX_CHANNELS];

    private static final int STATE_OFF = 0;
    private static final int STATE_FADE_IN = 1;
    private static final int STATE_CROSS_FADE = 2;
    private static final int STATE_PLAY = 3;
    private static final int STATE_FADE_OUT = 4;
    private static int[] channelStates = new int[MAX_CHANNELS];

    // fade management - have to fade new tones in, and crossfade frequency changes
    private static int[] fadeCount = new int[MAX_CHANNELS];
    private static int fadeSamples = 4000;  // 5 ms
    private static final int FADE_TIME_MAX = 10000;
    private static final int FADE_TIME_MIN = 0;

    public ToneGenerator() {
        // initialize arrays
        for (int iCh = 0; iCh < MAX_CHANNELS; iCh++){
            channelStates[iCh] = STATE_OFF;
            volumes[iCh] = START_VOLUME;
            frequencies[iCh] = START_FREQUENCY;
            newFrequencies[iCh] = START_FREQUENCY;
        }
    }

    public void onDestroy() {
        this.stop();
    }

    public void onReset() {
        this.stop();
    }

    //--------------------------------------------------------------------------
    // Cordova Plugin Methods
    //--------------------------------------------------------------------------

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("startChannel")) {
            int ch = args.getInt(0);
            this.frequencies[ch] = args.getInt(1);
            this.volumes[ch] = args.getInt(2);
            Log.d(TAG, "Updated Freq: " + this.frequencies[ch] + " and Volume: " + this.volumes[ch]);
            if (!isRunning) {
                Log.d(TAG, "Starting With Channel " + ch);
                // playing, but not this channel
                channelStates[ch] = STATE_PLAY; // turn channel on
                //this.startChannel(ch);
                startChannel();
            } else {
                Log.d(TAG, "Already Started.  Adding Channel " + ch);
                // already playing this channel, already updated freq and vol
                channelStates[ch] = STATE_FADE_IN; // set channel flag
                fadeCount[ch] = 0;
            }
        }
        else if (action.equals("stop")) {
            this.stop();
        }
        else if (action.equals("stopChannel")) {
            int ch = args.getInt(0);
            this.stopChannel(ch);
        }
        else if (action.equals("setFrequencyForChannel")) {
            int ch = args.getInt(0);
            int freq = args.getInt(1);
            // TODO ensure within limits
            changeFrequencyForChannel(ch, freq);
        }
        else if (action.equals("setVolumeForChannel")) {
            int ch = args.getInt(0);
            int volume = args.getInt(1);
            changeVolumeForChannel(ch, volume);
        }
        else if (action.equals("setFadeTime")) {
            // takes int, milliseconds
            int tmp = args.getInt(0);
            if (tmp > FADE_TIME_MAX) tmp = FADE_TIME_MAX;
            if (tmp < FADE_TIME_MIN) tmp = FADE_TIME_MIN;
            Log.d(TAG, "Changing fade time to " + tmp);
            fadeSamples = tmp * SAMPLE_RATE / 1000;
            Log.d(TAG, "Changed fade samples to " + fadeSamples);
        }
        else {
            // Unsupported action
            return false;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    // Local Methods
    //--------------------------------------------------------------------------

    private int startChannel() {

        try {
            isRunning = true;
            t = new Thread() {
                public void run() {
                    // set process priority
                    setPriority(Thread.MAX_PRIORITY);
                    int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    buffsize /= 4; // shorter buffer means slower change lag

                    // create an audiotrack object
                    Log.d(TAG, "Buffer Size: " + Integer.toString(buffsize));
                    AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                    AudioFormat format = new AudioFormat.Builder()
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .build();
                    AudioTrack audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(attributes)
                        .setAudioFormat(format)
                        .setBufferSizeInBytes(buffsize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build();

                    short samples[] = new short[buffsize];
                    double twopi = 8. * Math.atan(1.);
                    double ph = 0.0;

                    // start audio
                    Log.d(TAG, "Starting multichannel");
                    audioTrack.play();

                    int amp, ampIn, ampOut;
                    double newFreq = 0.0, freq = 0.0;

                    while (isRunning) {
                        // initialize samples[] to 0's, so each channel can add on
                        for (int i = 0; i < buffsize; i++) {
                            samples[i] = 0;
                        }
                        // store phase so it can be incremented in the loop for each channel
                        double phTmp = ph;
                        for (int iCh = 0; iCh < MAX_CHANNELS; iCh++) {
                            // only run channels that are on
                            if (channelStates[iCh] != STATE_OFF) {
                                // grab channel-specific properties
                                freq = frequencies[iCh] * 1.0;
                                amp = volumes[iCh] * 128;
                                // reset phase
                                ph = phTmp;
                                for (int i = 0; i < buffsize; i++) {
                                    if (channelStates[iCh] == STATE_FADE_IN) {
                                        // fade in for new channels
                                        ampIn = amp * fadeCount[iCh] / fadeSamples;
                                        samples[i] += (short) (ampIn * Math.sin(freq*ph));
                                        fadeCount[iCh] ++;
                                        if (fadeCount[iCh] >= fadeSamples) {
                                            Log.d(TAG, "Fade In Complete");
                                            channelStates[iCh] = STATE_PLAY;
                                        }
                                    } else if (channelStates[iCh] == STATE_FADE_OUT) {
                                        // fade in for new channels
                                        ampOut = amp * (fadeSamples - fadeCount[iCh]) / fadeSamples;
                                        samples[i] += (short) (ampOut * Math.sin(freq*ph));
                                        fadeCount[iCh] ++;
                                        if (fadeCount[iCh] >= fadeSamples) {
                                            Log.d(TAG, "Fade Out Complete");
                                            channelStates[iCh] = STATE_OFF;
                                            checkForRemainingChannels();
                                        }
                                    } else if (channelStates[iCh] == STATE_CROSS_FADE) {
                                        // cross fade for frequency changes to avoid non-0-crossing pops
                                        ampOut = amp * (fadeSamples - fadeCount[iCh]) / fadeSamples;
                                        ampIn = amp * fadeCount[iCh] / fadeSamples;
                                        newFreq = newFrequencies[iCh] * 1.0;
                                        samples[i] += (short) (ampOut * Math.sin(freq*ph) + ampIn * Math.sin(newFreq*ph));
                                        fadeCount[iCh] ++;
                                        if (fadeCount[iCh] >= fadeSamples) {
                                            Log.d(TAG, "Cross Fade Complete");
                                            channelStates[iCh] = STATE_PLAY;
                                            frequencies[iCh] = newFrequencies[iCh];
                                            freq = frequencies[iCh] * 1.0;
                                        }
                                    } else if (channelStates[iCh] == STATE_PLAY){
                                        // no fading, just generate the signal
                                        samples[i] += (short) (amp * Math.sin(freq*ph));
                                    } else {
                                        // off
                                    }

                                    ph += twopi / SAMPLE_RATE;
                                }
                            }
                        }
                        audioTrack.write(samples, 0, samples.length);
                    }
                    audioTrack.stop();
                    audioTrack.release();
                }
            };
            t.start();

        }

        // If error notify consumer
        catch (Exception ex) {
            return 0;
        }

        return 1;
    }

    private void changeFrequencyForChannel(int ch, int freq) {
        // TODO ensure within limits
        channelStates[ch] = STATE_CROSS_FADE;
        fadeCount[ch] = 0;
        newFrequencies[ch] = freq;
        //this.frequencies[ch] = fr;  // not yet - need to fade it in
        Log.d(TAG, "Changing Frequency from " + frequencies[ch] + " to " + newFrequencies[ch] + " for channel " + ch);
    }

    private void changeVolumeForChannel(int ch, int volume) {
        // TODO ensure within limits
        volumes[ch] = volume;
        Log.d(TAG, "Set Volume: " + volumes[ch]  + " for channel " + ch);
    }

    /**
     * Stop tone.
     */
    private void stop() {
        Log.d(TAG, "Stop All Sounds");

        // single channel
        isRunning = false;
        try {
            t.join();
        } catch (Exception ex) {}
        t = null;

        // multichannel
        for (int iCh = 0; iCh < MAX_CHANNELS; iCh++){
            channelStates[iCh] = STATE_OFF;
        }
        isRunning = false;
        try {
            t.join();
        } catch (Exception ex) {}
        t = null;

        Log.d(TAG, "Stop Complete");
    }

    /**
     * Stop tone.
     */
    private void stopChannel(int ch) {
        Log.d(TAG, "Stop Channel " + ch);
        if (channelStates[ch] != STATE_OFF) {
            //channel is on - turn it off
            fadeCount[ch] = 0;
            channelStates[ch] = STATE_FADE_OUT;
        }
    }

    private void checkForRemainingChannels() {
        // if all channels are off stop the thread
        boolean allChannelsAreOff = true;
        for (int iCh = 0; iCh < MAX_CHANNELS; iCh++){
            if (channelStates[iCh] != STATE_OFF) {
                allChannelsAreOff = false;
            }
        }
        if (allChannelsAreOff){
            // no channels left - clean up thread
            Log.d(TAG, "Stopped the last channel - shutting down the thread.");
            isRunning = false;
            try {
                t.join();
            } catch (Exception ex) {}
            t = null;
        }
    }

}
