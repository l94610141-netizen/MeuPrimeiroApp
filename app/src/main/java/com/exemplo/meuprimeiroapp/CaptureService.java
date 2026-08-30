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

    private void detectarBolaBranca(Bitmap bitmap, int largura, int altura) {
        int passo = 4;
        int melhorX = 0;
        int melhorY = 0;
        int melhorScore = 0;

        int inicioY = altura / 8;
        int fimY = altura * 7 / 8;

        for (int y = inicioY; y < fimY; y += passo) {
            for (int x = 0; x < largura; x += passo) {

                int claros = 0;
                int total = 0;
                int raio = 14;

                for (int yy = y - raio; yy <= y + raio; yy += passo) {
                    if (yy < inicioY || yy >= fimY) continue;

                    for (int xx = x - raio; xx <= x + raio; xx += passo) {
                        if (xx < 0 || xx >= largura) continue;

                        int dx = xx - x;
                        int dy = yy - y;

                        if (dx * dx + dy * dy > raio * raio) continue;

                        total++;

                        int p = bitmap.getPixel(xx, yy);
                        int r = (p >> 16) & 0xff;
                        int g = (p >> 8) & 0xff;
                        int b = p & 0xff;

                        int brilho = (r + g + b) / 3;

                        if (brilho > 175 &&
                                Math.abs(r - g) < 35 &&
                                Math.abs(r - b) < 35 &&
                                Math.abs(g - b) < 35) {
                            claros++;
                        }
                    }
                }

                if (total == 0) continue;

                int score = claros * 100 / total;

                if (claros >= 10 && score > melhorScore) {
                    melhorScore = score;
                    melhorX = x;
                    melhorY = y;
                }
            }
        }

        if (melhorScore >= 20) {
            Intent resultado = new Intent("GODEYE_DETECCAO");
            resultado.putExtra("frames", frameCount);
            resultado.putExtra("x", melhorX);
            resultado.putExtra("y", melhorY);
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
