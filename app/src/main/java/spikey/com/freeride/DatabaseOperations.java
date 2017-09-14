package spikey.com.freeride;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.joda.time.LocalDateTime;

import java.util.Map;

public class DatabaseOperations {

    private static final String TAG = DatabaseOperations.class.getSimpleName();

    private static boolean connected;
    private static final DatabaseReference mDatabaseUserTaskMessages =
            FirebaseDatabase.getInstance().getReference(VALUES.DB_MESSAGES_PATH);
    private static final DatabaseReference mDatabaseTasks =
            FirebaseDatabase.getInstance().getReference(VALUES.TASKS_PATH_DB);

    /**
     * Store User Task Info in database, to be read by server.
     * Queues all messages, to be easily read at once, more reliable than FCM upstream messaging.
     * @param dataPayload (user task info) data to be stored by database
     * @param taskId of task
     */
    public static void sendUserTaskInfo(Map<String, Object> dataPayload, String taskId) {
        if (!connectedToDatabase()) {
            return;
        }
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
     * @param taskId of task to secure
     */
    public static void secureTask(final String taskId) {
        final String userId = FirebaseInstanceId.getInstance().getToken();
        if (!connectedToDatabase()) {
            return;
        }

        DatabaseReference newMessageRef = mDatabaseTasks.child(taskId);
        Log.d(TAG, "Securing Task: " + taskId);

        newMessageRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d(TAG, "pre-transaction Data value: " + mutableData);
                Task task = mutableData.getValue(Task.class);
                if (task == null) {
                    return Transaction.success(mutableData);
                } else {
                    if (task.getUser() == null) {
                        task.setUser(userId);
                        mutableData.setValue(task);
                        Log.d(TAG, "Set user on Task: " + taskId + " set userId: " + userId);
                    } else {
                        Log.d(TAG, "Task: " + taskId + " already taken by user: " + task.getUser());
                    }
                    return Transaction.success(mutableData);
                }
            }


            @Override
            public void onComplete(DatabaseError databaseError, boolean error,
                                   DataSnapshot dataSnapshot) {
                if (error) {
                    Log.d(TAG, "Transaction completed");
                } else {
                    Log.d(TAG, "Error on securing task : " + databaseError);
                }
            }
        });
    }

    /**
     * Returns all available tasks from database as Json tree / string??
     */
    public static void getAvailableTasks(ValueEventListener getAvailableTasksListener) {
        connectedToDatabase();
        DatabaseReference allTasksRef = mDatabaseTasks;
        allTasksRef.addListenerForSingleValueEvent(getAvailableTasksListener);
    }

    /**
     * Sends data (userId) to database to check connection / reconnect to database
     */
    public static void databaseMessageTest() {
        connectedToDatabase();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                .getReference().child("ConnectTest").push();
        String userId = FirebaseInstanceId.getInstance().getToken();
        databaseRef.child("userId").setValue(userId);
        databaseRef.child("time").setValue(LocalDateTime.now().toString());
        Log.d(TAG, "DB TEST: " + databaseRef);
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
     * Prints out and returns connection status (true or false)
     * @return connection status
     */
    public static boolean connectedToDatabase() {
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
    }
}
