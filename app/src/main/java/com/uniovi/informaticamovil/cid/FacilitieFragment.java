package com.uniovi.informaticamovil.cid;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class FacilitieFragment extends Fragment {
    private static final String URL = "http://datos.gijon.es/doc/deporte/complejos-deportivos.json";

    private View view;
    private RecyclerView mRecyclerView;
    private CIDbHelper mCIDb;
    private SharedPreferences mSettings;

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

    private class DownloadJSONTask extends AsyncTask<String, Void, ArrayList<Facilitie>> {
        private static final String FACILITIES_TAG = "directorios";
        private static final String FACILITIE_TAG = "directorio";
        private static final String CONTENT_TAG = "content";
        private static final String NAME_TAG = "nombre";
        private static final String DESCRIPTION_TAG = "descripcion";
        private static final String ADDRES_TAG = "direccion";
        private static final String LOCATION_TAG = "localizacion";
        private static final String IMAGE_TAG = "foto";
        private static final String HORARIO_TAG = "horario";

        @Override
        protected ArrayList<Facilitie> doInBackground(String... urls) {
            // urls vienen de la llamada a execute(): urls[0] es la url
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                // TODO: las cadenas deberían ser recursos
                return null;
            }
        }

        private ArrayList<Facilitie> downloadUrl(String myUrl) throws IOException {
            InputStream is = null;

            try {
                is = openHttpInputStream(myUrl);

                return parseJsonFacilitiesFile(streamToString(is));
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
        protected void onPostExecute(ArrayList<Facilitie> result) {
            mCIDb.insertarFacilities(result);
            populateList(result);
            mSettings.edit().putBoolean("isInstalled", true).commit();
        }

        private ArrayList<Facilitie> parseJsonFacilitiesFile(String jsonFacilitiesInformation)
                throws JSONException {
            ArrayList<Facilitie> result = new ArrayList<Facilitie>();

            JSONObject root = new JSONObject(jsonFacilitiesInformation);
            JSONObject facilities = root.getJSONObject(FACILITIES_TAG);
            JSONArray facilitieArray = facilities.getJSONArray(FACILITIE_TAG);

            for (int i = 0; i < facilitieArray.length(); i++) {
                JSONObject aFacilitie = facilitieArray.getJSONObject(i);

                Facilitie facilitieInfo = getFacilitieInfo(aFacilitie);

                result.add(facilitieInfo);
            }

            return result;
        }

        private Facilitie getFacilitieInfo(JSONObject aFacilitie)
                throws JSONException{
            Facilitie facilitie;
            JSONObject fac;

            // Obtiene el nombre
            fac = aFacilitie.getJSONObject(NAME_TAG);
            String name = fac.getString(CONTENT_TAG);

            // Obtiene la direccion
            JSONArray adir = aFacilitie.getJSONArray(ADDRES_TAG);
            fac = adir.getJSONObject(1);
            String address = fac.getString(CONTENT_TAG);

            // Obtiene la descripcion
            fac = aFacilitie.getJSONObject(DESCRIPTION_TAG);
            String description = fac.getString(CONTENT_TAG);

            // Obtiene la localizacion
            fac = aFacilitie.getJSONObject(LOCATION_TAG);
            String location = "";
            if(fac.length() > 1)
                location = fac.getString(CONTENT_TAG);


            // Obtiene la url de la foto
            fac = aFacilitie.getJSONObject(IMAGE_TAG);
            String image = fac.getString(CONTENT_TAG);

            String horario = aFacilitie.getString(HORARIO_TAG);

            if(!location.isEmpty())
                facilitie = new Facilitie(name, address, description, location, image, horario);
            else
                facilitie = new Facilitie(name, address, description, image, horario);

            return facilitie;
        }
    }

    protected void populateList(ArrayList<Facilitie> facilitieList) {
        if (facilitieList != null) {
            FacilitiesAdapter adapter = new FacilitiesAdapter(facilitieList);
            mRecyclerView.setAdapter(adapter);
        }
    }

}
