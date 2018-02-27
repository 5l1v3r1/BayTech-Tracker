package com.visneweb.techbay.tracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class BluetoothService extends Service {


    public static final int WARN_ID = 101;
    public static final int ALARM_ID = 102;
    public static final int WEAK = -70;
    public static final int MEDIUM = -60;
    public static final int STRONG = -50;
    public static final int WARNING_DELAY = 1000;
    public static final int SIGNAL_DELAY = 300;
    private static final int MODE_WARNING = 201;
    private static final int MODE_ALARM = 202;
    private PendingIntent pi;
    private Uri alarmSound = null;
    private Uri warningSound = null;
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
            connection.readRemoteRssi();
            delayRun.postDelayed(signalCheck, SIGNAL_DELAY);
        }
    };
    private Runnable letWarn = new Runnable() {
        @Override
        public void run() {
            canWarn = true;
        }
    };
    private Runnable dontWarnForAWhile = new Runnable() {
        @Override
        public void run() {
            canWarn = false;
            delayRun.postDelayed(letWarn, WARNING_DELAY);
        }
    };
    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (canWarn) {
                try {
                    if (rssi < WEAK) {
                        Log.i("BLT", "signal is weak");
                        warn(MODE_ALARM);
                        dontWarnForAWhile.run();
                    } else if (rssi < MEDIUM) {
                        warn(MODE_WARNING);
                        Log.i("BLT", "signal is medium");
                        dontWarnForAWhile.run();

                    } else if (rssi > STRONG) {
                        Log.i("BLT", "device is near, stops warning..");
                    }
                } catch (NoNotificationManagerException e) {
                    e.printStackTrace();
                }
                Log.i("BLT", "rssi: " + rssi);
            }
        }
    };

    private void warn(int mode) throws NoNotificationManagerException {
        Uri sound = null;
        String title = null;
        switch (mode) {
            case MODE_WARNING:
                sound = warningSound;
                title = getString(R.string.forgot);
                break;
            case MODE_ALARM:
                sound = alarmSound;
                title = getString(R.string.losing);
                break;
        }
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            throw new NoNotificationManagerException();
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "warn",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Warn for far object");
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "WARN")
                .setSmallIcon(R.drawable.ic_techbay_logo)
                .setContentTitle(title)
                .setContentText(getString(R.string.detail))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (sound != null) {
            builder.setSound(sound);
        }
        manager.notify(WARN_ID, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String address = intent.getStringExtra("address");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String alarmString = pref.getString("alarmSound", null);
        String warningString = pref.getString("alarmSound", null);

        if (alarmString != null) {
            alarmSound = Uri.parse(alarmString);
        }
        if (warningString != null) {
            warningSound = Uri.parse(warningString);
        }
        Intent in = new Intent(this, DeviceActivity.class);
        in.putExtra("address", address);
        pi = PendingIntent.getActivity(this, 1, in, PendingIntent.FLAG_UPDATE_CURRENT);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        connection = device.connectGatt(getApplicationContext(), true, callback);
        connection.connect();
        signalCheck.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        connection.disconnect();
        connection.close();
    }
}