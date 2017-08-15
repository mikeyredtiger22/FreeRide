package spikey.com.freeride.cloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import spikey.com.freeride.MainActivity;
import spikey.com.freeride.R;


public class CloudMessagingService extends FirebaseMessagingService {
    private static final String TAG = CloudMessagingService.class.getSimpleName();

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d(TAG, "Sent message, ID: " + s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Log.d(TAG, "Send error: " + s + ",  Error: " + e);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received.");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (Objects.equals(remoteMessage.getData().get("messageType"), "reply-test")) {
                String count = remoteMessage.getData().get("count");
                handleReplyTestMessage(count);
            } else if (remoteMessage.getData().containsKey("task")) {

                //Task task =  TODO GSON
                //TODO not gson anymoooore!
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification body: " + remoteMessage.getNotification().getBody());
            createNotification(remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Bundle bundleExtras = intent.getExtras();
        String extras = "";
        if (bundleExtras != null) {
            extras = bundleExtras.toString();
        }
        Log.d(TAG, "Intent extras: " + extras);
        // Data payload received as intent
        if (intent.hasExtra("count")) {
            String count = intent.getStringExtra("count");
            //handleReplyTestMessage(count);
            //Datapayload message received as message and as intent
        }
    }

    //Sends test message back to server until count reached
    //Testing messaging functionality
    public void handleReplyTestMessage(String count) {
        final int countVal = Integer.parseInt(count) +1;
        if (countVal > 5) {
            return;
        }

        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.execute(countVal, null, null);
    }

    private void createNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                //todo newer Building - learn notification channels
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("New Notification - From the Cloud")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, mNotificationBuilder.build());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

}
