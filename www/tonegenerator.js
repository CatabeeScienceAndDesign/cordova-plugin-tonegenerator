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

var ToneGenerator = function(){};

ToneGenerator.prototype = {
  play: function(frequency, volume, waveType) {
    frequency = frequency || 440;
    volume = volume || 127;
    if (volume > 255) volume = 255;
    if (volume < 0) volume = 0;
    waveType = waveType || 1;
    return cordova.exec(function() {
      console.log('Generating tone at ' + frequency + 'Hz..');
    }, function() {
      throw "Error generating tone!";
    }, "ToneGenerator", "play", [frequency, volume, waveType]);
  },
  frequency: function(hz) {
    return cordova.exec(function() {}, function() {throw "Error updating tone frequency";}, "ToneGenerator", "frequency", [hz || 0]);
  },
  volume: function(vol) {
    if (vol > 255) vol = 255;
    if (vol < 0) vol = 0;
    return cordova.exec(function() {}, function() {throw "Error updating tone volume";}, "ToneGenerator", "volume", [vol || 0]);
  },
  stop: function() {
    return cordova.exec(function() {}, function() {throw "Error stopping tone";}, "ToneGenerator", "stop", []);
  }
};

module.exports = new ToneGenerator();
