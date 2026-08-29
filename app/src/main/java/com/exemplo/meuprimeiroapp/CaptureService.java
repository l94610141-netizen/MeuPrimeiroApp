package com.exemplo.meuprimeiroapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;

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

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "GodeyeV2.1",
                        NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager =
                getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Godeye V2.1")
                        .setContentText("Detectando bola branca")
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

            Intent data =
                    intent.getParcelableExtra("data");

            iniciarCaptura(resultCode, data);
        }

        return START_NOT_STICKY;
    }

    private void iniciarCaptura(
            int resultCode,
            Intent data) {

        MediaProjectionManager manager =
                (MediaProjectionManager)
                        getSystemService(
                                MEDIA_PROJECTION_SERVICE);

        mediaProjection =
                manager.getMediaProjection(
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
                        PixelFormat.RGBA_8888,
                        2);

        imageReader.setOnImageAvailableListener(
                reader -> processarFrame(reader),
                new Handler(Looper.getMainLooper()));

        virtualDisplay =
                mediaProjection.createVirtualDisplay(
                        "GodeyeV2.1",
                        largura,
                        altura,
                        densidade,
                        DisplayManager
                                .VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(),
                        null,
                        null);
    }

    private void processarFrame(ImageReader reader) {

        Image image = null;

        try {

            image = reader.acquireLatestImage();

            if (image == null)
                return;

            frameCount++;

            Image.Plane plane =
                    image.getPlanes()[0];

            ByteBuffer buffer =
                    plane.getBuffer();

            int pixelStride =
                    plane.getPixelStride();

            int rowStride =
                    plane.getRowStride();

            int largura =
                    image.getWidth();

            int altura =
                    image.getHeight();

            int rowPadding =
                    rowStride -
                    pixelStride * largura;

            int bitmapWidth =
                    largura +
                    rowPadding / pixelStride;

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            bitmapWidth,
                            altura,
                            Bitmap.Config.ARGB_8888);

            bitmap.copyPixelsFromBuffer(buffer);

            detectarBolaBranca(
                    bitmap,
                    largura,
                    altura);

            bitmap.recycle();

        } catch (Exception e) {

            // Evita que um frame defeituoso
            // derrube o serviço.

        } finally {

            if (image != null)
                image.close();
        }
    }

    private void detectarBolaBranca(
            Bitmap bitmap,
            int largura,
            int altura) {

        long somaX = 0;
        long somaY = 0;
        long quantidade = 0;

        /*
         * Procuramos pixels muito claros.
         *
         * Isto é apenas um detector inicial.
         * Na V2.1-B vamos melhorar a identificação
         * para procurar uma região circular.
         */

        for (int y = 0; y < altura; y += 4) {

            for (int x = 0; x < largura; x += 4) {

                int pixel =
                        bitmap.getPixel(x, y);

                int r =
                        (pixel >> 16) & 0xff;

                int g =
                        (pixel >> 8) & 0xff;

                int b =
                        pixel & 0xff;

                /*
                 * Branco aproximadamente neutro.
                 */
                if (r > 225 &&
                        g > 225 &&
                        b > 225) {

                    somaX += x;
                    somaY += y;
                    quantidade++;
                }
            }
        }

        if (quantidade > 0) {

            int centroX =
                    (int)(somaX / quantidade);

            int centroY =
                    (int)(somaY / quantidade);

            Intent resultado =
                    new Intent("GODEYE_DETECCAO");

            resultado.putExtra(
                    "frames",
                    frameCount);

            resultado.putExtra(
                    "x",
                    centroX);

            resultado.putExtra(
                    "y",
                    centroY);

            sendBroadcast(resultado);
        }
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
