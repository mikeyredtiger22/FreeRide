package spikey.com.freeride;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.LocalDateTime;

import java.util.Arrays;
import java.util.Map;

public class DatabaseOperations {

    public enum UserTaskActivity {Accepted, Completed, Cancelled}

    private static final String TAG = DatabaseOperations.class.getSimpleName();

    public static boolean connected;

    private static final DatabaseReference acceptedTasks =
            FirebaseDatabase.getInstance().getReference("acceptedTasks");
    private static final DatabaseReference mDatabaseUserTaskMessages =
            FirebaseDatabase.getInstance().getReference("messages/userTaskInfo");
    private static final DatabaseReference treatmentAll_TasksRef =
            FirebaseDatabase.getInstance().getReference("tasks/treatmentAll");
    private static final DatabaseReference userTaskActivity =
            FirebaseDatabase.getInstance().getReference("userTaskActivity");
    private static final Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();


    public static void getUserAcceptedTask(ValueEventListener userAcceptedTaskListener) {
        Log.d(TAG, "Getting user accepted task.");
        final String userId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase userID token: " + userId);
        if (userId == null) {
            Log.e(TAG, "Firebase get (userId) token returned null");
            return;
        }
        acceptedTasks.child(userId).addListenerForSingleValueEvent(userAcceptedTaskListener);
    }

