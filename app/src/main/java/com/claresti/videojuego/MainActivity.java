package com.claresti.videojuego;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private MediaPlayer mp;
    private GestureLibrary libreria;
    private Button bAcercaDe;
    private Button bJugar;
    private Button bPreferencias;
    private Button bSalir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAcercaDe = (Button) findViewById(R.id.button3);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarAcercaDe();
            }
        });

        bJugar = (Button) findViewById(R.id.button1);
        bJugar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarJugar();
            }
        });

        bPreferencias = (Button) findViewById(R.id.button2);
        bPreferencias.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarPreferencias();
            }
        });

        bSalir = (Button) findViewById(R.id.button4);
        bSalir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!libreria.load()) {
            finish();
        }
        GestureOverlayView gesturesView = (GestureOverlayView) findViewById(R.id.gestures);
        gesturesView.addOnGesturePerformedListener(this);
        mp = MediaPlayer.create(this, R.raw.audio);
    }
    @Override protected void onResume(){
        super.onResume();
        mp.start();
    }

    @Override protected void onPause(){
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado){
        super.onRestoreInstanceState(estadoGuardado);
        if (estadoGuardado != null && mp != null) {
            int pos = estadoGuardado.getInt("posicion");
            mp.seekTo(pos);
        }
    }

    public void lanzarAcercaDe() {
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    public void lanzarJugar() {
        Intent i = new Intent(this, juego.class);
        startActivity(i);
    }

    public void lanzarPreferencias() {
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acercaDe:
                lanzarAcercaDe();
                break;
            case R.id.confg:
                lanzarPreferencias();
                break;
        }
        return false;
    }

    public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {
        ArrayList<Prediction> predictions = libreria.recognize(gesture);
        if (predictions.size() > 0) {
            String comando = predictions.get(0).name;
            if (comando.equals("play")) {
                lanzarJugar();
            } else if (comando.equals("configurar")) {
                lanzarPreferencias();
            } else if (comando.equals("acerca_de")) {
                lanzarAcercaDe();
            } else if (comando.equals("cancelar")) {
                finish();
            }
        }
    }
}
