package spikey.com.freeride.cloudmessaging;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SendMessageTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SendMessageTask.class.getSimpleName();
    private static final String fcmProjectSenderId = VALUES.FCM_PROJECT_SENDER_ID;
    private final FirebaseMessaging fm;
    private Map<String, String> dataPayload;
    private String messageId;

    public SendMessageTask(Map<String, String> dataPayload, String messageId) {
        super();
        fm = FirebaseMessaging.getInstance();
        this.dataPayload = dataPayload;
        this.messageId = messageId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        fm.send(new RemoteMessage.Builder(fcmProjectSenderId + "@gcm.googleapis.com")
                .setMessageId(messageId)
                .setData(dataPayload)
                .build());
        Log.d(TAG, "Message sent with data: " + dataPayload);
        return null;
    }
}


