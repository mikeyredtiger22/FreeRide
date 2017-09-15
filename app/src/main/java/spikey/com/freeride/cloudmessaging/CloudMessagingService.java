package spikey.com.freeride.cloudmessaging;

import android.app.Notification;
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

import java.util.HashMap;
import java.util.Map;

import spikey.com.freeride.DatabaseOperations;
import spikey.com.freeride.R;
import spikey.com.freeride.taskCardsMapView.TasksAndMapActivity;


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
                    //Task task = new Gson().fromJson(messageData.get("task"), Task.class);
                    // (much later) calculate location and Reputation Score
                    String userId = FirebaseInstanceId.getInstance().getToken();
                    Log.d(TAG, "FB token: " + userId);
                    replyToNewTaskMessageViaDatabase(messageData.get("taskId"), userId);
                    break;
                case "new-task-notification":
                    newTaskNotification(remoteMessage.getNotification(),
                            messageData.get("taskData"), messageData.get("taskId"));
                    break;
            }
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

    /**
     * Server and client sending message back and forth, incrementing a counter.
     * Used to test connectivity between server and client, see where messaging breaks
     * and see how long messages take to get sent.
     * The client end sets a limit to the counter value.
     * @param count of received message
     * @param messageId to send message to (server/fcm)
     */
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

    /**
     * DEPRECATED - Not used anymore because of unreliable upstream messaging (from client)
     * Will be useful again if upstream messaging becomes reliable
     * @param locationScore
     * @param reputationScore
     * @param messageId
     */
    @Deprecated
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

    /**
     * Sends User Task Info (Map) to database. Info is stored and read by the server
     * Client adds their location score (depending on task and user location)
     * @param taskId of task
     * @param userId of user
     */
    public void replyToNewTaskMessageViaDatabase(String taskId, String userId) {

        Map<String, Object> dataPayload = new HashMap<>();
        dataPayload.put("messageType", "new-task-reply");
        dataPayload.put("taskId", taskId);
        dataPayload.put("userId", userId);
        dataPayload.put("locationScore", "100");
        dataPayload.put("reputationScore", "100");

        DatabaseOperations.sendUserTaskInfo(dataPayload, taskId);
        Log.d(TAG, "Sent message to database: " + dataPayload);
    }

    /**
     * Creates a notification for the user from the task data received from the server
     * @param notification payload received
     * @param taskData from server
     * @param taskId of task
     */
    private void newTaskNotification(RemoteMessage.Notification notification, String taskData, String taskId) {
        // todo use newer Gson serializer Task newTask = new Gson().fromJson(taskData, Task.class);
        //Log.d(TAG, "Task Object: " + newTask.getTitle() + ", " + newTask.getDescription());
        createNotification(notification, taskData);
        secureTask(taskId);
    }

    /**
     * Attempts to change the user field of a task to this user's Id.
     * @param taskId
     */
    private void secureTask(String taskId) {
        DatabaseOperations.secureTask(taskId);
    }

    /**
     * Create notification from data given from server
     * @param notificationData from server
     * @param taskData from server
     */
    private void createNotification(RemoteMessage.Notification notificationData, String taskData) {
        //TODO customise notification, add intent to open task details
        Intent intent = new Intent(this, TasksAndMapActivity.class);
        intent.putExtra("task", taskData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(this)
                //todo newer Building - learn notification channels
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(notificationData.getTitle()) //use default values if null
                .setContentText(notificationData.getBody())
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

}
