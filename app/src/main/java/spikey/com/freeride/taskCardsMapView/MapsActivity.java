package spikey.com.freeride.taskCardsMapView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

import java.util.Arrays;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Task[] tasks;
        Intent intent = getIntent();
        if (intent.hasExtra("tasks")) {
            String allTaskDataString = intent.getStringExtra("tasks");
            if (allTaskDataString != null) {
                Log.d("INTENT DATA", allTaskDataString);
                tasks = new Gson().fromJson(allTaskDataString, Task[].class);
                Log.d(TAG, Arrays.toString(tasks));
                setUpTaskCardsView(tasks);
            }
        }
    }

    private void setUpTaskCardsView(final Task[] tasks) {


        int[] MATERIAL_COLORS = getMaterialColors();

        final RecyclerView tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setHasFixedSize(true);

        TaskLayoutManager layoutManager = new TaskLayoutManager(this, tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(layoutManager);

        final TaskRecyclerViewAdapter taskAdapter = new TaskRecyclerViewAdapter(tasks, MATERIAL_COLORS);
        tasksRecyclerView.setAdapter(taskAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(tasksRecyclerView);

        SwitchCompat markersSwitch = findViewById(R.id.switch_show_all_markers);

        TaskIndicatorDecoration taskIndicatorDecoration = new TaskIndicatorDecoration(
                tasks, markersSwitch.isChecked(), getResources(), MATERIAL_COLORS);
        tasksRecyclerView.addItemDecoration(taskIndicatorDecoration);

        markersSwitch.setOnCheckedChangeListener(taskIndicatorDecoration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        // Map notifies the task indicator decoration class when it is ready, the task indicator
        // is then drawn on top.
        mapFragment.getMapAsync(taskIndicatorDecoration);
    }

    //todo put this somewhere else
    public int[] getMaterialColors() {
        int[] MATERIAL_COLORS = new int[16];
        MATERIAL_COLORS[0] = getResources().getColor(R.color.taskCardColor1);
        MATERIAL_COLORS[1] = getResources().getColor(R.color.taskCardColor2);
        MATERIAL_COLORS[2] = getResources().getColor(R.color.taskCardColor3);
        MATERIAL_COLORS[3] = getResources().getColor(R.color.taskCardColor4);
        MATERIAL_COLORS[4] = getResources().getColor(R.color.taskCardColor5);
        MATERIAL_COLORS[5] = getResources().getColor(R.color.taskCardColor6);
        MATERIAL_COLORS[6] = getResources().getColor(R.color.taskCardColor7);
        MATERIAL_COLORS[7] = getResources().getColor(R.color.taskCardColor8);
        MATERIAL_COLORS[8] = getResources().getColor(R.color.taskCardColor9);
        MATERIAL_COLORS[9] = getResources().getColor(R.color.taskCardColor10);
        MATERIAL_COLORS[10] = getResources().getColor(R.color.taskCardColor11);
        MATERIAL_COLORS[11] = getResources().getColor(R.color.taskCardColor12);
        MATERIAL_COLORS[12] = getResources().getColor(R.color.taskCardColor13);
        MATERIAL_COLORS[13] = getResources().getColor(R.color.taskCardColor14);
        MATERIAL_COLORS[14] = getResources().getColor(R.color.taskCardColor15);
        MATERIAL_COLORS[15] = getResources().getColor(R.color.taskCardColor16);
        return MATERIAL_COLORS;
    }
}
