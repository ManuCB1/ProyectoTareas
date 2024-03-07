package com.example.proyectotareas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectotareas.R;
import com.example.proyectotareas.bd.TareaBBDD;
import com.example.proyectotareas.bd.TaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainEditar extends AppCompatActivity implements TaskCompleted {

    private TextView textNombreEditar, textContenidoEditar;
    private Button btnModificar;
    private TareaBBDD editarTarea;
    private String enlace = "modificar_tarea";
    private int id_tarea;
    private int id_usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_editar);

        Intent recogerdatos = getIntent();
        id_tarea = recogerdatos.getIntExtra("id_tarea", -1);
        id_usuario = recogerdatos.getIntExtra("id_usuario", -1);
        String nombre = recogerdatos.getStringExtra("nombre");
        String contenido = recogerdatos.getStringExtra("contenido");

        textNombreEditar = findViewById(R.id.textNombreEditar);
        textContenidoEditar = findViewById(R.id.textContenidoEditar);
        btnModificar = findViewById(R.id.btnModificar);
        textNombreEditar.setText(nombre);
        textContenidoEditar.setText(contenido);

        btnModificar.setOnClickListener(view -> {
            editarTarea();
        });
    }

    private void editarTarea() {
        editarTarea = new TareaBBDD(this);
        try {
            String nombreModificado = textNombreEditar.getText().toString();
            String contenidoModificado = textContenidoEditar.getText().toString();
            String datos = "id_tarea="+ URLEncoder.encode(String.valueOf(id_tarea), "UTF-8")+
                    "&"+"id_usuario="+ URLEncoder.encode(String.valueOf(id_usuario), "UTF-8")+
                    "&"+"nombre="+ URLEncoder.encode(nombreModificado, "UTF-8")+
                    "&"+"contenido="+ URLEncoder.encode(contenidoModificado, "UTF-8")+
                    "&"+"fecha="+ URLEncoder.encode(getFechaFormateada(), "UTF-8");
            editarTarea.execute(enlace, datos);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFechaFormateada() {
        long timestamp = System.currentTimeMillis();
        String fechaString = String.valueOf(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaFormateada = sdf.format(new Date(timestamp));
        return fechaFormateada;
    }

    @Override
    public void onTaskCompleted(String s) {
        if (s!=null){
            try {
                JSONObject jsonObject = new JSONObject(s);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}