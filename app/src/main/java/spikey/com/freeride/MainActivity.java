package spikey.com.freeride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 7;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPlayServices();
        final FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.subscribeToTopic("test");
        //SendMessageTask sendMessageTask = new SendMessageTask();
        //sendMessageTask.execute(1, null, null);

//        mainTextView = (TextView) findViewById(R.id.main_text_view);
//        button = (Button) findViewById(R.id.send_upstream);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SendMessageTask sendMessageTask = new SendMessageTask(1);
//                sendMessageTask.execute(null, null, null);
//            }
//        });
        final Button buttonDbTest = findViewById(R.id.db_test);
        buttonDbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.databaseMessageTest();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, " New Instance App Resume \n");
        super.onResume();
        checkPlayServices();
        DatabaseOperations.connectedToDatabase();
        DatabaseReference.goOnline();////////////////
        //DatabaseOperations.listen();
        //DatabaseOperations.databaseMessageTest();
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
