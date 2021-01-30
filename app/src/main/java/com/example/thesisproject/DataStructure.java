package com.example.thesisproject;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public final class DataStructure {

    //public Double COLUMN_ID;// = "ID";
    public String DEVICE_NAME;// = "name";
    public Double DEVICE_RSSI;// = "rssi";
    public String CONDITIONS;// = "conditions";
    public String DATE;

    public DataStructure() {
    }

    public DataStructure(String DEVICE_NAME, Double DEVICE_RSSI, String CONDITIONS, String DATE) {
        //this.COLUMN_ID = COLUMN_ID;
        this.DEVICE_NAME = DEVICE_NAME;
        this.DEVICE_RSSI = DEVICE_RSSI;
        this.CONDITIONS = CONDITIONS;
        this.DATE = DATE;
    }
}
