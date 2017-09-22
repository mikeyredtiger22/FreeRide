package spikey.com.freeride;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();

    private Task task;
    ArrayList<String> labels;
    ArrayList<String> taskInfo;
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
        //todo set nav bar color
        //todo change toolbar color programmatically

        labels = new ArrayList<>();
        taskInfo = new ArrayList<>();
        setTaskInfo();

        final LayoutInflater layoutInflater = getLayoutInflater();
        RecyclerView recyclerView = findViewById(R.id.task_details_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TaskDetailsViewHolder(
                        layoutInflater.inflate(R.layout.task_details_layout_item, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                TaskDetailsViewHolder viewHolder = (TaskDetailsViewHolder) holder;
                viewHolder.label.setText(labels.get(position));
                viewHolder.info.setText(taskInfo.get(position));
                viewHolder.info.setSelected(true); //this allows the text to scroll when overflowing
            }

            @Override
            public int getItemCount() {
                return taskInfo.size();
            }
        });
    }

    private void setTaskInfo() {
        //Populate arrays with task info and labels
        addTaskInfo("Title", task.getTitle());
        addTaskInfo("Description", task.getDescription());
        addTaskInfo("Points", task.getIncentive().toString());
        if (task.getHasDirections()) {
            addTaskInfo("Distance", task.getDirectionsDistance());
            addTaskInfo("Duration", task.getDirectionsDuration());
        }
        addTaskInfo("Tasks:", "");
        String[] taskAddresses = task.getLocationAddresses();
        String[] taskInstructions = task.getLocationInstructions();
        for (Integer locationIndex = 0; locationIndex < task.getLocationCount(); locationIndex++) {
            String label = task.getAreLocationsOrdered() ? locationIndex + "." : "\u2022\u25E6";
            addTaskInfo(label, taskAddresses[locationIndex]);
            addTaskInfo("", taskInstructions[locationIndex]);
        }
    }

    private void addTaskInfo(String label, String taskDetail) {
        labels.add(label);
        taskInfo.add(taskDetail);
    }

    class TaskDetailsViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public TextView info;

        public TaskDetailsViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.task_details_label);
            info = itemView.findViewById(R.id.task_details_info);
        }
    }

}
