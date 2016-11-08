package fretx.version4.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;

import fretx.version4.activities.main.MainActivity;

public class BluetoothScanTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public BluetoothScanTask(Context ctx) { context = ctx; }

    @Override protected Void doInBackground(Void... inputs) {
        Bluetooth.adapter.startLeScan(scanCallback);
        return null;
    }

    @Override protected void onPostExecute(Void result) { }

    private final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            String device_name = device.getName();
            if( device_name == null ) return;
            if( ! device_name.equals(Bluetooth.deviceName) ) return;
            Bluetooth.adapter.stopLeScan(scanCallback);
            Bluetooth.fretxDevice = device;
            Bluetooth.Gatt = Bluetooth.fretxDevice.connectGatt(context, false, Bluetooth.gattCallback);
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if ( status   != BluetoothGatt.GATT_SUCCESS          ) return;
            if ( newState == BluetoothProfile.STATE_CONNECTED    )  {
                gatt.discoverServices();
                Bluetooth.isConnected = true;
            }
            if ( newState == BluetoothProfile.STATE_DISCONNECTED ) Bluetooth.isConnected = false;
            MainActivity act = (MainActivity) context;
            act.showConnectionState();
        }
    };

}
