package com.mokdoryeong.team7.mokdoryeong;

import android.provider.BaseColumns;

public final class Databases {

    public static final class CreateDB implements BaseColumns {
        public static final String _TABLENAME = "CervicalDataList";
        public static final String ID = "id";
        public static final String STARTTIME = "starttime";
        public static final String FINISHTIME = "finishtime";
        public static final String AVERAGEANGLE = "averageangle";
        public static final String CERVICALRISKINDEX = "cervicalriskindex";
        public static final String _CREATE =
                "create table " + _TABLENAME + " ( "
                        + ID + " integer primary key autoincrement, "
                        + STARTTIME + " integer not null, "
                        + FINISHTIME + " integer not null, "
                        + AVERAGEANGLE + " real not null, "
                        + CERVICALRISKINDEX + " real not null);";

    }

}
