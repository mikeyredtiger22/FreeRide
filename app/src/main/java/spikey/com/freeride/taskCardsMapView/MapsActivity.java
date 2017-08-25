package spikey.com.freeride.taskCardsMapView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

        TaskLayoutManager layoutManager = new TaskLayoutManager(this, 50, tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(layoutManager);

        TaskRecyclerViewAdapter taskAdapter = new TaskRecyclerViewAdapter(tasks);
        tasksRecyclerView.setAdapter(taskAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(tasksRecyclerView);


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

//        int edgePadding = (int) (tasksRecyclerView.getMeasuredWidth() * 0.05);
//        int lastPosition = tasksRecyclerView.getChildCount() - 1;
//        //tasksRecyclerView.getChildAt(0).setPadding(edgePadding, 0, 0, 0);
//        //tasksRecyclerView.getChildAt(lastPosition).setPadding(0, 0, edgePadding, 0);
//        Log.d(TAG, "edge Padding: " + edgePadding);
//        Log.d(TAG, "last Pos: " + lastPosition);
    }

    public class TaskLayoutManager extends LinearLayoutManager {

        private int parentWidth;
        RecyclerView tasksRecyclerView;

        public TaskLayoutManager(Context context, int parentWidth, RecyclerView tasksRecyclerView) {
            super(context, LinearLayoutManager.HORIZONTAL, false);
            this.parentWidth = parentWidth;
            this.tasksRecyclerView = tasksRecyclerView;
        }

        public void setParentWidth(int parentWidth) {
            this.parentWidth = parentWidth;
        }

        @Override
        public int getPaddingLeft() {
            //Used to center first and last item in recycler view.
            //This method is called very little compared to item decoration
            parentWidth = tasksRecyclerView.getMeasuredWidth();
            int padding = (int) (parentWidth * 0.05);
            Log.d(TAG, "LM RV width" + parentWidth);
            return super.getPaddingLeft() + padding;// - Math.round((parentWidth - itemWidth) / 2);
            //return Math.round(mParentWidth / 2f - mItemWidth / 2f);
        }

        @Override
        public int getPaddingRight() {
            parentWidth = tasksRecyclerView.getMeasuredWidth();
            int padding = (int) (parentWidth * 0.05);
            Log.d(TAG, "LM RV width" + parentWidth);
            return super.getPaddingRight() + padding;
        }

        @Override
        public int getPaddingTop() {
            //Used
            return super.getPaddingTop() + 50;
        }
    }
}
