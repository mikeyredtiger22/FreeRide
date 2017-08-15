package spikey.com.freeride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;

import spikey.com.freeride.cloudmessaging.SendMessageTask;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 7;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPlayServices();
        final FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.subscribeToTopic("client");
        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.execute(1, null, null);
//        mainTextView = (TextView) findViewById(R.id.main_text_view);
//        button = (Button) findViewById(R.id.send_upstream);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SendMessageTask sendMessageTask = new SendMessageTask(1);
//                sendMessageTask.execute(null, null, null);
//            }
//        });
    }

    @Override
    protected void onResume() {
        Log.d("new", "\n");
        super.onResume();
        checkPlayServices();
    }

    //still useful?
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }
}
