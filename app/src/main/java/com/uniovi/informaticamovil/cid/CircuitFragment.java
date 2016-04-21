package com.uniovi.informaticamovil.cid;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
public class CircuitFragment extends Fragment {
    private static final String URL = "http://datos.gijon.es/doc/informacion/circuitos-footing.json";
    private RecyclerView mRecyclerView;
    private View view;
    private CIDbHelper mCIDb;
    private SharedPreferences mSettings;

    public static CircuitFragment newInstance(){
        CircuitFragment fragment = new CircuitFragment();
        return fragment;
    }

    public CircuitFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circuits, container, false);

        mSettings = getActivity().getSharedPreferences("Settings", 0);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.circuitRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // Makes the recycler view like a list
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(llm);

        //BD initialization
        mCIDb= new CIDbHelper(getContext());

        // if application is installed gets de data base data
        if(mSettings.getBoolean("isInstalled", false) ) {
            Log.e("instalada", "esta instalada");
            populateList(mCIDb.leerCircuitos());
        }
        // if not are download, make it
        else {
            // Descarga los datos, los almacena en la BD y los adapta
            DownloadJSONTask djt = new DownloadJSONTask();
            djt.execute(URL);
        }

        return view;
    }

    private InputStream openHttpInputStream(String myUrl)
            throws MalformedURLException, IOException, ProtocolException {

        InputStream is;
        java.net.URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.connect();

        is = conn.getInputStream();
        return is;
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, ArrayList<Circuit>> {
        private static final String CIRCUITS_TAG = "directorios";
        private static final String CIRCUIT_TAG = "directorio";
        private static final String CONTENT_TAG = "content";
        private static final String NAME_TAG = "nombre";
        private static final String DESCRIPTION_TAG = "descripcion";
        private static final String ADDRES_TAG = "direccion";
        private static final String LOCATION_TAG = "localizacion";
        private static final String IMAGE_TAG = "foto";

        @Override
        protected ArrayList<Circuit> doInBackground(String... urls) {
            // urls vienen de la llamada a execute(): urls[0] es la url
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                // TODO: las cadenas deberían ser recursos
                return null;
            }
        }

        private ArrayList<Circuit> downloadUrl(String myUrl) throws IOException {
            InputStream is = null;

            try {
                is = openHttpInputStream(myUrl);

                return parseJsonBusFile(streamToString(is));
            }catch(JSONException e){
                return null;
            } finally{
                // Asegurarse de que el InputStream se cierra
                if (is != null){
                    is.close();
                }
            }
        }

        // Pasa un InputStream a un String
        public String streamToString(InputStream stream) throws IOException,
                UnsupportedEncodingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            do{
                length = stream.read(buffer);
                if (length != -1) {
                    baos.write(buffer, 0, length);
                }
            } while (length != -1);
            return baos.toString("UTF-8");
        }

        // Muestra el resultado en un list_view
        @Override
        protected void onPostExecute(ArrayList<Circuit> result) {
            mCIDb.insertarCircuitos(result);
            populateList(result);
            mSettings.edit().putBoolean("isInstalled", true).commit();
        }

        private ArrayList<Circuit> parseJsonBusFile(String jsonCircuitsInformation)
                throws JSONException {
            ArrayList<Circuit> result = new ArrayList<Circuit>();

            JSONObject root = new JSONObject(jsonCircuitsInformation);
            JSONObject circuits = root.getJSONObject(CIRCUITS_TAG);
            JSONArray circuitArray = circuits.getJSONArray(CIRCUIT_TAG);

            for (int i = 0; i < circuitArray.length(); i++) {
                JSONObject aCircuit = circuitArray.getJSONObject(i);

                Circuit circuitInfo = getCircuitInfo(aCircuit);

                result.add(circuitInfo);
            }

            return result;
        }

        private Circuit getCircuitInfo(JSONObject aCircuit)
                throws JSONException{
            Circuit circuit;
            JSONObject cir;

            // Obtiene el nombre
            cir = aCircuit.getJSONObject(NAME_TAG);
            String name = cir.getString(CONTENT_TAG);

            // Obtiene la direccion
            JSONArray adir = aCircuit.getJSONArray(ADDRES_TAG);
            cir = adir.getJSONObject(1);
            String address = cir.getString(CONTENT_TAG);

            // Obtiene la descripcion
            cir = aCircuit.getJSONObject(DESCRIPTION_TAG);
            String description = cir.getString(CONTENT_TAG);

            // Obtiene la localizacion
            cir = aCircuit.getJSONObject(LOCATION_TAG);
            String location = cir.getString(CONTENT_TAG);

            // Obtiene la url de la foto
            String image = "";
            if(aCircuit.has(IMAGE_TAG)) {
                cir = aCircuit.getJSONObject(IMAGE_TAG);
                image = cir.getString(CONTENT_TAG);
            }

            if(!image.isEmpty())
                circuit = new Circuit(name, address, description, location, image);
            else {
                circuit = new Circuit(name, address, description, location);
            }

            return circuit;
        }
    }

    protected void populateList(ArrayList<Circuit> circuitList) {
        if (circuitList != null) {
            CircuitsAdapter adapter = new CircuitsAdapter(circuitList);
            mRecyclerView.setAdapter(adapter);
        }
    }

}
