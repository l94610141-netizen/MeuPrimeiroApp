package com.projeto8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class MesaView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MesaView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fundo
        canvas.drawColor(Color.rgb(22, 22, 22));

        float w = getWidth();
        float h = getHeight();

        // Mesa com proporção de sinuca
        float mesaEsq = 22;
        float mesaDir = w - 22;

        float mesaLargura = mesaDir - mesaEsq;
        float mesaAltura = mesaLargura * 0.55f;

        float mesaTopo = (h - mesaAltura) / 2f;
        float mesaBaixo = mesaTopo + mesaAltura;

        // Sombra da mesa
        paint.setColor(Color.rgb(5, 5, 5));
        canvas.drawRoundRect(
                new RectF(
                        mesaEsq + 8,
                        mesaTopo + 10,
                        mesaDir + 8,
                        mesaBaixo + 10
                ),
                18,
                18,
                paint
        );

        // Madeira externa
        paint.setColor(Color.rgb(92, 48, 22));
        canvas.drawRoundRect(
                new RectF(
                        mesaEsq,
                        mesaTopo,
                        mesaDir,
                        mesaBaixo
                ),
                18,
                18,
                paint
        );

        // Madeira interna
        paint.setColor(Color.rgb(135, 72, 28));
        canvas.drawRoundRect(
                new RectF(
                        mesaEsq + 10,
                        mesaTopo + 10,
                        mesaDir - 10,
                        mesaBaixo - 10
                ),
                13,
                13,
                paint
        );

        // Feltro
        float feltroEsq = mesaEsq + 32;
        float feltroTopo = mesaTopo + 30;
        float feltroDir = mesaDir - 32;
        float feltroBaixo = mesaBaixo - 30;

        paint.setColor(Color.rgb(18, 105, 53));

        canvas.drawRect(
                feltroEsq,
                feltroTopo,
                feltroDir,
                feltroBaixo,
                paint
        );

        // Sombra interna do feltro
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.rgb(12, 72, 38));

        canvas.drawRect(
                feltroEsq,
                feltroTopo,
                feltroDir,
                feltroBaixo,
                paint
        );

        paint.setStyle(Paint.Style.FILL);

        // Caçapas grandes
        float raioBolso = 28;

        paint.setColor(Color.rgb(5, 5, 5));

        canvas.drawCircle(feltroEsq, feltroTopo, raioBolso, paint);
        canvas.drawCircle(feltroDir, feltroTopo, raioBolso, paint);

        canvas.drawCircle(feltroEsq, feltroBaixo, raioBolso, paint);
        canvas.drawCircle(feltroDir, feltroBaixo, raioBolso, paint);

        canvas.drawCircle(
                (feltroEsq + feltroDir) / 2f,
                feltroTopo,
                raioBolso,
                paint
        );

        canvas.drawCircle(
                (feltroEsq + feltroDir) / 2f,
                feltroBaixo,
                raioBolso,
                paint
        );

        // Aro das caçapas
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.rgb(45, 25, 12));

        canvas.drawCircle(feltroEsq, feltroTopo, raioBolso, paint);
        canvas.drawCircle(feltroDir, feltroTopo, raioBolso, paint);
        canvas.drawCircle(feltroEsq, feltroBaixo, raioBolso, paint);
        canvas.drawCircle(feltroDir, feltroBaixo, raioBolso, paint);

        canvas.drawCircle(
                (feltroEsq + feltroDir) / 2f,
                feltroTopo,
                raioBolso,
                paint
        );

        canvas.drawCircle(
                (feltroEsq + feltroDir) / 2f,
                feltroBaixo,
                raioBolso,
                paint
        );

        paint.setStyle(Paint.Style.FILL);

        // Bola branca
        float bolaX = w * 0.68f;
        float bolaY = (feltroTopo + feltroBaixo) / 2f;
        float bolaRaio = 17;

        // Sombra da bola
        paint.setColor(Color.argb(100, 0, 0, 0));

        canvas.drawCircle(
                bolaX + 3,
                bolaY + 4,
                bolaRaio,
                paint
        );

        // Bola
        paint.setColor(Color.WHITE);

        canvas.drawCircle(
                bolaX,
                bolaY,
                bolaRaio,
                paint
        );

        // Brilho
        paint.setColor(Color.rgb(235, 235, 235));

        canvas.drawCircle(
                bolaX - 5,
                bolaY - 5,
                4,
                paint
        );
    }
}
