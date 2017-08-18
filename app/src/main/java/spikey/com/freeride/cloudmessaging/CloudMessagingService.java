package spikey.com.freeride.cloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import spikey.com.freeride.MainActivity;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;


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
        Map<String, String> messageData = remoteMessage.getData();
        if (messageData.size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String messageType = messageData.get("messageType");
            switch (messageType) {
                case "reply-test":
                    String count = messageData.get("count");
                    replyToTestMessage(count, remoteMessage.getMessageId());
                    break;
                case "new-task":
                    //String jsonTask = messageData.get("task");
                    //Task task = new Gson().fromJson(jsonTask, Task.class);

                    /*TODO (much later) calculate location and Reputation Score
                    using messageData.get("task")
                    Can store Rep on database, and only give location*/
                    //replyToNewTaskMessage("100", "100", remoteMessage.getMessageId());
                    String userId = FirebaseInstanceId.getInstance().getToken(); //todo clean
                    Log.d(TAG, "FB token: " + userId);
                    replyToNewTaskMessageViaDatabase(messageData.get("taskId"), userId);
                    break;
                case "new-task-notification":
                    newTaskNotification(remoteMessage.getNotification(),
                            messageData.get("taskData"), messageData.get("taskId"));
                    break;
            }

            // Check if message contains a notification payload.
//            if (remoteMessage.getNotification() != null) {
//                Log.d(TAG, "Message notification body: " + remoteMessage.getNotification().getBody());
//                createNotification(remoteMessage.getNotification().getBody());
//            }
        }
    }


    //All message types received as intent (after OnMessageReceived - if called)
    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        /*
        Bundle bundleExtras = intent.getExtras();
        String extras = "";
        if (bundleExtras != null) {
            extras = bundleExtras.toString();
        }
        Log.d(TAG, "Intent extras: " + extras);*/
    }

    //Sends test message back to server until count reached
    //Testing messaging functionality
    public void replyToTestMessage(String count, String messageId) {
        final int countVal = Integer.parseInt(count) +1;
        if (countVal > 8) {
            return;
        }
        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("messageType", "reply-test");
        dataPayload.put("count", String.valueOf(countVal));

        SendMessageTask sendMessageTask = new SendMessageTask(dataPayload, messageId);
        sendMessageTask.execute(null, null, null);
    }

    //Sends test message back to server until count reached
    //Testing messaging functionality
    public void replyToNewTaskMessage(String locationScore, String reputationScore, String messageId) {

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("messageType", "new-task-reply");
        dataPayload.put("locationScore", locationScore);
        dataPayload.put("reputationScore", reputationScore);

        //VERY UNRELIABLE UPSTREAM MESSAGING.
        SendMessageTask sendMessageTask = new SendMessageTask(dataPayload, messageId);
        sendMessageTask.execute(null, null, null);
        Log.d(TAG, "Sent user data reply to new task message");
    }

    //Sends test message back to server until count reached
    //Testing messaging functionality
    public void replyToNewTaskMessageViaDatabase(String taskId, String userId) {

        Map<String, Object> dataPayload = new HashMap<>();
        dataPayload.put("messageType", "new-task-reply");
        dataPayload.put("taskId", taskId); //todo remove after checking
        dataPayload.put("userId", userId);
        dataPayload.put("locationScore", "100");
        dataPayload.put("reputationScore", "100");

        DatabaseOperations.sendUserTaskInfo(dataPayload, taskId);
        Log.d(TAG, "Sent message to database: " + dataPayload);
    }

    private void newTaskNotification(RemoteMessage.Notification notification, String taskData, String taskId) {
        Task newTask = new Gson().fromJson(taskData, Task.class);
        Log.d(TAG, "Task Object: " + newTask.getTitle() + ", " + newTask.getDescription());
        createNotification(notification);
        ////////////
        secureTask(taskId);
    }

    private void secureTask(String taskId) {
        String userId = FirebaseInstanceId.getInstance().getToken();
        DatabaseOperations.secureTask(taskId, userId);
    }

    private void createNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                //todo newer Building - learn notification channels
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(notification.getTitle()) //use default values if null
                .setContentText(notification.getBody())
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
