package com.uniovi.informaticamovil.cid;

/**
 * Created by Luis on 14/4/16.
 */
public class Circuit {
    private String mName;
    private String mDirection;
    private String mDescription;
    private String mLocation;
    private String mImage;

    public Circuit(){}

    public Circuit(String name, String direccion, String description, String location){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = location;
        mImage = "";
    }

    public Circuit(String name, String direccion, String description, String location, String image){
        mName = name;
        mDirection = direccion;
        mDescription = description;
        mLocation = location;
        mImage = image;
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
}
