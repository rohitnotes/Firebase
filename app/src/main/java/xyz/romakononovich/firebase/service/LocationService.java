package xyz.romakononovich.firebase.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import xyz.romakononovich.firebase.Manifest;

/**
 * Created by romank on 29.06.17.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    public Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private IBinder binder = new MapBinder();
    public static final String ACTION_LOCATION = "action location";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MapBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        mGoogleApiClient = builder.addConnectionCallbacks(this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Location service connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation != null) {
            Log.d(TAG, "initial location: " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
            Intent intent = new Intent(ACTION_LOCATION);
            sendBroadcast(intent);
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Location service suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Location service failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        if (currentLocation != null) {
            Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
            Intent intent = new Intent(ACTION_LOCATION);
            sendBroadcast(intent);
        } else {
            Log.d(TAG, "incoming location was null!");
        }

    }
}
