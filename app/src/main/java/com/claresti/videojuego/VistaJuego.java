package com.claresti.videojuego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Vector;

/**
 * Created by Alan Abundis on 08/05/2017.
 */

public class VistaJuego extends View implements SensorEventListener {
    /**
     * Se declaran las variables que utilizaremos
     */
    private int puntuacion = 0;
    // //// THREAD Y TIEMPO //////
    private boolean corriendo;
    private boolean pausa;
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    private Activity padre;
    // //// ASTEROIDES //////
    private Vector<Grafico> Asteroides; // Vector con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos; // Fragmentos en que se divide
    private Drawable drawableAsteroide[] = new Drawable[3];



    // //// NAVE //////
    // Gráfico de la nave
    private Grafico nave;
    private int giroNave; // Incremento de dirección
    private float aceleracionNave; // aumento de velocidad
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // //// MISIL //////
    private Grafico misil;
    private static int PASO_VELOCIDAD_MISIL = 12;
    private boolean misilActivo = false;
    private int distanciaMisil;

    /**
     * Funcion para asigna las texturas de los elementos
     * @param context
     * @param attrs
     */
    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableMisil;
        SharedPreferences pref = context.getSharedPreferences(
                "org.example.asteroides_preferences", Context.MODE_PRIVATE);
        try {
            numFragmentos = Integer.parseInt(pref.getString("fragmentos", "3"));
        } catch (Exception e) {
            numFragmentos = 3;
        }
        /*if (pref.getString("graficos", "0").equals("0")) {
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            for (int i = 0; i < 3; i++) {
                ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(
                        pathAsteroide, 1, 1));
                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50 - i * 14);
                dAsteroide.setIntrinsicHeight(50 - i * 14);
                drawableAsteroide[i] = dAsteroide;
            }
            Path pathNave = new Path();
            pathNave.moveTo((float) 0.0, (float) 0.0);
            pathNave.lineTo((float) 1.0, (float) 0.5);
            pathNave.lineTo((float) 0.0, (float) 1.0);
            pathNave.lineTo((float) 0.0, (float) 0.0);
            ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1,
                    1));
            dNave.getPaint().setColor(Color.WHITE);
            dNave.getPaint().setStyle(Paint.Style.STROKE);
            dNave.setIntrinsicWidth(20);
            dNave.setIntrinsicHeight(15);
            drawableNave = dNave;

            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;

            setBackgroundColor(Color.BLACK);
        } else {*/
            drawableAsteroide[0] =context.getResources().getDrawable(R.drawable.c1);
            drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.c2);
            drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.c3);

            drawableNave = context.getResources().getDrawable(R.drawable.p);
            drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
       // }

        Asteroides = new Vector<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            Asteroides.add(asteroide);
        }
        nave = new Grafico(this, drawableNave);
        misil = new Grafico(this, drawableMisil);

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager
                .getSensorList(Sensor.TYPE_ORIENTATION);
        if (!listSensors.isEmpty()) {
            Sensor orientationSensor = listSensors.get(0);
            mSensorManager.registerListener(this, orientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * Funcion para generar los asteroides aleatoriamente
     * @param ancho
     * @param alto
     * @param ancho_anter
     * @param alto_anter
     */
    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter,
                                 int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        nave.setPosX((ancho - nave.getAncho()) / 2);
        nave.setPosY((alto - nave.getAlto()) / 2);
        for (Grafico asteroide : Asteroides) {
            do {
                asteroide.setPosX(Math.random()
                        * (ancho - asteroide.getAncho()));
                asteroide.setPosY(Math.random() * (alto - asteroide.getAlto()));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }
        thread.start();
    }

    /**
     * Funcion para crear la animacion del misil
     * @param canvas
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (misilActivo)
            misil.dibujaGrafico(canvas);
        nave.dibujaGrafico(canvas);
        for (Grafico asteroide : Asteroides) {
            asteroide.dibujaGrafico(canvas);
        }
    }

    /**
     * Clase para inicializar el hilo que va a correr el juego
     */
    class ThreadJuego extends Thread {
        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                if (!pausa)
                    actualizaFisica();
            }
        }
    }

    /**
     * Funcion para la actualizacion de la fisica y la gravedad de los elementos
     */
    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        // Actualizamos posición nave
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleracionNave
                * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave
                * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
        if (Grafico.distanciaE(0, 0, nIncX, nIncY) <= Grafico.getMaxVelocidad()) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        // Actualizamos posición de asteroides
        nave.incrementaPos();
        for (Grafico asteroide : Asteroides) {
            asteroide.incrementaPos();
        }
        // Actualizamos posición de misil
        if (misilActivo) {
            misil.incrementaPos();
            distanciaMisil--;
            if (distanciaMisil < 0) {
                misilActivo = false;
            } else {
                for (int i = 0; i < Asteroides.size(); i++)
                    if (misil.verificaColision(Asteroides.elementAt(i))) {
                        destruyeAsteroide(i);
                        break;
                    }
            }
        }
        for (Grafico asteroide : Asteroides) {
            if (asteroide.verificaColision(nave)) {
                salir();
            }
        }
        ultimoProceso = ahora;
    }

    /**
     * Funcion para crear la animacion de la colision
     * @param i
     */
    private void destruyeAsteroide(int i) {
        int tam;
        if (Asteroides.get(i).getDrawable() != drawableAsteroide[2]) {
            if (Asteroides.get(i).getDrawable() == drawableAsteroide[1]) {
                tam = 2;
            } else {
                tam = 1;
            }
            for (int n = 0; n < numFragmentos; n++) {
                Grafico asteroide = new Grafico(this, drawableAsteroide[tam]);
                asteroide.setPosX(Asteroides.get(i).getPosX());
                asteroide.setPosY(Asteroides.get(i).getPosY());
                asteroide.setIncX(Math.random() * 7 - 2 - tam);
                asteroide.setIncY(Math.random() * 7 - 2 - tam);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                Asteroides.add(asteroide);
            }
        }
        Asteroides.remove(i);
        misilActivo = false;
        puntuacion += 1000;
        if (Asteroides.isEmpty()) {
            salir();
        }
    }

    /**
     * Funcion para salir del juego
     */
    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }

    /**
     * Funsion para disparar el misil
     */
    private void ActivaMisil() {
        misil.setPosX(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho()
                / 2);
        misil.setPosY(nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo()))
                * PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo()))
                * PASO_VELOCIDAD_MISIL);
        distanciaMisil = (int) Math.min(
                this.getWidth() / Math.abs(misil.getIncX()), this.getHeight()
                        / Math.abs(misil.getIncY())) - 2;
        misilActivo = true;
    }

    /**
     * Funcion para el manejo de la nave
     * @param codigoTecla
     * @param evento
     * @return
     */
    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                ActivaMisil();
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    /**
     * Funcion para validar si se presiono la pantalla con el teclado
     * @param codigoTecla
     * @param evento
     * @return
     */
    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    // MANEJO DE LA NAVE CON PANTALLA TÁCTIL
    private float mX = 0, mY = 0;
    private boolean disparo = false;

    /**
     * Funcion para validar si se esta utilizando el touch
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx < 6 && dy > 6) {
                    aceleracionNave = Math.round((mY - y) / 25);
                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    ActivaMisil();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    /**
     * Funcion para el manejo de la nave con sensores
     * @param sensor
     * @param accuracy
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private boolean hayValorInicial = false;
    private float valorInicial;

    /**
     * Funsion para validar los cambios del sensor
     * @param event
     */
    public void onSensorChanged(SensorEvent event) {
        float valor = event.values[1];
        if (!hayValorInicial) {
            valorInicial = valor;
            hayValorInicial = true;
        }
        giroNave = (int) (valor - valorInicial) / 3;
    }

    /**
     *
     * @param corriendo
     */
    public void setCorriendo(boolean corriendo) {
        this.corriendo = corriendo;
    }

    /**
     *
     * @param pausa
     */
    public void setPausa(boolean pausa) {
        this.pausa = pausa;
    }

    /**
     *
     * @param padre
     */
    public void setPadre(Activity padre) {
        this.padre = padre;
    }
}
