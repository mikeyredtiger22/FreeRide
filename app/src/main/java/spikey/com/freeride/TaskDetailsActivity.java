package spikey.com.freeride;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();

    private Task task;
    private int taskColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();

        //Get task data
        Intent intent = getIntent();
        if (intent.hasExtra("task") && intent.hasExtra("color")) {
            taskColor = intent.getIntExtra("color", 0);
            String taskData = intent.getStringExtra("task");
            if (taskData != null) {
                this.task = gson.fromJson(taskData, Task.class);
            }
        }

        //Adds back button to return to previous screen
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(taskColor));
        setTaskInfo();
    }

    private void setTaskInfo() {
        TextView title = findViewById(R.id.task_details_title_info);
        TextView desc = findViewById(R.id.task_details_desc_info);
        TextView incentive = findViewById(R.id.task_details_incentive_info);
        TextView startLocation = findViewById(R.id.task_details_start_location_info);
        TextView startLocationLabel = findViewById(R.id.task_details_start_location);
        TextView endLocation = findViewById(R.id.task_details_end_location_info);
        TextView endLocationLabel = findViewById(R.id.task_details_end_location);
        TextView duration = findViewById(R.id.task_details_duration_info);
        TextView durationLabel = findViewById(R.id.task_details_duration);
        TextView distance = findViewById(R.id.task_details_distance_info);
        TextView distanceLabel = findViewById(R.id.task_details_distance);

        title        .setText(task.getTitle());
        desc         .setText(task.getDescription());
        startLocation.setText(task.getStartAddress());
        incentive    .setText(String.valueOf(task.getIncentive()));

        if (!task.getOneLocation()) {
            endLocation.setText(task.getEndAddress());
            distance   .setText(task.getDirectionsDistance());
            duration   .setText(task.getDirectionsDuration());
        } else {
            startLocationLabel.setText(R.string.location_colon);
            endLocationLabel.setVisibility(View.GONE);
            durationLabel.setVisibility(View.GONE);
            distanceLabel.setVisibility(View.GONE);
        }

    }

}
