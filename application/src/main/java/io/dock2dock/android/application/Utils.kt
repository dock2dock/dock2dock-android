package io.dock2dock.android.application

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class Utils {
    companion object {

        fun playErrorSound() {
            playErrorTone()
        }

        private fun playErrorTone() {
            val sampleRate = 44100
            val duration = 0.5 // seconds
            val numSamples = (duration * sampleRate).toInt()
            val samples = DoubleArray(numSamples)
            val generatedSnd = ByteArray(2 * numSamples) // Generate sine wave at 250Hz
            val freqOfTone = 250.0 // Hz
            for (i in samples.indices) {
                samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone))
            }
            // Convert to 16-bit PCM sound array
            var idx = 0
            for (dVal in samples) { // Scale to maximum amplitude
                val value =
                    (dVal * 32767).toInt() // In 16-bit PCM, first byte is the low order byte
                generatedSnd[idx++] = (value and 0x00ff).toByte()
                generatedSnd[idx++] = ((value and 0xff00) shr 8).toByte()
            } // Play the sound
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                generatedSnd.size,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play() // Release resources after playing
            audioTrack.setNotificationMarkerPosition(numSamples)
            audioTrack.setPlaybackPositionUpdateListener(object :
                AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack) {
                    track.release()
                }

                override fun onPeriodicNotification(track: AudioTrack) {
                    // No action needed
                }
            })
        }
    }
}