package fretx.version4;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayAdapter<String> listAdapter;
    ListView listView;
    BluetoothAdapter btAdapter;

    List<BluetoothDevice> deviceList;
    private DeviceAdapter deviceAdapter;
    Map<String, Integer> devRssiValues;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> Devices;
    ArrayList<BluetoothDevice> devices;
    private String Name = "FretX";

    public static BluetoothGatt mBluetoothGatt;
    public static BluetoothDevice fretx;
    static final int DISC = 1;
    static final int FRET = 2;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    IntentFilter filter;
    BroadcastReceiver receiver;

    Button m_btnGoBack;

    String macAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        m_btnGoBack = (Button)findViewById(R.id.btGoBack);
        m_btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.stopLeScan(mLeScanCallback);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(), "No Bluetooth on your phone!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!btAdapter.isEnabled()){
                turnOnBT();
            }

            /** scan for the FRETX**/

            btAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //btAdapter.stopLeScan(mLeScanCallback);
        Log.v("debug", "================================================================");
    }

    private void turnOnBT() {
        Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 0);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device,final int rssi,
                                     byte[] scanRecord) {
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            /*if(device.getName().equals(Name)) {
                                btAdapter.stopLeScan(mLeScanCallback);
                                listAdapter.add("Device Found");
                                fretx = device;
                                listAdapter.add("Connecting...");
                                mBluetoothGatt = fretx.connectGatt(getApplicationContext(), false, mGattCallback);
                                Config.bBlueToothActive = true;
                            }*/
                            addDevice(device,rssi);
                        }
                    });
                }
            };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }


        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
            //mEmptyList.setVisibility(View.GONE);




            deviceAdapter.notifyDataSetChanged();
        }
    }

    private void init() {

        deviceList = new ArrayList<BluetoothDevice>();
        deviceAdapter = new DeviceAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

        ListView newDevicesListView = (ListView) findViewById(R.id.listView);
        newDevicesListView.setAdapter(deviceAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


        /*BluetoothDevice selectedDevice = devices.get(arg2);
        //fretx = selectedDevice;
        //mBluetoothGatt = fretx.connectGatt(getApplicationContext(), false, mGattCallback);
        Log.i(BluetoothClass.tag, "in click listener");*/
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt,int status,int newState) {

            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {

                //Discover services
                gatt.discoverServices();
                finish();

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {

                //Handle a disconnect event

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

    };

    @Override
    public void onBackPressed() {
    }

    public void exportEmailInCSV(final Context context, final String user, final String mac) throws IOException {
        try {
            FileWriter fw = new FileWriter(context.getFilesDir().toString() + "/" + Constants.USER_INFO_FILE);

            fw.append(user);
            fw.append(',');
            fw.append(mac);

            fw.append('\n');
            // fw.flush();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;
            switch (msg.what) {
                case BluetoothActivity.FRET:
                    String data = new String(writeBuf);
                    byte[] send;
                    try {
                        //send data to service
                        send = data.getBytes("UTF-8");
                        writeRXCharacteristic(send);
                        //Update the log with time stamp
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case BluetoothActivity.DISC:
                    Config.bBlueToothActive = false;
                    mBluetoothGatt.disconnect();
                    break;
            }
            return false;
        }
    };
    static Handler mHandler = new Handler(callback);

    public static void writeRXCharacteristic(byte[] value)
    {


        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
        if (RxService == null) {
            //Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_SHORT).show();
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) {
            //Toast.makeText(getBaseContext(), "No Characteristic", Toast.LENGTH_SHORT).show();
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
    }

    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<BluetoothDevice> devices;
        LayoutInflater inflater;

        public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }

            BluetoothDevice device = devices.get(position);
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

            tvrssi.setVisibility(View.VISIBLE);
            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
            if (rssival != 0) {
                tvrssi.setText("RSSI = " + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());

            return vg;
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceList.get(position);
            btAdapter.stopLeScan(mLeScanCallback);

            //BluetoothDevice selectedDevice = devices.get(position);
            fretx = device;
            mBluetoothGatt = fretx.connectGatt(getApplicationContext(), false, mGattCallback);
            Config.bBlueToothActive = true;
        }
    };

}

