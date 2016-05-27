package com.uniovi.informaticamovil.cid.Circuits;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniovi.informaticamovil.cid.DB.CIDbHelper;
import com.uniovi.informaticamovil.cid.R;

import java.util.ArrayList;


public class CircuitFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Circuit>>{
    private static final String URL = "http://datos.gijon.es/doc/informacion/circuitos-footing.json";
    private static final String PREFERENCES = "SettingsCircuit";
    private static final int CIRCUIT_LOADER = 1;

    private RecyclerView mRecyclerView;
    private View view;
    private CIDbHelper mCIDb;
    private SharedPreferences mSettings;
    private CircuitsAdapter adapter;

    public static CircuitFragment newInstance(){
        CircuitFragment fragment = new CircuitFragment();
        return fragment;
    }

    public CircuitFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circuits, container, false);

        mSettings = getActivity().getSharedPreferences(PREFERENCES, 0);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.circuitRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // Makes the recycler view like a list
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        //BD initialization
        mCIDb= new CIDbHelper(getContext());

        // if application is installed gets de data base data
        if(mSettings.getBoolean("isInstalled", false) ) {
            populateList(mCIDb.leerCircuitos());
        }
        // if not are downloaded then download the data
        else {
            Bundle bundle = new Bundle();
            bundle.putString("URL", URL);
            getLoaderManager().initLoader(CIRCUIT_LOADER, bundle, this);
        }

        return view;
    }

    @Override
    public Loader<ArrayList<Circuit>> onCreateLoader(int id, Bundle args) {
        CircuitLoader loader = new CircuitLoader(getContext(), args.getString("URL"));
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Circuit>> loader, ArrayList<Circuit> data) {
        mCIDb.insertarCircuitos(data);
        populateList(data);
        // Señala que los datos han sido descargados
        mSettings.edit().putBoolean("isInstalled", true).commit();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Circuit>> loader) {
        populateList(null);
    }

    protected void populateList(ArrayList<Circuit> circuitList) {
        if (circuitList != null) {
            adapter = new CircuitsAdapter(circuitList);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void updateContent(String query){
        adapter.getFilter().filter(query);
    }


}
