package com.uniovi.informaticamovil.cid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Luis on 26/4/16.
 */
public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilitieViewHolder>{
    ArrayList<Facilitie> mFacilities;

    public FacilitiesAdapter(ArrayList<Facilitie> facilities) {
        mFacilities = facilities;
    }

    public static class FacilitieViewHolder extends RecyclerView.ViewHolder {
        TextView facilitieName;
        ImageView facilitieView;
        View mItemView;

        FacilitieViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            facilitieName = (TextView) itemView.findViewById(R.id.facilitieName);
            facilitieView = (ImageView) itemView.findViewById(R.id.facilitieView);
        }
    }

    @Override
    public int getItemCount() {
        return mFacilities.size();
    }

    @Override
    public FacilitieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.facilitie_item, viewGroup, false);
        FacilitieViewHolder cvh = new FacilitieViewHolder(convertView);

        return cvh;
    }

    @Override
    public void onBindViewHolder(FacilitieViewHolder facilitieViewHolder, int i) {
        facilitieViewHolder.facilitieName.setText(mFacilities.get(i).getName());

        try {
            facilitieViewHolder.facilitieView.setImageBitmap(
                    getBitmapFromURL(mFacilities.get(i).getImage(), facilitieViewHolder.mItemView));
        } catch (IOException e) {
            e.printStackTrace();
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
}

