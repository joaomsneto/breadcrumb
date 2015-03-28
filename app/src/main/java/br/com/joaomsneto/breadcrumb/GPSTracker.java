package br.com.joaomsneto.breadcrumb;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by joao on 24/03/15.
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private static LatLng prev;
    private static boolean flag = false;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    ArrayList<Location> locations;
    GoogleMap map;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;
    private boolean marcarNoMapa;

    public GPSTracker( Context context, GoogleMap map, ArrayList<Location> locations ) {
        this.mContext = context;
        this.locations = locations;
        this.map = map;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if( !isGPSEnabled ) {

            } else {
                this.canGetLocation = true;

                if( isGPSEnabled ) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);
                    if( locationManager != null ) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if( location != null ) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS() {
        if( locationManager != null ) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if( location != null ) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if( location != null ) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Desativado");
        alertDialog.setMessage("GPS está desabilitado. Você deseja habilitar nas configurações?");
        alertDialog.setPositiveButton("Configurações", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which ) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public void trocarStatusTrajetoNoMapa(boolean marcarNoMapa) {
        this.marcarNoMapa = marcarNoMapa;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.locations.add(location);
        if( this.marcarNoMapa ) {
            /* PolylineOptions polylineOptions = new PolylineOptions();
            for(Location unLocation : this.locations) {
                polylineOptions.add(new LatLng(unLocation.getLatitude(), unLocation.getLongitude()));
            }
            polylineOptions.geodesic(true);
            Polyline line = this.map.addPolyline(polylineOptions);
            line.
            this.map.addPolyline() */
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

            if(!flag)  //when the first update comes, we have no previous points,hence this
            {
                prev=current;
                flag=true;
            }
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(current, 16);
            map.animateCamera(update);
            map.addPolyline((new PolylineOptions())
                    .add(prev, current).width(6).color(Color.BLUE)
                    .visible(true));
            prev=current;
            current = null;
        }
        Toast.makeText(mContext, location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
