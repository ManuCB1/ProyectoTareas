package com.example.proyectotareas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements TaskCompleted {

    private TextView textEmail, textContrasenia;
    private Button btnConfirmar;
    private TareaBBDD logIn;
    private String enlace = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConfirmar = findViewById(R.id.btnConfirmar);
        textEmail = findViewById(R.id.textEmail);
        textContrasenia = findViewById(R.id.textContrasenia);

        btnConfirmar.setOnClickListener(view -> {
            login();
        });
    }

    private void login() {
        String email = textEmail.getText().toString();
        String contrasenia = textContrasenia.getText().toString();
        if (!email.isEmpty() && !contrasenia.isEmpty()){
            try {
                String datos = "email="+ URLEncoder.encode(email, "UTF-8")+
                        "&"+"contrasenia="+ URLEncoder.encode(contrasenia, "UTF-8");
                logIn = new TareaBBDD(this);
                logIn.execute(enlace, datos);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskCompleted(String s) {
        if (s!=null) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("true")){
                        JSONObject usuario = jsonObject.getJSONObject("usuario");
                        int id = usuario.getInt("id");
                        String nombre = usuario.getString("nombre");
                        String rol = usuario.getString("rol");
                        Intent cambiarPagina = new Intent(getApplicationContext(), MainTareas.class);
                        cambiarPagina.putExtra("id", id);
                        cambiarPagina.putExtra("rol", rol);
                        startActivity(cambiarPagina);
                        finish();
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        };
    }
}