package com.exemplo.meuprimeiroapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.media.Image;
import android.media.ImageReader;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends Activity {

    private static final int REQUEST_CAPTURE = 1001;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;

    private TextView status;
    private TextView framesText;

    private long frameCount = 0;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);

        TextView titulo = new TextView(this);
        titulo.setText("GodeyeV1");
        titulo.setTextSize(32);
        titulo.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setText("Status: parado");
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 20);

        framesText = new TextView(this);
        framesText.setText("Frames recebidos: 0");
        framesText.setTextSize(18);
        framesText.setGravity(Gravity.CENTER);
        framesText.setPadding(0, 0, 0, 30);

        Button iniciar = new Button(this);
        iniciar.setText("Iniciar captura");
        iniciar.setTextSize(18);

        iniciar.setOnClickListener(v -> solicitarCaptura());

        layout.addView(titulo);
        layout.addView(status);
        layout.addView(framesText);
        layout.addView(iniciar);

        setContentView(layout);
    }

    private void solicitarCaptura() {

        projectionManager =
                (MediaProjectionManager)
                getSystemService(MEDIA_PROJECTION_SERVICE);

        Intent intent =
                projectionManager.createScreenCaptureIntent();

        startActivityForResult(intent, REQUEST_CAPTURE);
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

        if (requestCode == REQUEST_CAPTURE) {

            if (resultCode == RESULT_OK) {

                status.setText("Status: iniciando captura...");

                iniciarCaptura(
                        resultCode,
                        data);

            } else {

                status.setText(
                        "Status: captura recusada");
            }
        }
    }

    private void iniciarCaptura(
            int resultCode,
            Intent data) {

        mediaProjection =
                projectionManager.getMediaProjection(
                        resultCode,
                        data);

        DisplayMetrics metrics =
                getResources().getDisplayMetrics();

        int largura = metrics.widthPixels;
        int altura = metrics.heightPixels;
        int densidade = metrics.densityDpi;

        imageReader =
                ImageReader.newInstance(
                        largura,
                        altura,
                        android.graphics.PixelFormat.RGBA_8888,
                        2);

        imageReader.setOnImageAvailableListener(
                reader -> {

                    Image image = null;

                    try {

                        image = reader.acquireLatestImage();

                        if (image != null) {

                            frameCount++;

                            final long frames =
                                    frameCount;

                            handler.post(() ->
                                    framesText.setText(
                                            "Frames recebidos: "
                                            + frames));

                            image.close();
                        }

                    } catch (Exception e) {

                        if (image != null) {
                            image.close();
                        }
                    }

                },
                handler);

        virtualDisplay =
                mediaProjection.createVirtualDisplay(
                        "GodeyeV1",
                        largura,
                        altura,
                        densidade,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(),
                        null,
                        handler);

        status.setText(
                "Status: captura ativa");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (virtualDisplay != null) {
            virtualDisplay.release();
        }

        if (imageReader != null) {
            imageReader.close();
        }

        if (mediaProjection != null) {
            mediaProjection.stop();
        }
    }
}
