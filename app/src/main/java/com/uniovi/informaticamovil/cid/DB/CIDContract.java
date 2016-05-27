package com.uniovi.informaticamovil.cid.DB;

import android.provider.BaseColumns;


public final class CIDContract {

    public CIDContract(){}

    public static abstract class Circuit implements BaseColumns {
        public static final String TABLE_NAME = "circuit";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_BIMAGE = "bimage";

    }

    public static abstract class Facilitie implements BaseColumns {
        public static final String TABLE_NAME = "facilitie";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_HORARIO = "horario";
        public static final String COLUMN_NAME_BIMAGE = "bimage";

    }


}
