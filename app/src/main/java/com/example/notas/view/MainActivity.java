package com.example.notas.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notas.NotesAdapter;
import com.example.notas.R;
import com.example.notas.database.Databasehelper;
import com.example.notas.database.model.Note;
import com.example.notas.utils.MyDividerItemDecoration;
import com.example.notas.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter mAdapter;
    private List<Note> noteList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private Databasehelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new Databasehelper(this);
        noteList.addAll(db.getAllNotes());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        mAdapter = new NotesAdapter(this, noteList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        toggleEmptyNotes();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    private void createNote(String note){
        //Insertando nota en db y consiguiendo ID de nota recién insertada
        long id = db.insertNote(note);

        //Obtener la nota recién insertada de db
        Note n = db.getNote(id);
        if (n != null){
            //Añadiendo una nueva nota a la lista de matrices en la posición 0
            noteList.add(0, n);

            //Actualizando la lista
            mAdapter.notifyDataSetChanged();
            toggleEmptyNotes();
        }
    }

    //Actualización de nota en db y actualización item en la lista por su posición
    private void updateNote(String note, int position){
        Note n = noteList.get(position);
        //Actualizando texto de nota
        n.setNote(note);
        //Actualizando nota en db
        db.updateNote(n);

        //Refrescando la lista
        noteList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    //Eliminando nota de SQLite y eliminando el item de la lista por su posición
    private void deleteNote(int position){
        //Borrando la nota de db
        db.deleteNote(noteList.get(position));
        //Borrando nota de la lista
        noteList.remove(position);

        mAdapter.notifyItemChanged(position);
        toggleEmptyNotes();
    }

    private void showActionsDialog(final int position){
        CharSequence colors[] = new CharSequence[]{"Editar", "Eliminar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione opción");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    showNoteDialog(true, noteList.get(position), position);
                }else{
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog,  null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null){
            inputNote.setText(note.getNote());
        }

        alertDialogBuilderUserInput.setCancelable(false)
                .setPositiveButton(shouldUpdate ? "Actualizar" : "Grabar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogBox, int id) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Mostrar mensaje cuando no se ingresa texto
                if (TextUtils.isEmpty(inputNote.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter nota!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    alertDialog.dismiss();
                }

                //Comprobar si el usuario está actualizando nota
                if (shouldUpdate && note != null){
                    //Actualizar nota por su id
                    updateNote(inputNote.getText().toString(), position);
                }else{
                    //Crear nueva nota
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }

    private void toggleEmptyNotes(){
        //Puedes revisar noteList.size() > 0

        if (db.getNotesCount() > 0){
            noNotesView.setVisibility(View.GONE);
        }else{
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
