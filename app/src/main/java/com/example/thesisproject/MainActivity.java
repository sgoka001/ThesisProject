package com.example.thesisproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<String> deviceList;
    ArrayAdapter adapter;
    TextView statusTextView;

    BluetoothAdapter bluetoothAdapter;
    int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
            {
                statusTextView.setText("Finished.");
                Button button = findViewById(R.id.searchButton);
                button.setEnabled(true);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName() == null)
                {
                    deviceList.add("DEVICE: " + device.getAddress() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                }
                else if(device.getName() != null)
                {
                    deviceList.add("DEVICE: " + device.getName() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    public void onSearchClicked(View view) {
        int hasPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            statusTextView.setText("Searching...");
            deviceList.clear();
            bluetoothAdapter.startDiscovery();
            view.setEnabled(false);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        deviceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);

        registerReceiver(broadcastReceiver, intentFilter);
    }
}