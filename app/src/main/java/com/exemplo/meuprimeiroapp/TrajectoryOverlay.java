package com.exemplo.meuprimeiroapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import android.view.Gravity;

public class TrajectoryOverlay {

    private final Context context;
    private final WindowManager windowManager;

    private TrajectoryView view;
    private WindowManager.LayoutParams params;

    public TrajectoryOverlay(Context context) {
        this.context = context;
        windowManager =
                (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
    }

    public void mostrar() {

        view = new TrajectoryView(context);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity =
                Gravity.TOP | Gravity.START;

        windowManager.addView(view, params);
    }

    public void desenharLinha(
            float x1,
            float y1,
            float x2,
            float y2) {

        if (view != null) {
            view.inicio =
                    new PointF(x1, y1);

            view.fim =
                    new PointF(x2, y2);

            view.invalidate();
        }
    }

    public void limpar() {

        if (view != null) {

            view.inicio = null;
            view.fim = null;

            view.invalidate();
        }
    }

    public void fechar() {

        if (view != null) {

            windowManager.removeView(view);
            view = null;
        }
    }

    private static class TrajectoryView
            extends View {

        private final Paint paint;

        private PointF inicio;
        private PointF fim;

        public TrajectoryView(Context context) {

            super(context);

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null);

            paint = new Paint(
                    Paint.ANTI_ALIAS_FLAG);

            paint.setColor(
                    Color.WHITE);

            paint.setStrokeWidth(6f);

            paint.setStyle(
                    Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            if (inicio != null &&
                    fim != null) {

                canvas.drawLine(
                        inicio.x,
                        inicio.y,
                        fim.x,
                        fim.y,
                        paint);
            }
        }
    }
}
