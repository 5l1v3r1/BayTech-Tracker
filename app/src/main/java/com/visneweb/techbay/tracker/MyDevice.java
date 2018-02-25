package com.visneweb.techbay.tracker;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.util.Log;

/**
 * Created by aureo on 25.02.2018.
 */

public class MyDevice {
    private BluetoothDevice dev;
    private int rssi;
    private boolean tracked = false;

    public MyDevice(ScanResult result) {
        dev = result.getDevice();
    }

    @Override
    public boolean equals(Object dev) {
        if (dev instanceof MyDevice) {
            return ((MyDevice) dev).getAdress().equals(this.getAdress());
        } else {
            Log.i("BLT", "not same class to check method equals: expected class: MyDevice; found: " + dev.getClass().getSimpleName());
            return false;
        }
    }

    public String getAdress() {
        return dev.getAddress();
    }

    public String getName() {
        return dev.getName();
    }

    public void track(int rssi) {
        Log.i("BLT", "trying to track");
        this.rssi = rssi;
        tracked = true;
    }

    public void stopTrack() {
        rssi = 0;
        tracked = false;
    }

    public int getRssi() {
        return rssi;
    }

    public boolean isTracked() {
        return tracked;
    }

}
