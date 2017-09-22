package spikey.com.freeride;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToastMessage{

    public static void show(String message, Activity activity) {
        View toastView = activity.getLayoutInflater().inflate(R.layout.custom_toast_message_layout,
                (ViewGroup) activity.findViewById(R.id.custom_toast_root_view));

        TextView text = toastView.findViewById(R.id.toast_message);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setView(toastView);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
