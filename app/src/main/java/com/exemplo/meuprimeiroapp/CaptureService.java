package com.exemplo.meuprimeiroapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.graphics.PixelFormat;

public class CaptureService extends Service {

    public static final String ACTION_START = "START_CAPTURE";

    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;

    private long frameCount = 0;

    private static final String CHANNEL_ID = "GodeyeCapture";

    @Override
    public void onCreate() {
        super.onCreate();

        criarCanalNotificacao();

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("GodeyeV1")
                        .setContentText("Captura de tela ativa")
                        .setSmallIcon(android.R.drawable.ic_menu_view)
                        .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId) {

        if (intent != null &&
                ACTION_START.equals(intent.getAction())) {

            int resultCode =
                    intent.getIntExtra("resultCode", 0);

            Intent resultData =
                    intent.getParcelableExtra("data");

            iniciarCaptura(resultCode, resultData);
        }

        return START_NOT_STICKY;
    }

    private void iniciarCaptura(
            int resultCode,
            Intent resultData) {

        MediaProjectionManager manager =
                (MediaProjectionManager)
                getSystemService(
                        MEDIA_PROJECTION_SERVICE);

        mediaProjection =
                manager.getMediaProjection(
                        resultCode,
                        resultData);

        DisplayMetrics metrics =
                getResources().getDisplayMetrics();

        int largura = metrics.widthPixels;
        int altura = metrics.heightPixels;
        int densidade = metrics.densityDpi;

        imageReader =
                ImageReader.newInstance(
                        largura,
                        altura,
                        PixelFormat.RGBA_8888,
                        2);

        imageReader.setOnImageAvailableListener(
                reader -> {

                    Image image = null;

                    try {

                        image =
                                reader.acquireLatestImage();

                        if (image != null) {

                            frameCount++;

                            Intent broadcast =
                                    new Intent(
                                            "GODEYE_FRAME");

                            broadcast.putExtra(
                                    "frames",
                                    frameCount);

                            sendBroadcast(broadcast);

                            image.close();
                        }

                    } catch (Exception e) {

                        if (image != null) {
                            image.close();
                        }
                    }

                },
                new Handler(
                        Looper.getMainLooper()));

        virtualDisplay =
                mediaProjection.createVirtualDisplay(
                        "GodeyeV1",
                        largura,
                        altura,
                        densidade,
                        DisplayManager
                                .VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(),
                        null,
                        null);
    }

    private void criarCanalNotificacao() {

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "GodeyeV1",
                        NotificationManager
                                .IMPORTANCE_LOW);

        NotificationManager manager =
                getSystemService(
                        NotificationManager.class);

        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {

        if (virtualDisplay != null)
            virtualDisplay.release();

        if (imageReader != null)
            imageReader.close();

        if (mediaProjection != null)
            mediaProjection.stop();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
