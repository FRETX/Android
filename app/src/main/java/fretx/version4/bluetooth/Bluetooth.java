package fretx.version4.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import fretx.version4.callbacks.Callbacks;

import static fretx.version4.R.string.connect;

public final class Bluetooth {

  public static String           deviceName = "FretX";
  public static BluetoothDevice  fretxDevice;
  public static BluetoothGatt    Gatt;

  public static Boolean          isInitialized;
  public static Boolean          isSupported;
  public static Boolean          isEnabled;
  public static Boolean          isConnected;
  public static BluetoothAdapter adapter;

  public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
  public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

  public static void Initialize() {
      adapter       = BluetoothAdapter.getDefaultAdapter();
      isSupported   = adapter instanceof BluetoothAdapter;
      isEnabled     = adapter.isEnabled();
      isConnected   = false;
      isInitialized = true;
  }

  public static void Enable( Activity activity ) {
      if ( ! isSupported ) return;
      if (   isEnabled   ) return;
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, Callbacks.REQUEST_ENABLE_BT);
  }

  public static void onEnableResult(int result, Intent data) { Initialize(); }

  public static void send(byte[] array) {
      if(!isConnected) return;
      String data = new String(array);
      try {
          byte[] send = data.getBytes("UTF-8");
          writeRXCharacteristic(send);
      }
      catch (UnsupportedEncodingException e) { e.printStackTrace(); }
  }

  public static void clear() { send(new byte[]{0}); }

  public static void writeRXCharacteristic(byte[] value)
  {   BluetoothGattService RxService = Gatt.getService(RX_SERVICE_UUID);
        if (RxService == null) return; // No Service

        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) return; // No Characteristic

        RxChar.setValue(value);
        Gatt.writeCharacteristic(RxChar);
  }

    public static final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if ( status   != BluetoothGatt.GATT_SUCCESS          ) return;
            if ( newState == BluetoothProfile.STATE_CONNECTED    )  {
                gatt.discoverServices();
                Bluetooth.isConnected = true;
            }
            if ( newState == BluetoothProfile.STATE_DISCONNECTED ) Bluetooth.isConnected = false;
        }
    };

}
