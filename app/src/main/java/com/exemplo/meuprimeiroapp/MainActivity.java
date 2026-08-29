package com.exemplo.meuprimeiroapp;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView texto = new TextView(this);
        texto.setText("Olá! Meu primeiro app!");
        texto.setTextSize(24);
        texto.setTextColor(Color.BLACK);
        texto.setGravity(Gravity.CENTER);

        setContentView(texto);
    }
}
