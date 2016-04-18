package com.uniovi.informaticamovil.cid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Luis on 16/4/16.
 */
public class CircuitsAdapter extends ArrayAdapter<Circuit> {
    private Context mContext;

    public CircuitsAdapter(Context context, ArrayList<Circuit> circuits) {
        super(context, R.layout.circuit_item, circuits);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Circuit circuit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.circuit_item, parent, false);
        }
        // Lookup view for data population
        TextView circuitName = (TextView) convertView.findViewById(R.id.circuitName);
        TextView circuitDirection = (TextView) convertView.findViewById(R.id.circuitDirection);
        TextView circuitDescription = (TextView) convertView.findViewById(R.id.circuitDescription);
        // Populate the data into the template view using the data object
        circuitName.setText(circuit.getName());
        circuitDirection.setText(circuit.getDirection());
        circuitDescription.setText(circuit.getDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}
