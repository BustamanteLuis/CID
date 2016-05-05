package com.uniovi.informaticamovil.cid.Facilities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Luis on 13/4/16.
 */
public class FacilitieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Facilitie>>{
    private static final String URL = "http://datos.gijon.es/doc/deporte/complejos-deportivos.json";
    private static final int FACILITIE_LOADER = 2;

    private View view;
    private RecyclerView mRecyclerView;
    private CIDbHelper mCIDb;
    private SharedPreferences mSettings;
    private FacilitiesAdapter adapter;

    public static FacilitieFragment newInstance(){
        FacilitieFragment fragment = new FacilitieFragment();
        return fragment;
    }

    public FacilitieFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_facilities, container, false);

        mSettings = getActivity().getSharedPreferences("SettingsFacilitie", 0);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.facilitieRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // Makes the recycler view like a list
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        //BD initialization
        mCIDb= new CIDbHelper(getContext());

        // if application is installed gets de data base data
        if(mSettings.getBoolean("isInstalled", false) ) {
            populateList(mCIDb.leerFacilities());
        }
        // if not are download, make it
        else {
            Bundle bundle = new Bundle();
            bundle.putString("URL", URL);
            getLoaderManager().initLoader(FACILITIE_LOADER, bundle, this);
        }

        return view;
    }

    @Override
    public Loader<ArrayList<Facilitie>> onCreateLoader(int id, Bundle args) {
        FacilitieLoader loader = new FacilitieLoader(getContext(), args.getString("URL"));
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Facilitie>> loader, ArrayList<Facilitie> data) {
        mCIDb.insertarFacilities(data);
        populateList(data);
        mSettings.edit().putBoolean("isInstalled", true).commit();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Facilitie>> loader) {
        populateList(null);
    }

    protected void populateList(ArrayList<Facilitie> facilitieList) {
        if (facilitieList != null) {
            adapter = new FacilitiesAdapter(facilitieList);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void updateContent(String query){
        adapter.getFilter().filter(query);
    }

}
