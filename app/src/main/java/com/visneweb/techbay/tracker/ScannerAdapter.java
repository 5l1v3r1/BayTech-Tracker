package com.visneweb.techbay.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aureo on 16.02.2018.
 */

public class ScannerAdapter extends BaseAdapter {
    private List<MyDevice> devices = new ArrayList<>();
    private Context context;
    private SharedPreferences pref;


    public ScannerAdapter(Context c) {
        this.context = c;
        pref = PreferenceManager.getDefaultSharedPreferences(c);
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
        private Switch track;
        private MyDevice device;
        private Intent i = new Intent(context, BluetoothService.class);


        public ViewHolder(View v, final int position) {
            device = getItem(position);
            name = v.findViewById(com.visneweb.techbay.tracker.R.id.name);
            name.setText(device.getName());
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
            Set<String> addresses = pref.getStringSet("devices", new HashSet<String>());
            addresses.add(device.getAdress());
            pref.edit().putStringSet("devices", addresses).apply();
            i.putExtra("address", device.getAdress());
            context.startService(i);
        }

        private void stopTracking() {
            Set<String> addresses = pref.getStringSet("devices", new HashSet<String>());
            addresses.remove(device.getAdress());
            pref.edit().putStringSet("devices", addresses).apply();
            context.stopService(i);
        }
    }
}
