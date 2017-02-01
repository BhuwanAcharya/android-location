package com.salmanwahed.locationproject.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.salmanwahed.locationproject.R;

import java.util.Locale;

/**
 * Use this IntententService class to get location update for one time.
 */


public class LocationUpdateService extends IntentService implements OnConnectionFailedListener, ConnectionCallbacks {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private GoogleApiClient mGoogleApiClient = null;
    private static final String TAG = "SMW_LocUpdate";
    public static final String TRACK_LOCATION = "com.salmanwahed.locationproject.action.TRACK_LOCATION";
    public static final String ACTION_FOO = "com.salmanwahed.locationproject.action.FOO";

    // Dummy Params
    public static final String EXTRA_PARAM1 = "com.salmanwahed.locationproject.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.salmanwahed.locationproject.extra.PARAM2";

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "IntentService created");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (TRACK_LOCATION.equals(action)) {
                handleActionLocation();
            } else if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action location update in the provided background.
     */
    private void handleActionLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission is not OK");
            return;
        }
        Log.d(TAG, "Permission is OK in service");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG, String.format(Locale.US, "Latitude: %f, Longitude: %f", location.getLatitude(), location.getLongitude()));
        Intent intent = new Intent(getString(R.string.intent_action_location_update));
        intent.putExtra(getString(R.string.bundle_key_location), location);
        Log.d(TAG, "Broadcasting location from IntentService");
        sendBroadcast(intent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
