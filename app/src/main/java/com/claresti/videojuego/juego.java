package com.claresti.videojuego;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class juego extends Activity {
    private VistaJuego vistaJuego;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
    }
    @Override
    protected void onDestroy() {
        vistaJuego.setCorriendo(false);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vistaJuego.setPausa(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.setPausa(false);
    }
}
