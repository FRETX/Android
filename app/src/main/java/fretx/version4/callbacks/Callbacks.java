package fretx.version4.callbacks;

import android.content.Intent;

import fretx.version4.bluetooth.Bluetooth;

public class Callbacks {

    public static final int REQUEST_ENABLE_BT = 1;

    public static void onActivityResult(int request, int result, Intent data) {
        switch(request) {
            case(REQUEST_ENABLE_BT) : Bluetooth.onEnableResult(result, data);
        }
    }
}
