package fretx.version4;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class BluetoothClass {

    static String tag = "debug";
    static BluetoothSocket mmSocket;

    private static final int SUCCESS_CONNECT = 0;
    private static final int MESSAGE_READ = 1;
    private static final int ARDUINO = 2;

    private static ConnectedThread connectedThread = null;

    static Handler mHandler = new Handler( new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothClass.SUCCESS_CONNECT:
                    BluetoothClass.connectedThread = new BluetoothClass.ConnectedThread( (BluetoothSocket) msg.obj );
                    break;
                case BluetoothClass.ARDUINO:
                    if ( Config.bBlueToothActive ) { BluetoothClass.connectedThread.write( (byte[]) msg.obj ); }
                    break;
            }
            return false;
        }
    });

    private static class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {     // Get the input and output streams, using temp objects because member streams are final
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }   catch (IOException e) { Log.e(tag, "tmpIn or tmpOut"); }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes;      // bytes returned from read()

            while (true) {  // Keep listening to the InputStream until an exception occurs
                try {
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget(); // Send the obtained bytes to the UI activity
                }   catch (IOException e) { break; }
            }
        }

        public void write(byte[] bytes) {                           // Call this from the main activity to send data to the remote device
            try                   { mmOutStream.write(bytes);  }
            catch (IOException e) { Log.e(tag, "mmOutStream"); }
        }
    }
}