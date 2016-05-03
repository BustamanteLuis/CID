package com.uniovi.informaticamovil.cid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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
public class CircuitsAdapter extends RecyclerView.Adapter<CircuitsAdapter.CircuitViewHolder> implements Filterable{
    ArrayList<Circuit> mCircuits;
    ArrayList<Circuit> Original;

    public CircuitsAdapter(ArrayList<Circuit> circuits) {
        mCircuits = circuits;
    }

    public static class CircuitViewHolder extends RecyclerView.ViewHolder {
        TextView circuitName;
        TextView circuitDirection;
        TextView circuitDescription;
        ImageView circuitView;
        View mItemView;

        CircuitViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            circuitName = (TextView) itemView.findViewById(R.id.circuitName);
            circuitDirection = (TextView) itemView.findViewById(R.id.circuitDirection);
            circuitDescription = (TextView) itemView.findViewById(R.id.circuitDescription);
            circuitView = (ImageView) itemView.findViewById(R.id.circuitView);
        }
    }

    @Override
    public int getItemCount() {
        return mCircuits.size();
    }

    @Override
    public CircuitViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.circuit_item, viewGroup, false);
        CircuitViewHolder cvh = new CircuitViewHolder(convertView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CircuitViewHolder circuitViewHolder, int i) {
        circuitViewHolder.circuitName.setText(Html.fromHtml(mCircuits.get(i).getName()));
        circuitViewHolder.circuitDirection.setText(Html.fromHtml(mCircuits.get(i).getDirection()));
        circuitViewHolder.circuitDescription.setText(Html.fromHtml(mCircuits.get(i).getDescription()));
        if(!mCircuits.get(i).getImage().isEmpty()) {
            try {
                circuitViewHolder.circuitView.setImageBitmap(
                        getBitmapFromURL(mCircuits.get(i).getImage(), circuitViewHolder.mItemView));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public Bitmap getBitmapFromURL(String myUrl, View itemView) throws IOException {
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
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(itemView.getContext());

            Bitmap imagen = BitmapFactory.decodeStream(is);

            // float factor = screenWidth / (float) imagen.getWidth();

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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mCircuits=(ArrayList<Circuit>)results.values;
                notifyDataSetChanged();
                Log.e("Filter", "publish");
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Circuit> filteredList= new ArrayList<Circuit>();

                if(Original == null)
                    Original = mCircuits;

                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = mCircuits;
                    results.count = mCircuits.size();
                }
                else {
                    for (int i = 0; i < Original.size(); i++) {
                        Circuit data = Original.get(i);
                        if (data.getName().toLowerCase().contains(constraint.toString()))  {
                            filteredList.add(data);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                Log.e("Filter", "perform");
                return results;
            }
        };
        return filter;
    }
}
