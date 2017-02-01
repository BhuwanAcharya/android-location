package com.salmanwahed.locationproject.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.salmanwahed.locationproject.R;

import java.util.Locale;

/**
 * Use this class for continuous location update
 */

public class GPSLocService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "SMW_GPS";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public boolean mRequestingLocUpdate;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static boolean isEnded = false;

    public GPSLocService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "GPSLocationService Created");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isEnded = false;
        mRequestingLocUpdate = false;
        initGoogleAPIClient();
        if (mGoogleApiClient.isConnected() && mRequestingLocUpdate) {
            Log.i(TAG, "Connected GoogleApi");
        }
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();
    }

    private synchronized void initGoogleAPIClient() {
        Log.d(TAG, "Creating googleAPIClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();

//        Sets the desired interval for active location updates. This interval is
//        inexact. You may not receive updates at all if no location sources are available, or
//        you may receive them slower than requested. You may also receive updates faster than
//        requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

//         Sets the fastest rate for active location updates. This interval is exact, and your
//         application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    public void startLocationUpdate() {
        Log.d(TAG, "startLocationUpdates");
        if (!mRequestingLocUpdate) {
            mRequestingLocUpdate = true;
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location Permission OK");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                isEnded = true;
            }
        }
    }

    public void stopLocationUpdate(){
        Log.d(TAG, "stopLocationUpdate");
        if (mRequestingLocUpdate){
            mRequestingLocUpdate = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void broadcastLocation(Context context, Location location){
        Log.d(TAG, "broadcastLocation");
        Intent intent = new Intent(getString(R.string.intent_action_location_update));
        intent.putExtra(getString(R.string.bundle_key_location), location);
        Log.i(TAG, String.format(Locale.US, "Broadcasting location. Lat: %f, Lon: %f", location.getLatitude(),
                location.getLongitude()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /** ConnectionCallbacks overrides **/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApi");
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, String.format(Locale.US, "Connection suspended: %d", i));
        mGoogleApiClient.connect();
    }

    /** OnConnectionFailedListener overrides**/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, String.format(Locale.US, "Connection failed: %s", connectionResult.getErrorCode()));
    }

    /** LocationListener overrides **/

    @Override
    public void onLocationChanged(Location location) {
        broadcastLocation(this,location);
    }
}
