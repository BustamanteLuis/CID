package com.uniovi.informaticamovil.cid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Luis on 19/4/16.
 */
public class CIDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CID.db";

    private static final String SQL_CREATE_CIRCUITS = "CREATE TABLE " + CIDContract.Circuit.TABLE_NAME + " ("
            + CIDContract.Circuit._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            "," +
            CIDContract.Circuit.COLUMN_NAME_NAME + " TEXT NOT NULL," +
            CIDContract.Circuit.COLUMN_NAME_DIRECTION + " TEXT NOT NULL," +
            CIDContract.Circuit.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
            CIDContract.Circuit.COLUMN_NAME_LOCATION+ " TEXT NOT NULL," +
            CIDContract.Circuit.COLUMN_NAME_IMAGE + " TEXT" + ")";

    private static final String SQL_CREATE_FACILITIES = "CREATE TABLE " + CIDContract.Facilitie.TABLE_NAME + " ("
            + CIDContract.Facilitie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
            "," +
            CIDContract.Facilitie.COLUMN_NAME_NAME + " TEXT NOT NULL," +
            CIDContract.Facilitie.COLUMN_NAME_DIRECTION + " TEXT NOT NULL," +
            CIDContract.Facilitie.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
            CIDContract.Facilitie.COLUMN_NAME_LOCATION+ " TEXT," +
            CIDContract.Facilitie.COLUMN_NAME_IMAGE + " TEXT NOT NULL," +
            CIDContract.Facilitie.COLUMN_NAME_HORARIO + " TEXT NOT NULL" + ")";

    private static final String SQL_DELETE_CIRCUITS = "DROP TABLE IF EXISTS " + CIDContract.Circuit.TABLE_NAME;
    private static final String SQL_DELETE_FACILITIES= "DROP TABLE IF EXISTS " + CIDContract.Facilitie.TABLE_NAME;

    public CIDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CIRCUITS);
        db.execSQL(SQL_CREATE_FACILITIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CIRCUITS);
        db.execSQL(SQL_DELETE_FACILITIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    protected void insertarCircuitos(ArrayList<Circuit> circuitList){

        SQLiteDatabase dbW = getWritableDatabase();

        for(Circuit circuit : circuitList){
            ContentValues values = new ContentValues();
            values.put(CIDContract.Circuit.COLUMN_NAME_NAME, circuit.getName());
            values.put(CIDContract.Circuit.COLUMN_NAME_DIRECTION, circuit.getDirection());
            values.put(CIDContract.Circuit.COLUMN_NAME_DESCRIPTION, circuit.getDescription());
            values.put(CIDContract.Circuit.COLUMN_NAME_LOCATION, circuit.getLocation());
            values.put(CIDContract.Circuit.COLUMN_NAME_IMAGE, circuit.getImage());

            dbW.insert(CIDContract.Circuit.TABLE_NAME, null, values);
        }
    }

    protected void insertarFacilities(ArrayList<Facilitie> facilitieList){

        SQLiteDatabase dbW = getWritableDatabase();

        for(Facilitie facilitie : facilitieList){
            ContentValues values = new ContentValues();
            values.put(CIDContract.Facilitie.COLUMN_NAME_NAME, facilitie.getName());
            values.put(CIDContract.Facilitie.COLUMN_NAME_DIRECTION, facilitie.getDirection());
            values.put(CIDContract.Facilitie.COLUMN_NAME_DESCRIPTION, facilitie.getDescription());
            values.put(CIDContract.Facilitie.COLUMN_NAME_LOCATION, facilitie.getLocation());
            values.put(CIDContract.Facilitie.COLUMN_NAME_IMAGE, facilitie.getImage());
            values.put(CIDContract.Facilitie.COLUMN_NAME_HORARIO, facilitie.getHorario());

            dbW.insert(CIDContract.Facilitie.TABLE_NAME, null, values);
        }
    }

    protected ArrayList<Circuit> leerCircuitos(){
        SQLiteDatabase dbR = getReadableDatabase();
        ArrayList<Circuit> circuits = new ArrayList<Circuit>();

        String[] colums = {CIDContract.Circuit._ID, CIDContract.Circuit.COLUMN_NAME_NAME, CIDContract.Circuit.COLUMN_NAME_DIRECTION,
                CIDContract.Circuit.COLUMN_NAME_DESCRIPTION, CIDContract.Circuit.COLUMN_NAME_LOCATION,
                CIDContract.Circuit.COLUMN_NAME_IMAGE};

        Cursor c = dbR.query(CIDContract.Circuit.TABLE_NAME, colums,
                null, null, null, null, null, null);

        c.moveToFirst();
        do {
            Circuit circuit;
            if(!c.getString(5).isEmpty())
                circuit = new Circuit(c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5));
            else
                circuit = new Circuit(c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4));

            circuits.add(circuit);
        } while (c.moveToNext());

        dbR.close();
        c.close();
        return circuits;
    }

    protected ArrayList<Facilitie> leerFacilities(){
        SQLiteDatabase dbR = getReadableDatabase();
        ArrayList<Facilitie> facilities = new ArrayList<Facilitie>();

        String[] colums = {CIDContract.Facilitie._ID, CIDContract.Facilitie.COLUMN_NAME_NAME, CIDContract.Facilitie.COLUMN_NAME_DIRECTION,
                CIDContract.Facilitie.COLUMN_NAME_DESCRIPTION, CIDContract.Facilitie.COLUMN_NAME_LOCATION,
                CIDContract.Facilitie.COLUMN_NAME_IMAGE, CIDContract.Facilitie.COLUMN_NAME_HORARIO};

        Cursor c = dbR.query(CIDContract.Facilitie.TABLE_NAME, colums,
                null, null, null, null, null, null);

        c.moveToFirst();
        do {
            Facilitie facilitie;

            if(!c.getString(4).isEmpty())
                facilitie = new Facilitie(c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6));
            else
                facilitie = new Facilitie(c.getString(1), c.getString(2), c.getString(3),
                        c.getString(5), c.getString(6));

            facilities.add(facilitie);
        } while (c.moveToNext());

        dbR.close();
        c.close();
        return facilities;
    }

}
