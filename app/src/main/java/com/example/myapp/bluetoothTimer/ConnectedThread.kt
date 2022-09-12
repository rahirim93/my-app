package com.example.myapp.bluetoothTimer

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder

    class ConnectedThread (mmSocket: BluetoothSocket?, myHandler: Handler) : Thread() {
        private val mmInStream: InputStream? = mmSocket?.inputStream
        private val mmOutStream: OutputStream? = mmSocket?.outputStream
        private var mmBuffer: ByteArray = ByteArray(100000) // mmBuffer store for the stream
        private var handler = myHandler

        override fun run() {
            var numBytes: Int // bytes returned from read()
            var stringBuilder = StringBuilder()
            lateinit var stringToPrint: String

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                Log.d("CheckWorkingThread", "working")
                // Read from the InputStream.
                try {
                    numBytes = mmInStream?.read(mmBuffer)!!
                    Log.d("CheckWorkingThread", "numBytes $numBytes")
                    var stringIncome = String(mmBuffer,0, numBytes)
                    Log.d("CheckWorkingThread", "stringIncome $stringIncome")
                    stringBuilder.append(stringIncome)
                    Log.d("CheckWorkingThread", "stringBuilder $stringBuilder")
                    var endLineIndex = stringBuilder.indexOf("\r\n")
                    Log.d("CheckWorkingThread", "endLineIndex $endLineIndex")
                    if (endLineIndex > 0) {
                        stringToPrint = stringBuilder.substring(0, endLineIndex)
                        Log.d("MyLog", "Message: $stringToPrint")
                        stringBuilder.delete(0, stringBuilder.length)

                        var msg = handler.obtainMessage(1, stringToPrint)
                        handler.sendMessage(msg)
                        //Log.d("MyLog","Размер буфера ${mmBuffer.size}")
                    }
                    //stringBuilder.delete(0, stringBuilder.length)
                } catch (e: IOException) {
                    Log.d("MyLog", "Input stream was disconnected", e)
                    break
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream?.write(bytes)
            } catch (e: Exception) {

            }
        }
    }
