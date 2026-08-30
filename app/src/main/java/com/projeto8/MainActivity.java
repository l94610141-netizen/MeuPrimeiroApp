package com.projeto8;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new MesaView());
    }

    public static class MesaView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public MesaView(android.content.Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Fundo
            canvas.drawColor(Color.rgb(25, 25, 25));

            float largura = getWidth();
            float altura = getHeight();

            // Mesa
            paint.setColor(Color.rgb(20, 110, 55));

            float margemX = largura * 0.08f;
            float margemY = altura * 0.12f;

            canvas.drawRect(
                    margemX,
                    margemY,
                    largura - margemX,
                    altura - margemY,
                    paint
            );
        }
    }
}
