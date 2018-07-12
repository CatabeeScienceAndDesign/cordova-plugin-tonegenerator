/*
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Brendan Flynn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

 var argscheck = require('cordova/argscheck'),
     utils = require('cordova/utils'),
     exec = require('cordova/exec'),
     timers = {};
 var DEFAULT_FREQUENCY = 440;
 var DEFAULT_VOLUME = 50;
 var MAX_VOLUME = 255;
 var MIN_VOLUME = 0;
 var MAX_FREQUENCY = 16000;
 var MIN_FREQUENCY = 60;
 var MAX_CHANNELS = 8;

 var ToneGenerator = function(){};

 ToneGenerator.prototype = {
   play: function(frequency, volume) {
     frequency = frequency || 440;
     volume = volume || 127;
     if (volume > 255) volume = 255;
     if (volume < 0) volume = 0;
     return cordova.exec(
       function() {
         console.log('Generating tone at ' + frequency + 'Hz..');
       }, function() {
         throw "Error generating tone!";
       },
       "ToneGenerator",
       "startChannel",
       [0, frequency, volume]
     );
   },
   frequency: function(freq) {
     if (freq === undefined) {freq = DEFAULT_FREQUENCY;}
     if (freq <MIN_FREQUENCY) {freq = MIN_FREQUENCY;}
     if (freq > MAX_FREQUENCY) freq = MAX_FREQUENCY;
     return cordova.exec(
       function() {},
       function() {throw "Error updating tone frequency";},
       "ToneGenerator",
       "setFrequencyForChannel",
       [0, freq]
     );
   },
   volume: function(vol) {
     if (vol === undefined) {vol = DEFAULT_VOLUME;}
     if (vol <MIN_VOLUME) {vol = MIN_VOLUME;}
     if (vol > MAX_VOLUME) vol = MAX_VOLUME;
     return cordova.exec(
       function() {},
       function() {throw "Error updating tone volume";},
       "ToneGenerator",
       "setVolumeForChannel",
       [0, vol]
     );
   },
   stop: function() {
     return cordova.exec(
       function() {},
       function() {throw "Error stopping tone";},
       "ToneGenerator",
       "stop",
       []
     );
   },
   startChannel: function(ch, freq, vol) {
     if (ch === undefined || ch < 0) {ch = 0;}
     if (ch >= MAX_CHANNELS) {ch = 0;}
     if (vol === undefined) {vol = DEFAULT_VOLUME;}
     if (vol <MIN_VOLUME) {vol = MIN_VOLUME;}
     if (vol > MAX_VOLUME) vol = MAX_VOLUME;
     if (freq === undefined) {freq = DEFAULT_FREQUENCY;}
     if (freq <MIN_FREQUENCY) {freq = MIN_FREQUENCY;}
     if (freq > MAX_FREQUENCY) freq = MAX_FREQUENCY;
     return cordova.exec(
       function() {},
       function(){throw "Error starting channel";},
       "ToneGenerator",
       "startChannel",
       [ch, freq, vol]
     );
   },
   setVolumeForChannel: function(ch, vol) {
     if (ch === undefined || ch < 0) {ch = 0;}
     if (ch >= MAX_CHANNELS) {ch = 0;}
     if (vol === undefined) {vol = DEFAULT_VOLUME;}
     if (vol <MIN_VOLUME) {vol = MIN_VOLUME;}
     if (vol > MAX_VOLUME) vol = MAX_VOLUME;
     return cordova.exec(
       function() {},
       function(){throw "Error setting volume for channel";},
       "ToneGenerator",
       "setVolumeForChannel",
       [ch, vol]
     );
   },
   setFrequencyForChannel: function(ch, freq) {
     if (ch === undefined || ch < 0) {ch = 0;}
     if (ch >= MAX_CHANNELS) {ch = 0;}
     if (freq === undefined) {freq = DEFAULT_FREQUENCY;}
     if (freq <MIN_FREQUENCY) {freq = MIN_FREQUENCY;}
     if (freq > MAX_FREQUENCY) freq = MAX_FREQUENCY;
     return cordova.exec(
       function() {},
       function(){throw "Error setting frequency for channel";},
       "ToneGenerator",
       "setFrequencyForChannel",
       [ch, freq]
     );
   },
   stopChannel: function(ch) {
     if (ch === undefined || ch < 0) {ch = 0;}
     if (ch >= MAX_CHANNELS) {ch = 0;}
     return cordova.exec(
       function() {},
       function(){throw "Error setting frequency for channel";},
       "ToneGenerator",
       "stopChannel",
       [ch]
     );
   },
   stopAllChannels: function() {
     return cordova.exec(
       function() {},
       function(){throw "Error setting frequency for channel";},
       "ToneGenerator",
       "stopAllChannels",
       []
     );
   },
   setFadeTime: function(fadeTime) {
     return cordova.exec(
       function() {},
       function(){throw "Error setting fadeTime to " + fadeTime;},
       "ToneGenerator",
       "setFadeTime",
       [fadeTime]
     );
   },
   setRampTime: function(rampTime) {
     return cordova.exec(
       function() {},
       function(){throw "Error setting rampTime to " + rampTime;},
       "ToneGenerator",
       "setRampTime",
       [rampTime]
     );
   }
 };

 module.exports = new ToneGenerator();
