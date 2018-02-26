package com.visneweb.techbay.tracker;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class BluetoothService extends Service {
    public static final int WEAK = -80;
    public static final int MEDIUM = -65;
    public static final int STRONG = -55;
    public static final int WARNING_DELAY = 10000;
    public static final int SIGNAL_DELAY = 3000;
    private boolean canWarn = true;
    private BluetoothGatt connection;
    private Handler delayRun = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    private Runnable signalCheck = new Runnable() {
        @Override
        public void run() {
            //TODO check signal power each second
            Toast.makeText(getApplicationContext(), "checking the signal..", Toast.LENGTH_SHORT).show();
            connection.readRemoteRssi();
            delayRun.postDelayed(signalCheck, SIGNAL_DELAY);
        }
    };
    private Runnable letWarn = new Runnable() {
        @Override
        public void run() {
            canWarn = true;
            //TODO check signal power each second
            Toast.makeText(getApplicationContext(), "now can warn again if device is far", Toast.LENGTH_SHORT).show();
        }
    };
    private Runnable dontWarnForAWhile = new Runnable() {
        @Override
        public void run() {
            canWarn = false;
            Toast.makeText(getApplicationContext(), "do not warn for milliseconds of: " + WARNING_DELAY, Toast.LENGTH_SHORT).show();
            delayRun.postDelayed(letWarn, WARNING_DELAY);
        }
    };
    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (canWarn) {
                if (rssi < WEAK) {
                    Toast.makeText(getApplicationContext(), " weak rssi: " + rssi, Toast.LENGTH_SHORT).show();
                    //TODO make a notification with call melody
                    dontWarnForAWhile.run();
                } else if (rssi < MEDIUM) {
                    Toast.makeText(getApplicationContext(), " medium rssi: " + rssi, Toast.LENGTH_SHORT).show();
                    //TODO make a simple notification
                    dontWarnForAWhile.run();
                }
            } else if (rssi > STRONG) {
                Toast.makeText(getApplicationContext(), "device is near, stops canWarn.. rssi:  " + rssi, Toast.LENGTH_SHORT).show();
                //TODO stop any canWarn if exists
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @Override
    public void onCreate() {
        //TODO prepare service
        Toast.makeText(this, "Bluetooth service is started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String address = intent.getStringExtra("address");
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        connection = device.connectGatt(getApplicationContext(), true, callback);
        connection.connect();
        signalCheck.run();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
        //TODO establish bluetooth connection and start runnabble to check signal power each second
    }

    @Override
    public void onDestroy() {
        connection.disconnect();
        connection.close();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
}