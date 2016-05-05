package com.uniovi.informaticamovil.cid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uniovi.informaticamovil.cid.Circuits.Circuit;
import com.uniovi.informaticamovil.cid.DB.CIDbHelper;
import com.uniovi.informaticamovil.cid.Facilities.Facilitie;

import java.util.ArrayList;


/**
 * Created by Luis on 20/4/16.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mMapView;
    private GoogleMap mMap;

    public static MapFragment newInstance(){
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public MapFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

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

        mapearCircuitos();
        mapearInstalaciones();
    }

    public void mapearCircuitos(){
        CIDbHelper CIDb = new CIDbHelper(getContext());
        ArrayList<Circuit> circuits = CIDb.leerCircuitos();

        if(circuits != null) {
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

    public void mapearInstalaciones(){
        CIDbHelper CIDb = new CIDbHelper(getContext());
        ArrayList<Facilitie> facilities = CIDb.leerFacilities();

        if(facilities != null) {
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

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
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
}
