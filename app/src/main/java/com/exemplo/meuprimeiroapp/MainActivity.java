package com.exemplo.meuprimeiroapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;

public class MainActivity extends Activity {

    private static final int REQUEST_CAPTURE = 1001;

    private TextView status;
    private TextView framesText;
    private TextView posicaoText;

    private BroadcastReceiver receiver =
            new BroadcastReceiver() {

        @Override
        public void onReceive(
                Context context,
                Intent intent) {

            if ("GODEYE_DETECCAO".equals(
                    intent.getAction())) {

                long frames =
                        intent.getLongExtra(
                                "frames", 0);

                int x =
                        intent.getIntExtra(
                                "x", 0);

                int y =
                        intent.getIntExtra(
                                "y", 0);

                framesText.setText(
                        "Frames: " + frames);

                posicaoText.setText(
                        "Bola branca: X=" +
                        x +
                        "  Y=" +
                        y);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL);

        layout.setGravity(Gravity.CENTER);

        layout.setPadding(
                30, 30, 30, 30);

        TextView titulo =
                new TextView(this);

        titulo.setText(
                "Godeye V2.1");

        titulo.setTextSize(32);

        titulo.setGravity(
                Gravity.CENTER);

        status =
                new TextView(this);

        status.setText(
                "Status: parado");

        status.setTextSize(20);

        status.setGravity(
                Gravity.CENTER);

        framesText =
                new TextView(this);

        framesText.setText(
                "Frames: 0");

        framesText.setTextSize(18);

        framesText.setGravity(
                Gravity.CENTER);

        posicaoText =
                new TextView(this);

        posicaoText.setText(
                "Bola branca: não detectada");

        posicaoText.setTextSize(18);

        posicaoText.setGravity(
                Gravity.CENTER);

        Button iniciar =
                new Button(this);

        iniciar.setText(
                "Iniciar captura");

        iniciar.setOnClickListener(
                v -> solicitarCaptura());

        layout.addView(titulo);
        layout.addView(status);
        layout.addView(framesText);
        layout.addView(posicaoText);
        layout.addView(iniciar);

        setContentView(layout);
    }

    private void solicitarCaptura() {

        MediaProjectionManager manager =
                (MediaProjectionManager)
                        getSystemService(
                                MEDIA_PROJECTION_SERVICE);

        Intent intent =
                manager.createScreenCaptureIntent();

        startActivityForResult(
                intent,
                REQUEST_CAPTURE);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data);

        if (requestCode == REQUEST_CAPTURE &&
                resultCode == RESULT_OK) {

            Intent serviceIntent =
                    new Intent(
                            this,
                            CaptureService.class);

            serviceIntent.setAction(
                    CaptureService.ACTION_START);

            serviceIntent.putExtra(
                    "resultCode",
                    resultCode);

            serviceIntent.putExtra(
                    "data",
                    data);

            startForegroundService(
                    serviceIntent);

            status.setText(
                    "Status: capturando");
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        registerReceiver(
                receiver,
                new IntentFilter(
                        "GODEYE_DETECCAO"),
                Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {

        unregisterReceiver(receiver);

        super.onPause();
    }
}
