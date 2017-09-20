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
    public void onMessageSent(String messageId) {
        super.onMessageSent(messageId);
        Log.d(TAG, "Sent message, ID: " + messageId);
    }

    @Override
    public void onSendError(String messageId, Exception exception) {
        super.onSendError(messageId, exception);
        Log.d(TAG, "Send error: " + messageId + ",  Error: " + exception);
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
                case "new-task":
                    // (much later) calculate location and Reputation Score
                    replyToNewTaskMessageViaDatabase(messageData.get("taskId"));
                    break;
                case "new-task-notification":
                    newTaskNotification(remoteMessage.getNotification(),
                            messageData.get("taskData"), messageData.get("taskId"));
                    break;
            }
        }
    }

    /**
     * Sends User Task Info (Map) to database. Info is stored and read by the server
     * Client adds their location score (depending on task and user location)
     * @param taskId of task
     */
    public void replyToNewTaskMessageViaDatabase(String taskId) {
        String userId = FirebaseInstanceId.getInstance().getToken();

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
        createNotification(notification, taskData);
    }

    /**
     * Create notification from data given from server
     * @param notificationData from server
     * @param taskData from server
     */
    private void createNotification(RemoteMessage.Notification notificationData, String taskData) {


        // todo use newer Gson serializer Task newTask = new Gson().fromJson(taskData, Task.class);

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
