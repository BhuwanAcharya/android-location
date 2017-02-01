package com.salmanwahed.locationproject.views;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.salmanwahed.locationproject.services.GPSLocService;
import com.salmanwahed.locationproject.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView mTvLat;
    private TextView mTvLon;
    private static final String TAG = "SMW_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvLat = (TextView) findViewById(R.id.tvLat);
        mTvLon = (TextView) findViewById(R.id.tvLon);
    }

    @Override
    protected void onStart() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Request to access location data");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            Log.i(TAG, "Location permission not granted");
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(getString(R.string.intent_action_location_update)));

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startService(new Intent(this, GPSLocService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(getString(R.string.bundle_key_location));
            if (location != null){
                mTvLat.setText(String.format(Locale.US, "Latitude: %f", location.getLatitude()));
                mTvLon.setText(String.format(Locale.US, "Longitude: %f", location.getLongitude()));
            }

        }
    };

}
