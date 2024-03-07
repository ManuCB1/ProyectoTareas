package com.example.proyectotareas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectotareas.R;
import com.example.proyectotareas.bd.DeleteTarea;
import com.example.proyectotareas.bd.DeleteTaskCompleted;
import com.example.proyectotareas.bd.TareaBBDD;
import com.example.proyectotareas.bd.TaskCompleted;
import com.example.proyectotareas.model.Tarea;
import com.example.proyectotareas.recycler.MyRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainTareas extends AppCompatActivity implements TaskCompleted, DeleteTaskCompleted {

    private TextView textFecha;
    private Button btnCrear;
    private RecyclerView recycler;
    private MyRecyclerViewAdapter adapter;
    private List<Tarea> tareas = new ArrayList<>();
    private TareaBBDD getTareas;
    private DeleteTarea deleteTarea;
    private Intent cambiarPagina;
    private String enlace = "get_tareas";
    private int idUsuario;
    private String rolUsuario;
    private String read = "READ";
    private String write = "WRITE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tareas);

        recycler = findViewById(R.id.recycler);
        textFecha = findViewById(R.id.textFecha);
        btnCrear = findViewById(R.id.btnCrear);

        getData();

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, tareas, R.layout.recycler_view_item_tareas);
        recycler.setAdapter(adapter);
        registerForContextMenu(recycler);

        btnCrear.setOnClickListener(view -> {
            cambiarPagina = new Intent(this, MainCrear.class);
            startActivity(cambiarPagina);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        iniciarRecycler();
    }

    private void getData(){
        Intent recogerDatos = getIntent();
        idUsuario = recogerDatos.getIntExtra("id", -1);
        rolUsuario = recogerDatos.getStringExtra("rol");

        if (rolUsuario.equals(read) || rolUsuario.equals(write)){
            btnCrear.setEnabled(false);
            btnCrear.setVisibility(View.INVISIBLE);
        }
    }
    private void iniciarRecycler() {
        long timestamp = System.currentTimeMillis();
        String fechaString = String.valueOf(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = sdf.format(new Date(timestamp));

        textFecha.setText(getFechaOrdenada(fechaActual));
        mostrarTareas(fechaActual);
    }

    @NonNull
    private void mostrarTareas(String fecha) {
        try {
            String datos = "fecha="+ URLEncoder.encode(fecha, "UTF-8");
            getTareas = new TareaBBDD(this);
            getTareas.execute(enlace, datos);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (rolUsuario.equals(read)){
            Toast.makeText(getApplicationContext(), "No tienes permisos", Toast.LENGTH_SHORT).show();
            return false;
        }
        int position = adapter.getPosition();
        Tarea tareaSeleccionada = adapter.getItem(position);
        int comparar = item.getItemId();
        if (comparar == R.id.itemEditar){
            cambiarPagina = new Intent(getApplicationContext(), MainEditar.class);
            cambiarPagina.putExtra("id_tarea", tareaSeleccionada.getId());
            cambiarPagina.putExtra("id_usuario", idUsuario);
            cambiarPagina.putExtra("nombre", tareaSeleccionada.getNombre());
            cambiarPagina.putExtra("contenido", tareaSeleccionada.getContenido());
            startActivity(cambiarPagina);
            return true;
        }
        if (comparar == R.id.itemHistorial){
            cambiarPagina = new Intent(getApplicationContext(), MainHistorial.class);
            cambiarPagina.putExtra("id_tarea", tareaSeleccionada.getId());

            startActivity(cambiarPagina);
            return true;
        }
        if (comparar == R.id.itemBorrar){
            if (tareaSeleccionada!= null) deleteDialog(tareaSeleccionada);
            return true;
        }
        return false;
    }

    private void deleteDialog(Tarea tareaSeleccionada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Borrar tarea")
                .setMessage("Está seguro/a de borrar la tarea "+ tareaSeleccionada.getNombre() + "?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTarea = new DeleteTarea(MainTareas.this);
                        try {
                            String datos = "id_tarea="+ URLEncoder.encode(String.valueOf(tareaSeleccionada.getId()), "UTF-8");
                            deleteTarea.execute(datos);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public void mostrarCalendario(View view){
            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    calendario.set(Calendar.YEAR, year);
                    calendario.set(Calendar.MONTH, month);
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaFormateada = sdf.format(calendario.getTime());
                    textFecha.setText(getFechaOrdenada(fechaFormateada));
                    mostrarTareas(fechaFormateada);

                }
            }, año, mes, dia);
            dialog.show();
    }
    private String getFechaOrdenada(String fecha){
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaOrdenada = "";
        try {
            Date fechaDate = formatoEntrada.parse(fecha);
            fechaOrdenada = formatoSalida.format(fechaDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaOrdenada;
    }
    @Override
    public void onTaskCompleted(String s) {
        if (s!=null){
            try {
                tareas.clear();
                JSONObject jsonObject = new JSONObject(s);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                if (success.equals("true")){
                    JSONArray tareasArray = jsonObject.getJSONArray("tareas");
                    for (int i = 0; i < tareasArray.length(); i++) {
                        JSONObject tareaObject = tareasArray.getJSONObject(i);
                        int id = tareaObject.getInt("id");
                        String nombre = tareaObject.getString("nombre");
                        String contenido = tareaObject.getString("contenido");
                        String fecha = tareaObject.getString("fecha");
                        tareas.add(new Tarea(id, nombre, contenido));
                    }
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDeleteTaskCompleted(String s) {
        if (s!=null){
            try {
                JSONObject jsonObject = new JSONObject(s);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                if (success.equals("true")){
                    int position = adapter.getPosition();
                    tareas.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}