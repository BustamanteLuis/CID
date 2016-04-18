package com.uniovi.informaticamovil.cid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Luis on 13/4/16.
 */
public class FacilitieFragment extends Fragment {
    public static FacilitieFragment newInstance(){
        FacilitieFragment fragment = new FacilitieFragment();
        return fragment;
    }

    public FacilitieFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facilities, container, false);

        return view;
    }
}
