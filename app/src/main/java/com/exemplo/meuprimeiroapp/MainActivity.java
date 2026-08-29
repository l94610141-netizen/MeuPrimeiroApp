package com.exemplo.meuprimeiroapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.graphics.Color;

public class MainActivity extends Activity {

    EditText visor;
    double numero1 = 0;
    String operador = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        visor = new EditText(this);
        visor.setTextSize(32);
        visor.setGravity(Gravity.RIGHT);
        visor.setInputType(2);
        layout.addView(visor);

        String[] botoes = {
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", "C", "=", "+"
        };

        GridLayout grade = new GridLayout(this);
        grade.setColumnCount(4);

        for (String texto : botoes) {
            Button botao = new Button(this);
            botao.setText(texto);
            botao.setTextSize(20);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 120;
            params.columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);

            botao.setLayoutParams(params);

            botao.setOnClickListener(v -> clicar(texto));

            grade.addView(botao);
        }

        layout.addView(grade);
        setContentView(layout);
    }

    void clicar(String texto) {

        if (texto.equals("C")) {
            visor.setText("");
            numero1 = 0;
            operador = "";
            return;
        }

        if (texto.equals("+") || texto.equals("-") ||
            texto.equals("×") || texto.equals("÷")) {

            if (!visor.getText().toString().isEmpty()) {
                numero1 = Double.parseDouble(
                        visor.getText().toString());
                operador = texto;
                visor.setText("");
            }
            return;
        }

        if (texto.equals("=")) {
            if (visor.getText().toString().isEmpty())
                return;

            double numero2 = Double.parseDouble(
                    visor.getText().toString());

            double resultado = 0;

            switch (operador) {
                case "+":
                    resultado = numero1 + numero2;
                    break;
                case "-":
                    resultado = numero1 - numero2;
                    break;
                case "×":
                    resultado = numero1 * numero2;
                    break;
                case "÷":
                    if (numero2 != 0)
                        resultado = numero1 / numero2;
                    else {
                        visor.setText("Erro");
                        return;
                    }
                    break;
            }

            visor.setText(String.valueOf(resultado));
            return;
        }

        visor.append(texto);
    }
}
