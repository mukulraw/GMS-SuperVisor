package morgantech.com.gms.FCMs;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;

import morgantech.com.gms.Interfaces.Updatemsg;
import morgantech.com.gms.Messaging;
import morgantech.com.gms.MessagingList;
import morgantech.com.gms.R;
import morgantech.com.gms.Utils.Prefs;

/**
 * Created by Administrator on 30-01-2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    static Updatemsg updateOtp;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Create and show notification

        Prefs prefs = new Prefs();
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activitys = activityManager
                .getRunningTasks(Integer.MAX_VALUE);

        if (activitys.get(0).topActivity
                .toString()
                .equalsIgnoreCase(
                        "ComponentInfo{morgantech.com.gms/morgantech.com.gms.MessagingList}")) {
            if (prefs.getPreferencesString(this, "incident_id").equals(remoteMessage.getNotification().getTag())) {
                updateOtp.update();
            } else {
                sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTag());
            }

        } else {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTag());
        }
    }

    private void sendNotification(String messageBody, String title, String tag) {
        Intent intent = new Intent(this, MessagingList.class);
        intent.putExtra("incident_id", tag);
        intent.putExtra("subject", title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.guardit)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(id, notificationBuilder.build());
        //   notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void bindListener(Updatemsg listener) {
        updateOtp = listener;
    }
}
