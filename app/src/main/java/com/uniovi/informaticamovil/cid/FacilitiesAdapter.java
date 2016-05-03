package com.uniovi.informaticamovil.cid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Luis on 26/4/16.
 */
public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilitieViewHolder> implements Filterable{
    ArrayList<Facilitie> mFacilities;
    ArrayList<Facilitie> Original;
    static byte[] mBImage;
    static String mName;
    static String mDirection;
    static String mHorario;
    static String mDescription;


    public FacilitiesAdapter(ArrayList<Facilitie> facilities) {
        mFacilities = facilities;
    }

    public static class FacilitieViewHolder extends RecyclerView.ViewHolder {
        TextView facilitieName;
        ImageView facilitieView;
        View mItemView;

        FacilitieViewHolder(final View itemView) {
            super(itemView);
            mItemView = itemView;
            facilitieName = (TextView) itemView.findViewById(R.id.facilitieName);
            facilitieView = (ImageView) itemView.findViewById(R.id.facilitieView);

            // Click event to start a new activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(mItemView.getContext(), FacilitieActivity.class);
                    intent.putExtra("image", mBImage);
                    intent.putExtra("name", mName);
                    intent.putExtra("direccion", mDirection);
                    intent.putExtra("horario", mHorario);
                    intent.putExtra("descripcion", mDescription);
                    mItemView.getContext().startActivity(intent);
                }
            });
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
        mName = mFacilities.get(i).getName();
        mDirection = mFacilities.get(i).getDirection();
        mHorario = mFacilities.get(i).getHorario();
        mDescription = mFacilities.get(i).getDescription();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            Bitmap image = getBitmapFromURL(mFacilities.get(i).getImage(), facilitieViewHolder.mItemView);
            facilitieViewHolder.facilitieView.setImageBitmap(image);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            mBImage = stream.toByteArray();
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFacilities=(ArrayList<Facilitie>)results.values;
                notifyDataSetChanged();
                Log.e("Filter", "publish");
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Facilitie> filteredList= new ArrayList<Facilitie>();

                if(Original == null)
                    Original = mFacilities;

                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = mFacilities;
                    results.count = mFacilities.size();
                }
                else {
                    for (int i = 0; i < Original.size(); i++) {
                        Facilitie data = Original.get(i);
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

