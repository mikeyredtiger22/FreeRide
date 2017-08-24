package spikey.com.freeride.taskCardsMapView;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class MapsActivity extends FragmentActivity {


    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Task[] tasks = null;
        Intent intent = getIntent();
        if (intent.hasExtra("tasks")) {
            String taskData = intent.getStringExtra("tasks");
            if (taskData != null) {
                Log.d("INTENT DATA", taskData);
                tasks = new Gson().fromJson(taskData, Task[].class);
            }
        }
        RecyclerView tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        tasksRecyclerView.setLayoutManager(layoutManager);

        TaskRecyclerViewAdapter taskAdapter = new TaskRecyclerViewAdapter(tasks);
        tasksRecyclerView.setAdapter(taskAdapter);

        PagerSnapHelper PsnapHelper = new PagerSnapHelper();
        PsnapHelper.attachToRecyclerView(tasksRecyclerView);


        SwitchCompat markersSwitch = findViewById(R.id.switch_show_all_markers);

        TaskIndicatorDecoration taskIndicatorDecoration = new TaskIndicatorDecoration(
                tasks, markersSwitch.isChecked(), getResources().getColor(R.color.colorBlueDark),
                getResources().getColor(R.color.colorAccent));
        tasksRecyclerView.addItemDecoration(taskIndicatorDecoration);

        markersSwitch.setOnCheckedChangeListener(taskIndicatorDecoration);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(taskIndicatorDecoration);

    }
}
