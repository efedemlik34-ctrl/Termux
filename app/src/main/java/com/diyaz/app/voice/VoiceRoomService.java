package com.diyaz.app.voice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

/** Foreground-service shell for the future real-time voice transport. */
public class VoiceRoomService extends Service {
    private static final String CHANNEL = "diyaz_voice";
    private static final int ID = 1001;

    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "DİYAZ sesli oda", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Notification n = new Notification.Builder(this, CHANNEL)
                    .setContentTitle("DİYAZ")
                    .setContentText("Sesli oda bağlantısı aktif")
                    .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                    .build();
            startForeground(ID, n);
        }
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_NOT_STICKY; }
    @Override public IBinder onBind(Intent intent) { return null; }
}
