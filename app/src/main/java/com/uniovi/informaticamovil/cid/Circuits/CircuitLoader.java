package com.uniovi.informaticamovil.cid.Circuits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.uniovi.informaticamovil.cid.DeviceDimensionsHelper;

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
 * Created by Luis on 4/5/16.
 */
public class CircuitLoader extends AsyncTaskLoader<ArrayList<Circuit>> {
    private static final String CIRCUITS_TAG = "directorios";
    private static final String CIRCUIT_TAG = "directorio";
    private static final String CONTENT_TAG = "content";
    private static final String NAME_TAG = "nombre";
    private static final String DESCRIPTION_TAG = "descripcion";
    private static final String ADDRES_TAG = "direccion";
    private static final String LOCATION_TAG = "localizacion";
    private static final String IMAGE_TAG = "foto";
    private String mURL;

    public CircuitLoader(Context context, String URL){
        super(context);
        mURL = URL;
    }

    @Override
    public ArrayList<Circuit> loadInBackground(){
        try {
            return downloadUrl(mURL);
        } catch (IOException e) {
            // TODO: las cadenas deberían ser recursos
            return null;
        }
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

    private ArrayList<Circuit> downloadUrl(String myUrl) throws IOException {
        InputStream is = null;

        try {
            is = openHttpInputStream(myUrl);

            return parseJsonCircuitsFile(streamToString(is));
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

    // Parsea de manera adecuada el json descargado
    private ArrayList<Circuit> parseJsonCircuitsFile(String jsonCircuitsInformation)
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

        if(!image.isEmpty()) {
            byte[] bimage = null;
            try{
                // Transforma la imagen a un array de bytes
                bimage = DeviceDimensionsHelper.getBytes(getBitmapFromURL(image));

            }catch(IOException e){
                e.printStackTrace();
            }
            circuit = new Circuit(name, address, description, location, image, bimage);
        }
        else
            circuit = new Circuit(name, address, description, location);


        return circuit;
    }

    /* Descarga una imagen de una url */
    public Bitmap getBitmapFromURL(String URL) throws  IOException{
        InputStream is = null;
        // Evita que android bloquee las url de las imagenes, es necesario
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            is = openHttpInputStream(URL);

            return BitmapFactory.decodeStream(is);
        }catch(IOException e){
            return null;
        } finally{
            if (is != null){
                is.close();
            }
        }
    }
}
