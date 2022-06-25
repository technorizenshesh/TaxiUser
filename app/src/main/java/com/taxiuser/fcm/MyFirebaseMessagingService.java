package com.taxiuser.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxiuser.R;
import com.taxiuser.activities.HomeAct;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notificationManager;
    private MediaPlayer mPlayer;
    Intent intent;
    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "fsfsdfd:" + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();

            try {

                String title = "", key = "", status = "", noti_type = "", bookindStatus = "", bookType = "", requestId = "";

                JSONObject object = new JSONObject(data.get("message"));

                try {
                    noti_type = object.getString("notifi_type");
                } catch (Exception e) {
                }

                try {
                    bookType = object.getString("booktype");
                } catch (Exception e) {
                }

                try {
                    requestId = object.getString("request_id");
                } catch (Exception e) {
                }

                try {
                    bookindStatus = object.getString("booking_status");
                } catch (Exception e) {
                }

                try {
                    key = object.getString("key");
                    status = object.getString("status");
                } catch (Exception e) {
                }

                Log.e("fasdfsadfsfs", "key = " + key);
                Log.e("fasdfsadfsfs", "bookindStatus = " + bookindStatus);
                Log.e("fasdfsadfsfs", "noti_type = " + noti_type);
                Log.e("fasdfsadfsfs", "status = " + status);

                if ("Cancel".equals(bookindStatus)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "Cancel");
                    sendBroadcast(intent1);
                } else if ("Cancel".equals(status)) {
                    // title = object.getString("title");
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action_Accept");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "Cancel");
                    sendBroadcast(intent1);
                } else if ("Start".equals(status)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "Start");
                    sendBroadcast(intent1);
                } else if ("START".equals(status)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "START");
                    sendBroadcast(intent1);
                } else if ("End".equals(status)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "End");
                    sendBroadcast(intent1);
                } else if ("Arrived".equals(status)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "Arrived");
                    sendBroadcast(intent1);
                } else if ("Accept".equals(status)) {
                    title = "Booking Request accepted by driver";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action_Accept");
                    Log.e("SendData ===== ", object.toString());
                    if (bookType != null && bookType.equals("LATER")) {
                        intent1.putExtra("booktype", "LATER");
                    } else {
                        intent1.putExtra("booktype", "NOW");
                    }
                    intent1.putExtra("request_id", requestId);
                    intent1.putExtra("status", "Accept");
                    sendBroadcast(intent1);
                } else if ("Finish".equals(status)) {
                    title = "New Booking Request";
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action");
                    Log.e("SendData ===== ", object.toString());
                    intent1.putExtra("status", "Arrived");
                    sendBroadcast(intent1);
                }

                sharedPref = SharedPref.getInstance(this);

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    if ("POOL".equals(bookType)) {
                        wakeUpScreen();
                        displayCustomTaxiNotifyForUserPool(status, title, key, object.toString());
                    } else {
                        wakeUpScreen();
                        displayCustomTaxiNotifyForUser(status, title, key, object.toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void displayCustomTaxiNotifyForUserPool(String status, String title, String msg, String data) {

        intent = new Intent(this, HomeAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";

        String message = "";

        if ("Arrived".equals(status)) {
            message = "Driver is Arrived";
        } else if ("Start".equals(status)) {
            message = "Your Trip is started";
        } else if ("End".equals(status)) {
            message = "Trip is Ended";
        } else if ("Finish".equals(status)) {
            message = "Trip is Finish";
        } else {
            message = msg;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private void displayCustomTaxiNotifyForUser(String status, String title, String msg, String data) {

        intent = new Intent(this, HomeAct.class);
        intent.putExtra("type", "dialog");
        intent.putExtra("data", data);
        intent.putExtra("object", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";

        String message = "";

        if ("Arrived".equals(status)) {
            message = "Driver is Arrived";
        } else if ("Start".equals(status)) {
            message = "Your Trip is started";
        } else if ("End".equals(status)) {
            message = "Trip is Ended";
        } else if ("Finish".equals(status)) {
            message = "Trip is Finish";
        } else {
            message = msg;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }


    private void wakeUpScreen() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        Log.e("screen on......", "" + isScreenOn);
        if (isScreenOn == false) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }


}
