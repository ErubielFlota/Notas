package com.example.notas.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notas.database.model.Nota;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

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
public long insertNote(String note){
        SQLiteDatabase db=this.getWritableDatabase();
    ContentValues values= new ContentValues();

    values.put(Nota.COLUMN_NOTE,note);
    long id= db.insert(Nota.TABLE_NAME,null,values);
    db.close();
    return id;
    }


    public Nota getNota(long id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor= db.query(Nota.TABLE_NAME,new String[]{Nota.COLUMN_ID,Nota.COLUMN_ID,Nota.COLUMN_TIMESTAMP},Nota.COLUMN_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor !=null)
            cursor.moveToFirst();
            Nota note= new Nota(
                    cursor.getInt(cursor.getColumnIndex(Nota.COLUMN_ID)),cursor.getString(cursor.getColumnIndex(Nota.COLUMN_NOTE)),cursor.getString(cursor.getColumnIndex(Nota.COLUMN_TIMESTAMP)));

        cursor.close();
        return note;
    }
    public List<Nota> getAllNotes(){
        List<Nota> notes=new ArrayList<>();

        String selectQuery= "SELECT * FROM "+ Nota.TABLE_NAME+ " ORDER BY "+ Nota.COLUMN_TIMESTAMP +" DESC";
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor= db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do {
                Nota note= new Nota();
                note.setId(cursor.getInt(cursor.getColumnIndex(Nota.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Nota.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Nota.COLUMN_TIMESTAMP)));

                notes.add(note);
            }while (cursor.moveToNext());
        }
        db.close();
        return notes;

    }
    public int getNotesCount(){
        String countQuery= "SELECT * FROM "+ Nota.TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor= db.rawQuery(countQuery, null);

        int count= cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateNote(Nota nota){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Nota.COLUMN_NOTE,nota.getNote());

        return db.update(Nota.TABLE_NAME,values, Nota.COLUMN_ID+" = ?",new String[]{String.valueOf(nota.getId())});

    }
    public void deleteNote(Nota nota){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Nota.TABLE_NAME,Nota.COLUMN_ID+" =?",new String[]{String.valueOf(nota.getId())});
        db.close();

    }



}
