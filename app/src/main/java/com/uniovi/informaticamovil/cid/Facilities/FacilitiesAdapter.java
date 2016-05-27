package com.uniovi.informaticamovil.cid.Facilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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


public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilitieViewHolder> implements Filterable{
    ArrayList<Facilitie> mFacilities;
    ArrayList<Facilitie> Original;

    public FacilitiesAdapter(ArrayList<Facilitie> facilities) {
        mFacilities = facilities;
    }

    public static class FacilitieViewHolder extends RecyclerView.ViewHolder {
        TextView facilitieName;
        ImageView facilitieView;
        View mItemView;
        Facilitie currentFacilitie;

        FacilitieViewHolder(final View itemView) {
            super(itemView);
            mItemView = itemView;
            facilitieName = (TextView) itemView.findViewById(R.id.facilitieName);
            facilitieView = (ImageView) itemView.findViewById(R.id.facilitieView);

            // Click event to start a new activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(mItemView.getContext(), FacilitieActivity.class);
                    intent.putExtra("image", currentFacilitie.getBImage());
                    intent.putExtra("name", currentFacilitie.getName());
                    intent.putExtra("direccion", currentFacilitie.getDirection());
                    intent.putExtra("horario", currentFacilitie.getHorario());
                    intent.putExtra("descripcion", currentFacilitie.getDescription());
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
        facilitieViewHolder.currentFacilitie = mFacilities.get(i);

        // Get width of screen at runtime
        int screenWidth = DeviceDimensionsHelper.getDisplayWidth(facilitieViewHolder.mItemView.getContext());
        // [] byte to Bitmap
        Bitmap imagen = DeviceDimensionsHelper.getImage(mFacilities.get(i).getBImage());
        // Sets the image
        facilitieViewHolder.facilitieView.setImageBitmap(
                Bitmap.createScaledBitmap(imagen, screenWidth, 350, true));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /* Retorna un filtro de instalaciones segun el criterio de búsqueda que el
     * usuario halla introducido */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFacilities=(ArrayList<Facilitie>)results.values;
                notifyDataSetChanged();
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

                return results;
            }
        };
        return filter;
    }

}

