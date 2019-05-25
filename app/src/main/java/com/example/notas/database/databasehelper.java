package com.example.notas.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notas.database.model.Nota;

import java.security.PrivateKey;

public class databasehelper extends SQLiteOpenHelper {
    private  static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME= "notes_db";

    public databasehelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Nota.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ Nota.TABLE_NAME);

        onCreate(db);
    }
}
