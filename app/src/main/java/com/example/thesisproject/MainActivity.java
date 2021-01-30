package com.example.thesisproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<String> deviceList;
    ArrayAdapter adapter;
    TextView statusTextView;
    EditText conditions_holder;

    BluetoothAdapter bluetoothAdapter;
    int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
    DataStructure dataStructure;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ContentValues values = new ContentValues();
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                statusTextView.setText("Done.");
                Button button = findViewById(R.id.searchButton);
                button.setEnabled(true);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                /*if(device.getName() == null)
                {
                    deviceList.add("DEVICE: " + device.getAddress() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                }
                else*/
                if (device.getName() != null) {
                    //deviceList.add("DEVICE: " + device.getName() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm" + " code: " + device.hashCode());
                    short hold = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    final double rssi = hold;
                    final String conditions = conditions_holder.getText().toString();
                    final Date date = new Date();

                    if (device.hashCode() == -2108813337) //macbook hashcode
                    {
                        deviceList.add("FOUND MY MACBOOK");
                        deviceList.add("DEVICE: " + device.getName() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                        //DATABASE
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef = database.getReference();
                        Query query = myRef.orderByKey().limitToLast(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.v("register1", dataSnapshot.getChildren().iterator().next().getKey());
                                Log.v("register", String.valueOf(Integer.parseInt(dataSnapshot.getChildren().iterator().next().getKey()) + 1));
                                dataStructure = new DataStructure(device.getName(), rssi, conditions, date.toString());
                                myRef.child(String.valueOf(Integer.parseInt(dataSnapshot.getChildren().iterator().next().getKey()) + 1)).setValue(dataStructure);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                    else if (device.hashCode() == -1149078512) //work laptop hashcode
                    {
                        deviceList.add("FOUND MY LAPTOP");
                        deviceList.add("DEVICE: " + device.getName() + "\nRSSI: " + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                        //DATABASE
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference myRef = database.getReference();
                        Query query = myRef.orderByKey().limitToLast(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.v("register1", dataSnapshot.getChildren().iterator().next().getKey());
                                Log.v("register", String.valueOf(Integer.parseInt(dataSnapshot.getChildren().iterator().next().getKey()) + 1));
                                dataStructure = new DataStructure(device.getName(), rssi, conditions, date.toString());
                                myRef.child(String.valueOf(Integer.parseInt(dataSnapshot.getChildren().iterator().next().getKey()) + 1)).setValue(dataStructure);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
                adapter.notifyDataSetChanged();
            }
            }
    };

    public void onSearchClicked(View view) {
        int hasPermission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            statusTextView.setText("Detecting devices...");
            //deviceList.clear();
            bluetoothAdapter.startDiscovery();
            view.setEnabled(false);
        }
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
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
        conditions_holder = findViewById(R.id.conditions_text);

        //BLUETOOTH
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