package com.uniovi.informaticamovil.cid;

import android.provider.BaseColumns;

/**
 * Created by Luis on 19/4/16.
 */
public final class CIDContract {

    public CIDContract(){}

    public static abstract class Circuit implements BaseColumns {
        public static final String TABLE_NAME = "circuit";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_IMAGE = "image";

    }


}
