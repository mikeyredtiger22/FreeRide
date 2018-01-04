package spikey.com.freeride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import spikey.com.freeride.taskCardsMapView.TasksAndMapActivity;


public class MainActivity extends AppCompatActivity{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Activity activity;
    private Context context;

    private ProgressBar progressCircle;
    private TextView connectedValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPlayServices();
        DatabaseOperations.getUserAcceptedTasks();
        FirebaseMessaging.getInstance().subscribeToTopic("ALL");
        activity = this;
        context = this;

        progressCircle = findViewById(R.id.progress_circle);
        connectedValue = findViewById(R.id.connected_value);

        //TODO why does this return false first few times?
        ValueEventListener connectedListener = new ValueEventListener() {
            boolean connected = false;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    connectedValue.setText(R.string.true_);
                } else {
                    connectedValue.setText(R.string.false_);
                }
                Log.e(TAG, "Connected = " + connected);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        };
        DatabaseOperations.connectedToDatabase(connectedListener);

        final Button buttonDbTest = findViewById(R.id.button_db_connect);
        buttonDbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connected = DatabaseOperations.databaseMessageTest();
                if (connected) {
                    connectedValue.setText(R.string.true_);
                } else {
                    connectedValue.setText(R.string.false_);
                }
            }
        });


        final Button buttonGetTasks = findViewById(R.id.button_get_tasks);
        buttonGetTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCircle.setVisibility(View.VISIBLE);
                DatabaseOperations.getAvailableTasks(new GetAvailableTasksListener());
            }
        });



        //Speeds up the loading of the TasksAndMapActivity by pre-loading assets for the map.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){}
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressCircle.setVisibility(View.INVISIBLE);
        checkPlayServices();
//        DatabaseOperations.connectedToDatabase();
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

    public class GetAvailableTasksListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.hasChildren()) {
                //No tasks received from server
                CustomToastMessage.show("No Tasks Available", activity);
                progressCircle.setVisibility(View.INVISIBLE);
                return;
            }

            //We use an array to store the tasks because it is much easier for the rest of the
            //application to deal with an ordered immutable collection.
            int count = (int) dataSnapshot.getChildrenCount();
            String[] tasksJsonArray = new String[count];
            int index = 0;
            for (DataSnapshot taskData : dataSnapshot.getChildren()) {
                //todo check that no other user is doing this task
                tasksJsonArray[index] = taskData.getValue().toString();
                index++;
            }

            Intent openTasksView = new Intent(context, TasksAndMapActivity.class);
            openTasksView.putExtra("tasks", tasksJsonArray);
            context.startActivity(openTasksView);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "OnCancelled: " + databaseError);
        }
    }
}
