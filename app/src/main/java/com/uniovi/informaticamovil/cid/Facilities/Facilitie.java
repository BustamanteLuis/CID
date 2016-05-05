package com.uniovi.informaticamovil.cid.Facilities;

import android.support.v4.util.Pair;

/**
 * Created by Luis on 26/4/16.
 */
public class Facilitie {
    private String mName;
    private String mDirection;
    private String mDescription;
    private String mLocation;
    private String mImage;
    private String mHorario;
    private byte[] mBImage;

    public Facilitie(){}


    public Facilitie(String name, String direccion, String description, String location, String image,
                      String horario, byte[] BImage){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = location;
        mImage = image;
        mHorario = horario;
        mBImage = BImage;
    }

    public Facilitie(String name, String direccion, String description, String image,
                     String horario, byte[] BImage){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = "";
        mImage = image;
        mHorario = horario;
        mBImage = BImage;
    }

    public String getDirection() {
        return mDirection;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getImage() {
        return mImage;
    }

    public byte[] getBImage(){ return mBImage; }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setDirection(String mDireccion) {
        this.mDirection = mDireccion;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public void setBImage(byte[] mBImage){
        this.mBImage = mBImage;
    }

    public Pair<Double,Double> getParsedLocation(){
        String[] aux = mLocation.split(" ");

        Pair<Double,Double> LatLong = new Pair<>(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));

        return LatLong;

    }

    public String getHorario() {
        return mHorario;
    }

    public void setHorario(String mHorario) {
        this.mHorario = mHorario;
    }
}
