package com.example.proyectotareas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.proyectotareas.R;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainHistorial extends AppCompatActivity implements TaskCompleted {

    private RecyclerView recycler;
    private MyRecyclerViewAdapter adapter;
    private TareaBBDD getHistorial;
    private String enlace = "get_historial";
    List<Tarea> tareas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_historial);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, tareas, R.layout.recycler_view_item_historial);
        recycler.setAdapter(adapter);
        cargarLista();
    }

    private void cargarLista() {
        Intent recogerDatos = getIntent();
        getHistorial = new TareaBBDD(this, this);
        int idTarea = recogerDatos.getIntExtra("id_tarea", -1);
        try {
            String datos = "id_tarea="+ URLEncoder.encode(String.valueOf(idTarea), "UTF-8");
            getHistorial.execute(enlace, datos);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFechaOrdenada(String fecha){
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String fechaOrdenada = "";
        try {
            Date fechaDate = formatoEntrada.parse(fecha);
            fechaOrdenada = formatoSalida.format(fechaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaOrdenada;
    }

    @Override
    public void onTaskCompleted(String s) {
        if (s!=null) {
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
                        String email = tareaObject.getString("usuario");
                        String contenido = tareaObject.getString("contenido");
                        String fecha = tareaObject.getString("fecha");
                        tareas.add(new Tarea(id, email, contenido, getFechaOrdenada(fecha)));
                    }
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}