package com.uniovi.informaticamovil.cid.Circuits;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.uniovi.informaticamovil.cid.DeviceDimensionsHelper;
import com.uniovi.informaticamovil.cid.R;

import java.util.ArrayList;


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
            // Get width of screen at runtime
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(circuitViewHolder.mItemView.getContext());
            // [] byte to Bitmap
            Bitmap imagen = DeviceDimensionsHelper.getImage(mCircuits.get(i).getBImage());
            // Sets the image
            circuitViewHolder.circuitView.setImageBitmap(
                    Bitmap.createScaledBitmap(imagen, screenWidth, 350, true));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /* Retorna un filtro de circuitos segun el criterio de búsqueda que el
     * usuario halla introducido */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mCircuits=(ArrayList<Circuit>)results.values;
                notifyDataSetChanged();
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

                return results;
            }
        };
        return filter;
    }
}
