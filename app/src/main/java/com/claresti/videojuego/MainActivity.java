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
    /**
     * Se declaran las variables que utilizaremos
     */
    private MediaPlayer mp;
    private GestureLibrary libreria;
    private Button bAcercaDe;
    private Button bJugar;
    private Button bPreferencias;
    private Button bSalir;

    /**
     * Funcion para crear las acciones de los eventos
     * @param savedInstanceState
     */
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

    /**
     * Funcion para reanudar el juego
     */
    @Override protected void onResume(){
        super.onResume();
        mp.start();
    }

    /**
     * Funcion para pausar el juego
     */
    @Override protected void onPause(){
        super.onPause();
        mp.pause();
    }

    /**
     * Funcion para guardar la posicion actual de la nave al momento de pausar
     * @param estadoGuardado
     */
    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion", pos);
        }
    }

    /**
     * Funcion para restaurar la posicion en la que se encontraba cuando se pauso el juego
     * @param estadoGuardado
     */
    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado){
        super.onRestoreInstanceState(estadoGuardado);
        if (estadoGuardado != null && mp != null) {
            int pos = estadoGuardado.getInt("posicion");
            mp.seekTo(pos);
        }
    }

    /**
     * Funcian para ir a la ventana de AcercaDe
     */
    public void lanzarAcercaDe() {
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    /**
     * Funcion para iniciar el juega
     */
    public void lanzarJugar() {
        Intent i = new Intent(this, juego.class);
        startActivity(i);
    }

    /**
     * Funcion para ir a la ventana de ajustes
     */
    public void lanzarPreferencias() {
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    /**
     * Funcion para Crear el menu en la parte superior isquierda del dispositivo
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Funcion para verificar que opcion seleccionaste del menu
     * @param item
     * @return
     */
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

    /**
     * Funcion para verificar que boton precionaste
     * @param ov
     * @param gesture
     */
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
