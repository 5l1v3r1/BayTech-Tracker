package com.visneweb.techbay.tracker;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private ViewPager pager;
    private MyPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.visneweb.techbay.tracker.R.layout.main);
        pager = findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "intent detected", Toast.LENGTH_SHORT).show();

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d("BLT", "onNewIntent: "+intent.getAction());

        if(tag != null) {
            Toast.makeText(this, "tag detected", Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

//            if (isDialogDisplayed) {
//
//                if (isWrite) {
//
//                    String messageToWrite = mEtMessage.getText().toString();
//                    mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
//                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);
//
//                } else {
//
//                    mNfcReadFragment = (NFCReadFragment)getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
//                    mNfcReadFragment.onNfcDetected(ndef);
//                }
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.settings) {
//            Intent intent = new Intent(this,SettingsActivity.class);
//            this.startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
