package com.claresti.videojuego;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Alan Abundis on 08/05/2017.
 */

public class Grafico {
    /**
     * Se declaran las variables que utilizaremos
     */
    private Drawable drawable; // Imagen que dibujaremos
    private double posX, posY; // Posición
    private double incX, incY; // Velocidad desplazamiento
    private int angulo, rotacion;// Ángulo y velocidad rotación
    private int ancho, alto; // Dimensiones de la imagen
    private int radioColision; // Para determinar colisión
    // Donde dibujamos el gráfico (usada en view.ivalidate)
    private View view;
    // Para determinar el espacio a borrar (view.ivalidate)
    public static final int MAX_VELOCIDAD = 20;

    /**
     * Constructor
     * @param view
     * @param drawable
     */
    public Grafico(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioColision = (alto + ancho) / 4;
    }

    /**
     * Funcion para dibujar los graficos
     * @param canvas
     */
    public void dibujaGrafico(Canvas canvas) {
        canvas.save();
        int x = (int) (posX + ancho / 2);
        int y = (int) (posY + alto / 2);
        canvas.rotate((float) angulo, (float) x, (float) y);
        drawable.setBounds((int) posX, (int) posY, (int) posX + ancho,
                (int) posY + alto);
        drawable.draw(canvas);
        canvas.restore();
        int rInval = (int) distanciaE(0, 0, ancho, alto) / 2 + MAX_VELOCIDAD;
        view.invalidate(x - rInval, y - rInval, x + rInval, y + rInval);
    };

    /**
     * Funcion para el movimiento de los elementos
     *
     */

    public void incrementaPos() {
        posX += incX;
        // Si salimos de la pantalla, corregimos posición
        if (posX < -ancho / 2) {
            posX = view.getWidth() - ancho / 2;
        }
        if (posX > view.getWidth() - ancho / 2) {
            posX = -ancho / 2;
        }
        posY += incY;
        // Si salimos de la pantalla, corregimos posición
        if (posY < -alto / 2) {
            posY = view.getHeight() - alto / 2;
        }
        if (posY > view.getHeight() - alto / 2) {
            posY = -alto / 2;
        }
        angulo += rotacion; // Actualizamos ángulo
    }

    /**
     * Funcion para las colisiones
     * @param g
     * @return
     */
    public double distancia(Grafico g) {
        return distanciaE(posX, posY, g.posX, g.posY);
    }
    public boolean verificaColision(Grafico g) {
        return (distancia(g) < (radioColision + g.radioColision));
    }

    /**
     * Funcion que devuelve la posicion de los objetos
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */

    public static double distanciaE(double x, double y, double x2, double y2) {
        return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
    }

    /**
     *
     * @return
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     *
     * @param drawable
     */
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }


    /**
     *
     * @return posX
     */
    public double getPosX() {
        return posX;
    }

    /**
     *
     * @param posX
     */
    public void setPosX(double posX) {
        this.posX = posX;
    }

    /**
     *
     * @return posY
     */
    public double getPosY() {
        return posY;
    }

    /**
     *
     * @param posY
     */
    public void setPosY(double posY) {
        this.posY = posY;
    }

    /**
     *
     * @return incX
     */
    public double getIncX() {
        return incX;
    }

    /**
     *
     * @param incX
     */
    public void setIncX(double incX) {
        this.incX = incX;
    }

    /**
     *
     * @return incY
     */
    public double getIncY() {
        return incY;
    }

    /**
     *
     * @param incY
     */
    public void setIncY(double incY) {
        this.incY = incY;
    }

    /**
     *
     * @return angulo
     */
    public int getAngulo() {
        return angulo;
    }

    /**
     *
     * @param angulo
     */
    public void setAngulo(int angulo) {
        this.angulo = angulo;
    }

    /**
     *
     * @return rotacion
     */
    public int getRotacion() {
        return rotacion;
    }

    /**
     *
     * @param rotacion
     */
    public void setRotacion(int rotacion) {
        this.rotacion = rotacion;
    }

    /**
     *
     * @return ancho
     */
    public int getAncho() {
        return ancho;
    }

    /**
     *
     * @param ancho
     */
    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    /**
     *
     * @return alto
     */
    public int getAlto() {
        return alto;
    }

    /**
     *
     * @param alto
     */
    public void setAlto(int alto) {
        this.alto = alto;
    }

    /**
     *
     * @return radioColision
     */
    public int getRadioColision() {
        return radioColision;
    }

    /**
     *
     * @param radioColision
     */
    public void setRadioColision(int radioColision) {
        this.radioColision = radioColision;
    }

    /**
     *
     * @return view
     */
    public View getView() {
        return view;
    }

    /**
     *
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     *
     * @return MAX_VELOCIDAD
     */
    public static int getMaxVelocidad() {
        return MAX_VELOCIDAD;
    }
}
