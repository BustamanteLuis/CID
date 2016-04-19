package com.uniovi.informaticamovil.cid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Luis on 16/4/16.
 */
public class CircuitsAdapter extends ArrayAdapter<Circuit> {
    private ImageView mCircuitView;

    public CircuitsAdapter(Context context, ArrayList<Circuit> circuits) {
        super(context, R.layout.circuit_item, circuits);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Circuit circuit = getItem(position);
        Log.e("veces", "position:" + position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.circuit_item, parent, false);
        }
        // Lookup view for data population
        TextView circuitName = (TextView) convertView.findViewById(R.id.circuitName);
        TextView circuitDirection = (TextView) convertView.findViewById(R.id.circuitDirection);
        TextView circuitDescription = (TextView) convertView.findViewById(R.id.circuitDescription);
        mCircuitView = (ImageView) convertView.findViewById(R.id.circuitView);
        Log.e("medidas", "" + mCircuitView.getWidth());
        // Populate the data into the template view using the data object
        circuitName.setText(circuit.getName());
        circuitDirection.setText(circuit.getDirection());
        circuitDescription.setText(circuit.getDescription());
        Log.e("veces", "fuera");
        if(!circuit.getImage().isEmpty()) {
            Log.e("veces", "entra para poner la imagen");
            try {
                mCircuitView.setImageBitmap(getBitmapFromURL(circuit.getImage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public Bitmap getBitmapFromURL(String myUrl) throws IOException {
        InputStream is = null;
        // Evita que android bloquee las url de las imagenes
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            conn.getResponseCode();

            is = conn.getInputStream();

            // Get width of screen at runtime
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());

            Bitmap imagen = BitmapFactory.decodeStream(is);

            float factor = screenWidth / (float) imagen.getWidth();

            imagen = Bitmap.createScaledBitmap(imagen, screenWidth, 350, true);

            return imagen;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(is!= null) {
                is.close();
            }
        }

    }
}
