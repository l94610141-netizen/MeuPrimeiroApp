package com.exemplo.meuprimeiroapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.media.projection.MediaProjectionManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;

public class MainActivity extends Activity {

    private static final int REQUEST_CAPTURE = 1001;

    private TextView status;
    private GodeyeOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL);

        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);

        TextView titulo =
                new TextView(this);

        titulo.setText("Godeye V2.2");
        titulo.setTextSize(32);
        titulo.setGravity(Gravity.CENTER);

        status =
                new TextView(this);

        status.setText(
                "Status: parado");

        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 30);

        Button iniciar =
                new Button(this);

        iniciar.setText(
                "Iniciar Godeye");

        iniciar.setTextSize(18);

        iniciar.setOnClickListener(
                v -> iniciarGodeye());

        layout.addView(titulo);
        layout.addView(status);
        layout.addView(iniciar);

        setContentView(layout);
    }

    private void iniciarGodeye() {

        if (!Settings.canDrawOverlays(this)) {

            Intent intent =
                    new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse(
                                    "package:" + getPackageName()));

            startActivity(intent);

            status.setText(
                    "Conceda a permissão e volte ao Godeye");

            return;
        }

        solicitarCaptura();
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

            overlay =
                    new GodeyeOverlay(this);

            overlay.mostrar();

            status.setText(
                    "Godeye ativo");
        }
    }
}
