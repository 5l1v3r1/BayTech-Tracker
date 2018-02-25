package com.visneweb.techbay.tracker;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aureo on 16.02.2018.
 */

public class ScannerAdapter extends BaseAdapter {
    private List<MyDevice> devices = new ArrayList<>();
    private Context context;

    public ScannerAdapter(Context c) {
        this.context = c;
    }

    public void add(MyDevice device) {
        devices.add(device);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public MyDevice getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(com.visneweb.techbay.tracker.R.layout.bluetooth_item, null);
        }
        ViewHolder vh = new ViewHolder(view, i);
        view.setTag(vh);
        return view;
    }


    private class ViewHolder {
        private TextView name;
        private TextView rssi;
        private Switch track;
        private MyDevice device;
        private BluetoothGatt gatt;
        private BluetoothGattCallback callback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.i("BLT", "status" + status);
                Log.i("BLT", "new state" + status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.i("BLT", "rssi: " + rssi);
                notifyDataSetChanged();
            }
        };

        public ViewHolder(View v, final int position) {
            device = getItem(position);
            name = v.findViewById(com.visneweb.techbay.tracker.R.id.name);
            name.setText(device.getName());
            if (device.isTracked()) {
                rssi = v.findViewById(R.id.rssi);
                rssi.setText(device.getRssi());
                rssi.setVisibility(View.VISIBLE);
            } else {
                rssi.setVisibility(View.GONE);
            }
            track = v.findViewById(com.visneweb.techbay.tracker.R.id.track);
            track.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        track();
                    } else {
                        stopTracking();
                    }
                }
            });
        }

        private void track() {

        }

        private void stopTracking() {
            gatt.disconnect();
        }
    }
}
