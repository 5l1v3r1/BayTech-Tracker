package com.visneweb.techbay.tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by riskactive on 16.03.2018.
 */

public class ScannerFragment extends ListFragment {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private Switch scan;
    private ScannerAdapter adapter;
    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("BLT","scanned!");
            MyDevice dev = new MyDevice(result);
            boolean found = false;
            for (int i = 0; i < adapter.getCount(); i++) {
                try {
                    if (adapter.getItem(i).equals(dev)) {
                        if (adapter.getItem(i).isTracked()) {
                            adapter.getItem(i).track(result.getRssi());
                            adapter.notifyDataSetChanged();
                        }
                        found = true;
                        break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            if (!found) {
                adapter.add(dev);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scanner,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        adapter = new ScannerAdapter(getContext());
        setListAdapter(adapter);
        scan = view.findViewById(com.visneweb.techbay.tracker.R.id.scan);
        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    startScanning();
                } else {
                    stopScanning();
                }
            }
        });
        btManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Log.i("BLT","null blt adapater");
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        super.onViewCreated(view, savedInstanceState);
    }
    public void startScanning() {
        Log.i("BLT", "start scanning");
        if(btScanner==null){
            Log.i("BLT","no scanner");
        } else {
            Log.i("BLT", "scanner info: " + btScanner.toString());
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        Log.i("BLT", "stop scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }
}
