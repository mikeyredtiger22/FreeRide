package spikey.com.freeride.taskCardsMapView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

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
        Intent intent = getIntent();
        if (intent.hasExtra("task") && intent.hasExtra("color")) {
            taskColor = intent.getIntExtra("color", 0);
            String taskData = intent.getStringExtra("task");
            if (taskData != null) {
                this.task = new Gson().fromJson(taskData, Task.class);
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
        TextView time = findViewById(R.id.task_details_time_info);
        TextView distance = findViewById(R.id.task_details_distance_info);
        View colorBlock = findViewById(R.id.task_details_color_block);

        title        .setText(task.getTitle());
        desc         .setText("");
        incentive    .setText(String.valueOf(task.getIncentive()));
        startLocation.setText(String.format("%s, %s", task.getStartLat(), task.getStartLong()));
        endLocation  .setText(String.format("%s, %s", task.getEndLat(), task.getEndLong()));
        time         .setText(task.getCreationLocalDateTime());
        distance     .setText("");
        colorBlock.setBackgroundColor(taskColor);

    }

}
