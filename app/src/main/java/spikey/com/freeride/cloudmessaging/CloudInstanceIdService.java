package spikey.com.freeride.cloudmessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class CloudInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = CloudInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Client token refreshed: " + refreshedToken);
    }
}
