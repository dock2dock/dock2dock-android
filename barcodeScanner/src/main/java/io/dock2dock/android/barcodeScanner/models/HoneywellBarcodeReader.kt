package io.dock2dock.android.barcodeScanner.models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.*
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.ScannerNotClaimedException
import com.honeywell.aidc.ScannerUnavailableException
import com.honeywell.aidc.UnsupportedPropertyException

interface IBarcodeReader {
    fun initialise()
    fun setBarcodeReaderListener(listener: IBarcodeReaderListener)
    fun removeBarcodeReaderListener()
    fun setBarcodeTriggerListener(listener: IBarcodeTriggerListener)
    fun removeBarcodeTriggerListener()
}

interface IBarcodeReaderListener {
    fun onBarcodeScanned(barcode: String)
    fun onBarcodeFailure()
}

interface IBarcodeTriggerListener {
    fun onBarcodeTrigger()
}

class HoneywellBarcodeReader(private val context: Context): IBarcodeReader, DefaultLifecycleObserver {

    private var reader: BarcodeReader? = null
    private var barcodeReaderListener: BarcodeReader.BarcodeListener? = null
    private var barcodeTriggerListener: TriggerListener? = null
    private var barcodeManager: AidcManager? = null

    override fun initialise() {
        AidcManager.create(context) { aidcManager ->
            barcodeManager = aidcManager
            // use the manager to create a BarcodeReader with a session
            // associated with the internal imager.
            reader = barcodeManager?.createBarcodeReader()

            try {
                reader?.claim()
                // set the trigger mode to client control
                reader?.setProperty(
                    BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL
                )

                reader?.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_CODE_128_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_GS1_128_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_UPC_A_ENABLE, true)
                reader?.setProperty(BarcodeReader.PROPERTY_EAN_13_ENABLED, true)
                reader?.setProperty(BarcodeReader.PROPERTY_AZTEC_ENABLED, false)
                reader?.setProperty(BarcodeReader.PROPERTY_CODABAR_ENABLED, false)
                reader?.setProperty(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false)
                reader?.setProperty(BarcodeReader.PROPERTY_PDF_417_ENABLED, false)

                //Set Max Code 39 barcode length
                reader?.setProperty(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10)
                // Turn on center decoding
                reader?.setProperty(BarcodeReader.PROPERTY_CENTER_DECODE, true)
                // Enable bad read response
                reader?.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true)

                barcodeReaderListener?.let {
                    reader?.addBarcodeListener(it)
                }

                barcodeTriggerListener?.let {
                    reader?.addTriggerListener(it)
                }

            } catch (e: UnsupportedPropertyException) {
                Toast.makeText(context, "Failed to apply properties", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setBarcodeReaderListener(listener: IBarcodeReaderListener) {
        barcodeReaderListener = object : BarcodeReader.BarcodeListener {
            override fun onBarcodeEvent(event: BarcodeReadEvent) {
                try {
                    reader?.softwareTrigger(false)
                    val data = event.barcodeData
                    listener.onBarcodeScanned(data)

                } catch (e: ScannerNotClaimedException) {
                    e.printStackTrace()
                } catch (e: ScannerUnavailableException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    //errorDidOccur("Error", e.localizedMessage)
                }
            }

            override fun onFailureEvent(p0: BarcodeFailureEvent?) {
                listener.onBarcodeFailure()
            }
        }
    }

    override fun removeBarcodeReaderListener() {
        reader?.removeBarcodeListener(barcodeReaderListener)
    }

    override fun setBarcodeTriggerListener(listener: IBarcodeTriggerListener) {
        barcodeTriggerListener = TriggerListener { event ->
            try {
                //only handle trigger presses

                //turn on/off aimer, illumination and decoding
                reader?.aim(event.state)
                reader?.light(event.state)
                reader?.decode(event.state)

                listener.onBarcodeTrigger()

            } catch (e: ScannerNotClaimedException) {
                e.printStackTrace()
                //showToastMsg("Scanner is not claimed")
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
                //showToastMsg("Scanner is unavailable")
            }
        }
    }

    override fun removeBarcodeTriggerListener() {
        reader?.removeTriggerListener(barcodeTriggerListener)
    }

    //endregion

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (reader != null) {
            reader?.release()
        }
    }

    //close barcodeReader to clean up resources.
    //once closed, the object can no longer be used
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (reader != null) {
            removeBarcodeReaderListener()
            removeBarcodeTriggerListener()
            reader?.close()
        }
        if (barcodeManager != null) {
            barcodeManager?.close()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (reader != null) {
            try {
                reader?.claim()
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
                //Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }
}