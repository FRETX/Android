package fretx.version4.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import fretx.version4.callbacks.Callbacks;

public final class Bluetooth {

  public static Boolean noSupport;
  private static BluetoothAdapter adapter;

  public static void Initialize() {
    adapter = BluetoothAdapter.getDefaultAdapter();
    noSupport = (adapter == null);
  }

  public static void Enable( Activity activity ) {
      if(noSupport) return;
      if(adapter.isEnabled()) return;
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, Callbacks.REQUEST_ENABLE_BT);
  }

  public static void onEnableResult(int result, Intent data) {
      Log.d("got result", data.getData().toString());
  }

}
