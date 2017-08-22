package com.example.tom.projectws;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Tom on 2017/8/2.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    private static final String TAG="MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent new_intent = new Intent();
        new_intent.setAction("ACTION_STRING_ACTIVITY");
        new_intent.putExtra("msg", remoteMessage.getNotification().getBody());
        sendBroadcast(new_intent);
        Log.d(TAG,"FROM:"+remoteMessage.getFrom());
        //check if the message contains data
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"Message data:"+remoteMessage.getData());

            //check if the message contains notification
            if(remoteMessage.getNotification()!=null){
                Log.d(TAG,"Message body:"+remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getBody());
            }
        }
    }
/*
* display notification
* */
    private void sendNotification(String body) {
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0/*request code*/,intent,PendingIntent.FLAG_ONE_SHOT);
        //set sound notification

        Uri notifictaionSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Cloud Messaging")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notifictaionSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0/*ID of notification*/,notifiBuilder.build());

    }
}
