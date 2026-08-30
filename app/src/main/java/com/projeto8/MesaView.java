package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MesaView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MesaView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(30, 30, 30));

        float margemX = 40;
        float margemY = 80;

        float esquerda = margemX;
        float topo = margemY;
        float direita = getWidth() - margemX;
        float baixo = getHeight() - margemY;

        // Madeira da mesa
        paint.setColor(Color.rgb(100, 55, 25));
        canvas.drawRect(esquerda, topo, direita, baixo, paint);

        // Campo verde
        paint.setColor(Color.rgb(20, 110, 55));
        canvas.drawRect(
                esquerda + 25,
                topo + 25,
                direita - 25,
                baixo - 25,
                paint
        );

        // Bolsas
        paint.setColor(Color.BLACK);

        float raio = 22;

        canvas.drawCircle(esquerda + 25, topo + 25, raio, paint);
        canvas.drawCircle(direita - 25, topo + 25, raio, paint);
        canvas.drawCircle(esquerda + 25, baixo - 25, raio, paint);
        canvas.drawCircle(direita - 25, baixo - 25, raio, paint);

        canvas.drawCircle(
                (esquerda + direita) / 2,
                topo + 25,
                raio,
                paint
        );

        canvas.drawCircle(
                (esquerda + direita) / 2,
                baixo - 25,
                raio,
                paint
        );

        // Bola branca
        paint.setColor(Color.WHITE);
        canvas.drawCircle(
                getWidth() / 2f,
                getHeight() / 2f,
                18,
                paint
        );
    }
}
