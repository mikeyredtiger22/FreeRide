package spikey.com.freeride;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import spikey.com.freeride.taskCardsMapView.TasksAndMapActivity;


public class MainActivity extends AppCompatActivity{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Activity activity;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        context = this;

        FirebaseMessaging.getInstance().subscribeToTopic("ALL");
        checkPlayServices();
        getLocationPermission();

        //Speeds up the loading of the TasksAndMapActivity by pre-loading assets for the map.
        //todo needed?
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

    /*
    Starts up the application. Only called once location permission is given from user.
     */
    private void startUp() {
        DatabaseOperations.getUserAcceptedTask(new UserAcceptedTaskListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        getLocationPermission();
    }

    //todo still useful?
    private void checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }
    }

    public class UserAcceptedTaskListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot acceptedTaskId) {
            Log.d(TAG, "accepted task: " + acceptedTaskId);
            if (acceptedTaskId.getValue() == null) {
                //User has no accepted tasks
                //Open task search activity
                DatabaseOperations.getAvailableTasks(new GetAvailableTasksListener());
            } else {
                //User has accepted tasks
                //Open current task activity
                DatabaseOperations.openUserAcceptedTask(acceptedTaskId.getValue(String.class), context);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "OnCancelled: " + databaseError);
        }
    }

    public class GetAvailableTasksListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.hasChildren()) {
                //No tasks received from server
                CustomToastMessage.show("No Tasks Available", activity);
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

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            //Already have permission
            startUp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                startUp();
            } else {
                //Permission denied
                //Show 'need location' popup and request permission again
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                        .setMessage("Location permission is needed to use this application.\n" +
                                "Your location data never leaves the device.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getLocationPermission();
                            }
                        });
                alertDialogBuilder.show();
            }
        }
    }
}
