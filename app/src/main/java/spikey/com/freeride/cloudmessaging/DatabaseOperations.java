package spikey.com.freeride.cloudmessaging;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class DatabaseOperations {

    private static final DatabaseReference mDatabase =
            FirebaseDatabase.getInstance().getReference(VALUES.DB_MESSAGES_PATH);


    public static void addMessage(Map<String, String> dataPayload) {
        DatabaseReference newMessageRef = mDatabase.push();
        newMessageRef.setValue(dataPayload);
        Log.d("DATABASE", dataPayload.toString());
    }


}
