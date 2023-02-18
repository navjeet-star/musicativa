package com.developer.musicatiiva;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.developer.musicatiiva.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends  FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10005" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d("mohit", "onNewToken: "+s);

    }

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {

           showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

        }
    }

    public void showNotification(String title, String message) {

        Intent intent = new Intent(this, SplashActivity.class);
        String channel_id = NOTIFICATION_CHANNEL_ID;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

       // Uri sound = Uri.parse("android.resource://" + this.getPackageName() +"/"+ R.raw.sonu);

        Uri sound = Uri. parse (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" +R.raw.rm) ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id).setSmallIcon(R.mipmap.ic_musicative_foregroundd)
                .setAutoCancel(true).setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);
                            builder = builder.setContentTitle(title)
                           .setSound(sound).
                            setContentText(message).
                            setSmallIcon(R.mipmap.ic_musicative_foregroundd);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build() ;

            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel(channel_id , "qawam_app" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.setSound(sound , audioAttributes) ;

            builder.setChannelId(NOTIFICATION_CHANNEL_ID) ;
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert notificationManager != null;
        notificationManager.notify(( int ) System. currentTimeMillis () ,
                builder.build()) ;
    }
}
