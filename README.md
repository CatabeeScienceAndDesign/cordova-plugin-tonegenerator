---
title: Tone Generator
description: Play one or more tones specifying frequencies and volumes for each
---

An arbitrary tone generation plugin for cordova apps.

Generates pure sine wave tones at user selected frequencies and volumes.  Can be used to produce an interactive pitch response to various user actions, or to generate an electrical waveform through the headphone output jack to drive low-power AC electrical motors.

See [this page](https://github.com/CatabeeScienceAndDesign/FlexVolt) for an example app using this plugin.

Installation
------------

<code> cordova plugin add https://github.com/CatabeeScienceAndDesign/cordova-plugin-tonegenerator </code>


Simple Methods
--------------
- cordova.plugins.tonegenerator.play
- cordova.plugins.tonegenerator.frequency
- cordova.plugins.tonegenerator.volume
- cordova.plugins.tonegenerator.stop

Advanced Methods
----------------
- cordova.plugins.tonegenerator.startChannel
- cordova.plugins.tonegenerator.setFrequencyForChannel
- cordova.plugins.tonegenerator.setVolumeForChannel
- cordova.plugins.tonegenerator.stopChannel

Other Knobs
-----------
- cordova.plugins.tonegenerator.setFadeTime


cordova.plugins.tonegenerator.play()
-------------------------------------------

Starts playing a tone.

<pre>
<code>
  cordova.plugins.tonegenerator.play(frequency [, amplitude])
</code>
</pre>

Default values as follows:

- frequency: 440 (A)
- volume: 127 (50%, max is 255)

cordova.plugins.tonegenerator.frequency()
-------------------------------------------

Sets the frequency of the generated tone (in Hertz). The human audible range is 60hz to 16,000hz.

This method can be used to update the frequency while a tone is playing.

<pre>
<code>
  cordova.plugins.tonegenerator.frequency(hertz)
</code>
</pre>


cordova.plugins.tonegenerator.volume()
-------------------------------------------

Sets the volume (amplitude) of the generated tone. This should be a value between 0 and 255.

This method can be used to update the volume while a tone is playing.

<pre>
<code>
  cordova.plugins.tonegenerator.volume(volume)
</code>
</pre>


cordova.plugins.tonegenerator.stop()
--------------------------------

Stops all currently playing tones.

<pre>
<code>
  cordova.plugins.tonegenerator.stop()
</code>
</pre>


cordova.plugins.tonegenerator.startChannel(channel, frequency, volume)
-------------------------------------------

Starts a tone at [channel] with specified [frequency] and [volume].  Uses fade-in to avoid pops if another tone is already playing.

<pre>
<code>
  cordova.plugins.tonegenerator.play(channel, frequency, volume)
</code>
</pre>

Currently 8 channels are supported.

cordova.plugins.tonegenerator.setFrequencyForChannel(channel, frequency)
-------------------------------------------

Sets the frequency of the generated tone at (in Hertz) for [channel]. The human audible range is 60hz to 16,000hz.

This method can be used to update the frequency while a tone is playing.  The plugin uses cross-fade to avoid pops.

<pre>
<code>
  cordova.plugins.tonegenerator.setFrequencyForChannel(channel, frequency)
</code>
</pre>


cordova.plugins.tonegenerator.setVolumeForChannel(channel, volume)
-------------------------------------------

Sets the volume (amplitude) of the generated tone for [channel]. This should be a value between 0 and 255.

This method can be used to update the volume while a tone is playing.

<pre>
<code>
  cordova.plugins.tonegenerator.setVolumeForChannel(channel, volume)
</code>
</pre>


cordova.plugins.tonegenerator.stopChannel(channel)
--------------------------------

Stops [channel] if playing.  Leaves remaining channels running.  Uses fade-out to avoid pops.

<pre>
<code>
  cordova.plugins.tonegenerator.stop()
</code>
</pre>

cordova.plugins.tonegenerator.setFadeTime(milliseconds)
--------------------------------

Changes the time (in milliseconds) used for fade-in, fade-out, and cross-fade.  These fades are used for adding channels, stopping channels, and changing frequency, respectively, to avoid pops.

<pre>
<code>
  cordova.plugins.tonegenerator.setFadeTime(milliseconds)
</code>
</pre>

Supported Platforms
-------------------

- iOS (TODO)
- Android
