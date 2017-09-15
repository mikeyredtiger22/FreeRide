package spikey.com.freeride.taskCardsMapView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

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

        //getWindow().getDecorView().setBackgroundColor(taskColor);
        setTaskInfo();
    }

    private void setTaskInfo() {
        TextView title = findViewById(R.id.task_details_title_info);
        TextView desc = findViewById(R.id.task_details_desc_info);
        TextView incentive = findViewById(R.id.task_details_incentive_info);
        TextView startLocation = findViewById(R.id.task_details_start_location_info);
        TextView endLocation = findViewById(R.id.task_details_end_location_info);
        TextView duration = findViewById(R.id.task_details_duration_info);
        TextView distance = findViewById(R.id.task_details_distance_info);
        View colorBlock = findViewById(R.id.task_details_color_block);

        title     .setText(task.getTitle());
        desc      .setText(task.getDescription());
        incentive .setText(String.valueOf(task.getIncentive()));
        colorBlock.setBackgroundColor(taskColor);

        if (task.getRouteData() != null) {
            startLocation.setText(task.getRouteData().startAddress);
            endLocation  .setText(task.getRouteData().endAddress);
            distance     .setText(task.getRouteData().distance.humanReadable);
            duration     .setText(task.getRouteData().duration.humanReadable);
        } else {
            startLocation.setText(String.format("Lat,Long: %s, %s", task.getStartLat(), task.getStartLong()));
            endLocation  .setText(String.format("Lat,Long: %s, %s", task.getEndLat(), task.getEndLong()));
        }

    }

}
