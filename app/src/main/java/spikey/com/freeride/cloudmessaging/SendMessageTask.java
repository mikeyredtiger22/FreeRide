package spikey.com.freeride.cloudmessaging;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class SendMessageTask extends AsyncTask<Integer, Void, Void> {

    private static final String TAG = SendMessageTask.class.getSimpleName();
    private static final String fcmProjectSenderId = VALUES.FCM_PROJECT_SENDER_ID;
    private final FirebaseMessaging fm;

    public SendMessageTask() {
        super();
        fm = FirebaseMessaging.getInstance();

    }

    @Override
    protected Void doInBackground(Integer... params) {
        int count = params[0];
        String id = "1001";
        fm.send(new RemoteMessage.Builder(fcmProjectSenderId + "@gcm.googleapis.com")
                .setMessageId(id)
                .addData("count", String.valueOf(count))
                .addData("messageType", "reply-test")
                .build());
        Log.d(TAG, "Sent reply test with count: " + count);
        return null;
    }
}


