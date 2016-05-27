package com.uniovi.informaticamovil.cid.Facilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

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

public class FacilitieLoader extends AsyncTaskLoader<ArrayList<Facilitie>>{
    private static final String FACILITIES_TAG = "directorios";
    private static final String FACILITIE_TAG = "directorio";
    private static final String CONTENT_TAG = "content";
    private static final String NAME_TAG = "nombre";
    private static final String DESCRIPTION_TAG = "descripcion";
    private static final String ADDRES_TAG = "direccion";
    private static final String LOCATION_TAG = "localizacion";
    private static final String IMAGE_TAG = "foto";
    private static final String HORARIO_TAG = "horario";
    private String mURL;

    public FacilitieLoader(Context context, String URL){
        super(context);
        mURL = URL;
    }

    @Override
    public ArrayList<Facilitie> loadInBackground() {
        try {
            return downloadUrl(mURL);
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


        // Obtiene la url y la foto
        fac = aFacilitie.getJSONObject(IMAGE_TAG);
        String image = fac.getString(CONTENT_TAG);

        byte[] bimage = null;
        try{
            bimage = DeviceDimensionsHelper.getBytes(getBitmapFromURL(image));

        }catch(IOException e){
            e.printStackTrace();
        }

        String horario = aFacilitie.getString(HORARIO_TAG);

        if(!location.isEmpty())
            facilitie = new Facilitie(name, address, description, location, image, horario, bimage);
        else
            facilitie = new Facilitie(name, address, description, image, horario, bimage);

        return facilitie;
    }

    /* Descarga una imagen de una url */
    public Bitmap getBitmapFromURL(String URL) throws  IOException{
        InputStream is = null;
        // Evita que android bloquee las url de las imagenes
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
