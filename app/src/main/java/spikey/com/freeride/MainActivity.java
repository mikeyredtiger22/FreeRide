package spikey.com.freeride;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;

import spikey.com.freeride.taskCardsMapView.TasksAndMapActivity;


public class MainActivity extends AppCompatActivity{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView resultsTextView;
    private Context context;
    private ProgressBar progressCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPlayServices();
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        context = this;

        progressCircle = findViewById(R.id.progress_circle);


        final Button buttonDbTest = findViewById(R.id.button_db_connect);
        buttonDbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.databaseMessageTest();
            }
        });


        final Button buttonGetTasks = findViewById(R.id.button_get_tasks);
        resultsTextView = findViewById(R.id.text_results);
        buttonGetTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressCircle.setVisibility(View.VISIBLE);
                DatabaseOperations.getAvailableTasks(new GetAvailableTasksListener());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressCircle.setVisibility(View.INVISIBLE);
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

    public class GetAvailableTasksListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.hasChildren()) {
                //No tasks received from server
                Toast.makeText(context, "No Tasks Available.", Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
                return;
            }
//                String receivedData = dataSnapshot.getValue().toString();
//                resultsTextView.setText(receivedData);
            //Log.d(TAG, "Get Available tasks: " + receivedData);
            Gson gson = new Gson();
            ArrayList<Object> tasksObjectArray = new ArrayList<>();

            for (DataSnapshot taskData : dataSnapshot.getChildren()) {
                tasksObjectArray.add(taskData.getValue());
            }

            Object[] tasks = tasksObjectArray.toArray();
            String tasksJson = gson.toJson(tasks);
            Intent openTasksView = new Intent(context, TasksAndMapActivity.class);
            openTasksView.putExtra("tasks", tasksJson);
            context.startActivity(openTasksView);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "OnCancelled: " + databaseError);
        }
    }
}
