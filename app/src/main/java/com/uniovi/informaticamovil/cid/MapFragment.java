package com.uniovi.informaticamovil.cid;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uniovi.informaticamovil.cid.Circuits.Circuit;
import com.uniovi.informaticamovil.cid.DB.CIDContract;
import com.uniovi.informaticamovil.cid.DB.CIDbHelper;
import com.uniovi.informaticamovil.cid.Facilities.Facilitie;

import java.util.ArrayList;


/**
 * Created by Luis on 20/4/16.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public static final String TAG = MapFragment.class.getSimpleName();
    private MapView mMapView;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private CIDbHelper CIDb;
    private ArrayList<Facilitie> facilities;
    private ArrayList<Circuit> circuits;
    private LatLng myLocation;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        CIDb = new CIDbHelper(getContext());
        circuits = CIDb.leerCircuitos();
        facilities = CIDb.leerFacilities();

        Log.e("datos", "fin de carga de datos");

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mMapView.getMapAsync(this);

        return view;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        if (!SP.getBoolean("mapOption", false)) {
            mapearCircuitos();
            mapearInstalaciones();
        }
    }

    public void mapearCircuitos() {

        if (circuits != null) {
            // Add a markeres and move the camera
            for (Circuit c : circuits) {
                LatLng loc = new LatLng(c.getParsedLocation().first, c.getParsedLocation().second);
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(c.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 11));

            }
        }
    }

    public void mapearInstalaciones() {

        if (facilities != null) {
            // Add a markeres and move the camera
            for (Facilitie f : facilities) {
                if (!f.getLocation().isEmpty()) {
                    LatLng loc = new LatLng(f.getParsedLocation().first, f.getParsedLocation().second);
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(f.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }
        }

    }

    public void mapearPICercano() {

        LatLng currentLocation = myLocation;
        LatLng nearestLocation = new LatLng(0, 0);
        double distMin = Double.MAX_VALUE;
        String pointName = "";

        if (circuits != null) {
            for (Circuit c : circuits) {
                LatLng loc = new LatLng(c.getParsedLocation().first, c.getParsedLocation().second);
                double dist = distanciaEuclidea(currentLocation.latitude, loc.latitude,
                        currentLocation.longitude, loc.longitude);
                if (dist < distMin) {
                    distMin = dist;
                    nearestLocation = loc;
                    pointName = c.getName();
                }
            }
        }

        if (facilities != null) {
            for (Facilitie f : facilities) {
                if (!f.getLocation().isEmpty()) {
                    LatLng loc = new LatLng(f.getParsedLocation().first, f.getParsedLocation().second);
                    double dist = distanciaEuclidea(currentLocation.latitude, loc.latitude,
                            currentLocation.longitude, loc.longitude);
                    if (dist < distMin) {
                        distMin = dist;
                        nearestLocation = loc;
                        pointName = f.getName();
                    }
                }
            }
        }

        mMap.addMarker(new MarkerOptions()
                .position(nearestLocation)
                .title(pointName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearestLocation, 12));

        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title(getString(R.string.map_ubication))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }


    public double distanciaEuclidea(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
        Log.e("datos", "los datos ya deben estar cargados");
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        if (SP.getBoolean("mapOption", false))
            mapearPICercano();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        myLocation = new LatLng(currentLatitude, currentLongitude);
    }



    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}
