package com.uniovi.informaticamovil.cid.Circuits;

import android.support.v4.util.Pair;


public class Circuit {
    private String mName;
    private String mDirection;
    private String mDescription;
    private String mLocation;
    private String mImage;
    private byte[] mBImage;

    public Circuit(){}

    public Circuit(String name, String direccion, String description, String location){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = location;
        mImage = "";
        mBImage = null;
    }

    public Circuit(String name, String direccion, String description, String location, String image, byte[] BImage){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = location;
        mImage = image;
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

    // Devuele una coordenada como un par
    public Pair<Double,Double> getParsedLocation(){
        // Separa la latitud y longitud
        String[] aux = mLocation.split(" ");

        Pair<Double,Double> LatLong = new Pair<>(Double.parseDouble(aux[0]), Double.parseDouble(aux[1]));

        return LatLong;

    }
}
