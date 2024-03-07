package com.example.proyectotareas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectotareas.R;
import com.example.proyectotareas.bd.TareaBBDD;
import com.example.proyectotareas.bd.TaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainCrear extends AppCompatActivity implements TaskCompleted {

    private TextView textNombre, textContenido, textFechaCrear;
    private Button btnCrear;
    private TareaBBDD crearTarea;
    private String enlace = "crear_tarea";
    private String fechaFormateada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_crear);

        textNombre = findViewById(R.id.textNombre);
        textContenido = findViewById(R.id.textContenido);
        textFechaCrear = findViewById(R.id.textFechaCrear);
        btnCrear = findViewById(R.id.btnCrearTarea);

        btnCrear.setOnClickListener(view -> {
            crearTarea();
        });
    }

    private void crearTarea() {
        crearTarea = new TareaBBDD(this);
        String nombre = textNombre.getText().toString();
        String contenido = textContenido.getText().toString();
        String fecha = fechaFormateada;
        if (!nombre.isEmpty() && !contenido.isEmpty() && !fecha.isEmpty()){
            try {
                String datos = "nombre="+ URLEncoder.encode(nombre, "UTF-8")+
                        "&"+"contenido="+ URLEncoder.encode(contenido, "UTF-8")+
                        "&"+"fecha="+ URLEncoder.encode(fecha, "UTF-8");
                crearTarea.execute(enlace, datos);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
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
                fechaFormateada = sdf.format(calendario.getTime());
                textFechaCrear.setText(getFechaOrdenada(fechaFormateada));

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