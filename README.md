---
title: Tone Generator
description: Play a tone at specified frequency and volume
---

An arbitrary tone generation plugin for cordova apps.

It generates raw tones at specific frequencies and amplitudes, this can be used to produce an interactive pitch response to various user actions, or to generate an electrical waveform through the headphone output jack to drive low-power AC electrical motors.

See [this page](https://github.com/CatabeeScienceAndDesign/FlexVolt) for an example app using this plugin.

Installation
------------

<code> cordova plugin add https://github.com/CatabeeScienceAndDesign/cordova-plugin-tonegenerator </code>


Methods
-------
- cordova.plugins.tonegenerator.play
- cordova.plugins.tonegenerator.frequency
- cordova.plugins.tonegenerator.volume
- cordova.plugins.tonegenerator.stop


cordova.plugins.tonegenerator.play()
-------------------------------------------

Starts playing a tone.

<pre>
<code>
  cordova.plugins.tonegenerator.play(frequency [, amplitude] [, waveType])
</code>
</pre>

Default values as follows:

- volume: 127 (50%, max is 255)
- waveType: 0 (Sine Wave)

Currently, only Sine Waves are supported.

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

Stops playing the generated tone.

<pre>
<code>
  cordova.plugins.tonegenerator.stop()
</code>
</pre>

Supported Platforms
-------------------

- iOS (TODO)
- Android