    public static void openUserAcceptedTask(String taskId, final Context context) {
        treatmentAll_TasksRef.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent currentTaskIntent = new Intent(context, CurrentTaskActivity.class);
                currentTaskIntent.putExtra("task", dataSnapshot.getValue(String.class));
                context.startActivity(currentTaskIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * Store User Task Info in database, to be read by server.
     * Queues all messages, to be easily read at once, more reliable than FCM upstream messaging.
     *
     * @param dataPayload (user task info) data to be stored by database
     * @param taskId of task
     */
    public static void sendUserTaskInfo(Map<String, Object> dataPayload, String taskId) {
        Log.d(TAG, "Sending User Task Info to database for task: " + taskId);
        DatabaseReference newMessageRef = mDatabaseUserTaskMessages.child(taskId).push();
        newMessageRef.setValue(dataPayload, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.d(TAG, "User Task Info added to database SUCCESSFULLY ");
                } else {
                    Log.d(TAG, "Error on adding User Task Info : " + databaseError + ", " + databaseReference);
                }
            }
        });
    }

    /**
     * Attempts to set the user field of task to this user's Id.
     * Uses a transaction (concurrency safe).
     * transaction is recalled until the user field is set by a user.
     *
     * @param taskId for database
     * @param activity to create current task activity intent in success callback and toast message
     */
    public static void secureTask(final String taskId, final Activity activity, final int taskColor) {
        final String userId = FirebaseInstanceId.getInstance().getToken();
        final String acceptedState = "accepted";
        final String availableState = "available";
        final String notifyAvailableState = "notifyavailable";
        final Context context = activity;
        DatabaseReference taskRef = treatmentAll_TasksRef.child(taskId);
        Log.d(TAG, "Securing Task: " + taskId);

        taskRef.runTransaction(new Transaction.Handler() {
            int result;
            final int SUCCESS = 1;
            final int ALREADY_SECURED = 2;
            final int ANOTHER_USER = 3;
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d(TAG, "pre-transaction Data value: " + mutableData);
                String taskJson = mutableData.getValue(String.class);
                if (taskJson == null) {
                    return Transaction.success(mutableData);
                } else {
                    Task task = gson.fromJson(taskJson, Task.class);
                    String user = task.getUser();
                    if (user == null) {
                        result = SUCCESS;
                        task.setUser(userId);
                        task.setState(acceptedState);
                        taskJson = gson.toJson(task);
                        mutableData.setValue(taskJson);
                        return Transaction.success(mutableData);

                    } else if (user.equals(userId)) {
                        result = ALREADY_SECURED;
                        return Transaction.success(mutableData);
                    } else {
                        result = ANOTHER_USER;
                        return Transaction.abort();
                    }
                }
            }


            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "Transaction completed");
                switch (result) {
                    case SUCCESS:
                        Log.d(TAG, "Task secured");
                        CustomToastMessage.show("Task secured", activity);
                        addUserTaskActivity(taskId, UserTaskActivity.Accepted.toString());
                        acceptedTasks.child(userId).setValue(taskId);
                        break;
                    case ALREADY_SECURED:
                        Log.d(TAG, "Task already secured by this user");
                        CustomToastMessage.show("You have already secured this task", activity);
                        break;
                    case ANOTHER_USER:
                        Log.d(TAG, "Task secured by another user");
                        CustomToastMessage.show("Sorry, this task is no longer available", activity);
                        break;

                }
                if (committed) { // = (SUCCESS || ALREADY_SECURED)
                    Intent currentTaskIntent = new Intent(context, CurrentTaskActivity.class);
                    currentTaskIntent.putExtra("task", dataSnapshot.getValue(String.class));
                    currentTaskIntent.putExtra("color", taskColor);
                    context.startActivity(currentTaskIntent);
                }
            }
        });
    }

    public static void cancelTask(Task task) {
        //todo might need to double check that user owns task first
        //reset task
        String[] locationUserData = new String[task.getLocationCount()];
        Boolean[] verified = new Boolean[task.getLocationCount()];
        Arrays.fill(verified, false);

        task.setState("available");
        task.setUser(null);
        task.setLocationUserData(locationUserData);
        task.setLocationVerified(verified);
        String taskJson = gson.toJson(task);

        final String userId = FirebaseInstanceId.getInstance().getToken();
        addUserTaskActivity(task.getTaskId(), UserTaskActivity.Cancelled.toString());
        acceptedTasks.child(userId).removeValue();
        treatmentAll_TasksRef.child(task.getTaskId()).setValue(taskJson);
    }

    public static void addUserLocationData(final String taskId, Integer locationIndex, String data) {
        String activityString = "Verified Location, Index: " + locationIndex + ", Data: " + data;
        addUserTaskActivity(taskId, activityString);
    }

    public static void completeTask(String taskId) {
        addUserTaskActivity(taskId, UserTaskActivity.Completed.toString());
    }

    private static void addUserTaskActivity(final String taskId, String activity) {
        final String userId = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference activityRef = userTaskActivity.child(userId).child(taskId);
        //Firebase doesn't allow a decimal point inside a path name
        String datePath = LocalDateTime.now().toString().replaceAll("\\.", ",");
        activityRef = activityRef.child(gson.toJson(datePath));
        activityRef.setValue(activity, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "Error on setting user task activity. taskId: " + taskId + ", userId: " +
                            userId + " database error: " + databaseError + ", at " + databaseReference);
                }
            }
        });
    }

    public static void updateTask(Task task) {
        Log.d(TAG, "Updating task in database: " + task.getTaskId());
        DatabaseReference taskRef = treatmentAll_TasksRef.child(task.getTaskId());
        String taskJson = gson.toJson(task);
        taskRef.setValue(taskJson, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "Error updating task: " + databaseError + ", " + databaseReference);
                }
            }
        });
    }


    /**
     * Reads value at database path, returns as snapshot to listener
     *
     * @param getAvailableTasksListener given by the calling method
     */
    public static void getAvailableTasks(ValueEventListener getAvailableTasksListener) {
        connectedToDatabase();
        treatmentAll_TasksRef.addListenerForSingleValueEvent(getAvailableTasksListener);
    }

    /**
     * Sends data (userId) to database to check connection / reconnect to database
     */
    public static boolean databaseMessageTest() {
        connectedToDatabase();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                .getReference().child("ConnectTest").push();
        String userId = FirebaseInstanceId.getInstance().getToken();
        databaseRef.child("userId").setValue(userId);
        databaseRef.child("time").setValue(LocalDateTime.now().toString());
        Log.d(TAG, "DB TEST: " + databaseRef);
        return connected;
    }

    /**
     * Database - New task listener
     */
    public static void listen() {
        connectedToDatabase();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tasks/test");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Task newTask = dataSnapshot.getValue(Task.class);
                String taskId = dataSnapshot.getKey();
                String treatment = dataSnapshot.getRef().getParent().getKey();
                Log.d(TAG, "New Task Added To DB, TASK ID:" + taskId);
                //allowing to repeated testing:
                //dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "OnCancelled: " + databaseError);
            }

        });
    }

    /**
     * Attempts to reestablish connection to database
     */
    public static void connectedToDatabase(ValueEventListener connectedListener) {
        DatabaseReference.goOnline();/////////////
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(connectedListener);
    }

    private static boolean connectedToDatabase() {
        DatabaseReference.goOnline();/////////////
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                Log.e(TAG, "Connected = " + connected);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        return connected;
        //TODO wait for callback
        //TODO all methods: if not connected (from before) try to connect and wait for callback
    }
}
