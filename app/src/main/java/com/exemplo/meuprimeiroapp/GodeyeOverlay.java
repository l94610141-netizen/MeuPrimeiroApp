package com.exemplo.meuprimeiroapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GodeyeOverlay {

    private final Context context;
    private final WindowManager windowManager;

    private LinearLayout janela;
    private LinearLayout menu;
    private WindowManager.LayoutParams params;

    public GodeyeOverlay(Context context) {
        this.context = context;
        windowManager =
                (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
    }

    public void mostrar() {

        janela = new LinearLayout(context);
        janela.setOrientation(LinearLayout.VERTICAL);
        janela.setPadding(12, 12, 12, 12);

        GradientDrawable fundo = new GradientDrawable();
        fundo.setColor(Color.argb(235, 25, 25, 25));
        fundo.setCornerRadius(25);
        janela.setBackground(fundo);

        TextView icone = new TextView(context);
        icone.setText("👁");
        icone.setTextSize(28);
        icone.setGravity(Gravity.CENTER);
        icone.setPadding(15, 10, 15, 10);

        janela.addView(icone);

        menu = criarMenu();
        janela.addView(menu);

        menu.setVisibility(View.GONE);

        icone.setOnClickListener(v -> {

            if (menu.getVisibility() == View.VISIBLE) {
                menu.setVisibility(View.GONE);
            } else {
                menu.setVisibility(View.VISIBLE);
            }
        });

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 30;
        params.y = 150;

        windowManager.addView(janela, params);

        permitirArrastar(icone);
    }

    private LinearLayout criarMenu() {

        LinearLayout painel = new LinearLayout(context);
        painel.setOrientation(LinearLayout.VERTICAL);

        TextView titulo = new TextView(context);
        titulo.setText("GODEYE");
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(20);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(10, 10, 10, 15);

        painel.addView(titulo);

        adicionarBotao(painel, "⚪ Bola branca");
        adicionarBotao(painel, "🎯 Trajetória");
        adicionarBotao(painel, "💥 Colisões");
        adicionarBotao(painel, "📐 Mira");

        Button fechar = new Button(context);
        fechar.setText("− Minimizar");

        fechar.setOnClickListener(v -> fechar());

        painel.addView(fechar);

        return painel;
    }

    private void adicionarBotao(
            LinearLayout painel,
            String texto) {

        Button botao = new Button(context);
        botao.setText(texto);

        painel.addView(botao);
    }

    private void permitirArrastar(View view) {

        view.setOnTouchListener(
                new View.OnTouchListener() {

                    private int inicialX;
                    private int inicialY;
                    private float toqueX;
                    private float toqueY;

                    @Override
                    public boolean onTouch(
                            View v,
                            MotionEvent event) {

                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:

                                inicialX = params.x;
                                inicialY = params.y;

                                toqueX = event.getRawX();
                                toqueY = event.getRawY();

                                return false;

                            case MotionEvent.ACTION_MOVE:

                                params.x =
                                        inicialX +
                                        (int) (event.getRawX() - toqueX);

                                params.y =
                                        inicialY +
                                        (int) (event.getRawY() - toqueY);

                                windowManager.updateViewLayout(
                                        janela,
                                        params);

                                return true;
                        }

                        return false;
                    }
                });
    }

    public void fechar() {

        if (janela != null) {
            windowManager.removeView(janela);
            janela = null;
        }
    }
}
