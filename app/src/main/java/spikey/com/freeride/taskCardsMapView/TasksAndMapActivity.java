package spikey.com.freeride.taskCardsMapView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

import spikey.com.freeride.CustomToastMessage;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class TasksAndMapActivity extends FragmentActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = TasksAndMapActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }


        Task[] tasks;
        Intent intent = getIntent();
        if (intent.hasExtra("tasks")) {
            String[] allTasksJsonArray = intent.getStringArrayExtra("tasks");
            if (allTasksJsonArray != null) {
                Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
                int taskCount = allTasksJsonArray.length;
                tasks = new Task[taskCount];
                for (int i = 0; i < taskCount; i++) {
                    tasks[i] = gson.fromJson(allTasksJsonArray[i], Task.class);
                }
                setUpTaskCardsView(tasks);
            }
        }
    }

    private void setUpTaskCardsView(Task[] tasks) {

        //Limit to first 30 tasks if too many
        if (tasks.length > 30) {
            CustomToastMessage.show(String.format(getString(R.string.limiting_tasks), tasks.length), this);
            tasks = Arrays.copyOfRange(tasks, 0, 30);
        }

        int[] MATERIAL_COLORS = getMyMaterialColors();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasks_recycler_view);


        //Set up map view, takes a while to start activity
        MapView mapView = new MapView(this, tasks, MATERIAL_COLORS, tasksRecyclerView);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(mapView);


        //Card layout calculations
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float screenDensity = getResources().getDisplayMetrics().density;
        int cardPadding = (int) (0.075 * screenWidth);
        int outerCardPadding = cardPadding - (int) (8 * screenDensity); //negate 8dp margin of card
        int cardWidth = (int) (0.85 * screenWidth);


        //Set up recycler view (and adapter)
        tasksRecyclerView.setPadding(outerCardPadding, 0, outerCardPadding, 0);
        tasksRecyclerView.setHasFixedSize(true); //will change when cards can be added/removed
        TaskRecyclerViewAdapter taskAdapter =
                new TaskRecyclerViewAdapter(tasks, MATERIAL_COLORS, this, cardWidth);
        tasksRecyclerView.setAdapter(taskAdapter);


        //Setup scroll listener and focused task listeners
        TaskScrollListener taskScrollListener = new TaskScrollListener(tasks.length, cardPadding);
        tasksRecyclerView.addOnScrollListener(taskScrollListener);
        taskScrollListener.addFocusedTaskListener(mapView);


        //Start calculations for task indicator drawing
        if (tasks.length > 3) { //don't show task indicator for few items
            TaskIndicatorDecoration taskIndicatorDecoration = new TaskIndicatorDecoration(MATERIAL_COLORS,
                    tasks.length, screenWidth, screenDensity);
            tasksRecyclerView.addItemDecoration(taskIndicatorDecoration);
            taskScrollListener.addFocusedTaskListener(taskIndicatorDecoration);
        }


        tasksRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(tasksRecyclerView);

        SwitchCompat markersSwitch = findViewById(R.id.switch_show_all_markers);
        markersSwitch.setOnCheckedChangeListener(mapView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(context)
                        .setMessage("Location Permission is needed to use this app!").show();
            }
        }
    }

    public int[] createColors(int amount) {
        //create limit to prevent adjacent colours being too similar

        int[] CREATED_COLORS = new int[amount];

        float hue = 0.0f;
        float sat = 0.5f;
        float lum = 0.55f;
        float[] hsl = {hue, sat, lum};
        for (int i=0; i<amount; i++) {
            float newHue = (360f / amount) * i;
            hsl[0] = newHue;
            int col = ColorUtils.HSLToColor(hsl);
            CREATED_COLORS[i] = col;
        }

        return CREATED_COLORS;
    }

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

    public int[] getMaterialLightColors() {
        int[] MATERIAL_LIGHT_COLORS = new int[16];
        MATERIAL_LIGHT_COLORS[0] = getResources().getColor(R.color.taskCardColorLight1);
        MATERIAL_LIGHT_COLORS[1] = getResources().getColor(R.color.taskCardColorLight2);
        MATERIAL_LIGHT_COLORS[2] = getResources().getColor(R.color.taskCardColorLight3);
        MATERIAL_LIGHT_COLORS[3] = getResources().getColor(R.color.taskCardColorLight4);
        MATERIAL_LIGHT_COLORS[4] = getResources().getColor(R.color.taskCardColorLight5);
        MATERIAL_LIGHT_COLORS[5] = getResources().getColor(R.color.taskCardColorLight6);
        MATERIAL_LIGHT_COLORS[6] = getResources().getColor(R.color.taskCardColorLight7);
        MATERIAL_LIGHT_COLORS[7] = getResources().getColor(R.color.taskCardColorLight8);
        MATERIAL_LIGHT_COLORS[8] = getResources().getColor(R.color.taskCardColorLight9);
        MATERIAL_LIGHT_COLORS[9] = getResources().getColor(R.color.taskCardColorLight10);
        MATERIAL_LIGHT_COLORS[10] = getResources().getColor(R.color.taskCardColorLight11);
        MATERIAL_LIGHT_COLORS[11] = getResources().getColor(R.color.taskCardColorLight12);
        MATERIAL_LIGHT_COLORS[12] = getResources().getColor(R.color.taskCardColorLight13);
        MATERIAL_LIGHT_COLORS[13] = getResources().getColor(R.color.taskCardColorLight14);
        MATERIAL_LIGHT_COLORS[14] = getResources().getColor(R.color.taskCardColorLight15);
        MATERIAL_LIGHT_COLORS[15] = getResources().getColor(R.color.taskCardColorLight16);
        return MATERIAL_LIGHT_COLORS;
    }

    public int[] getMaterialDarkColors() {
        int[] MATERIAL_DARK_COLORS = new int[16];
        MATERIAL_DARK_COLORS[0] = getResources().getColor(R.color.taskCardColorDark1);
        MATERIAL_DARK_COLORS[1] = getResources().getColor(R.color.taskCardColorDark2);
        MATERIAL_DARK_COLORS[2] = getResources().getColor(R.color.taskCardColorDark3);
        MATERIAL_DARK_COLORS[3] = getResources().getColor(R.color.taskCardColorDark4);
        MATERIAL_DARK_COLORS[4] = getResources().getColor(R.color.taskCardColorDark5);
        MATERIAL_DARK_COLORS[5] = getResources().getColor(R.color.taskCardColorDark6);
        MATERIAL_DARK_COLORS[6] = getResources().getColor(R.color.taskCardColorDark7);
        MATERIAL_DARK_COLORS[7] = getResources().getColor(R.color.taskCardColorDark8);
        MATERIAL_DARK_COLORS[8] = getResources().getColor(R.color.taskCardColorDark9);
        MATERIAL_DARK_COLORS[9] = getResources().getColor(R.color.taskCardColorDark10);
        MATERIAL_DARK_COLORS[10] = getResources().getColor(R.color.taskCardColorDark11);
        MATERIAL_DARK_COLORS[11] = getResources().getColor(R.color.taskCardColorDark12);
        MATERIAL_DARK_COLORS[12] = getResources().getColor(R.color.taskCardColorDark13);
        MATERIAL_DARK_COLORS[13] = getResources().getColor(R.color.taskCardColorDark14);
        MATERIAL_DARK_COLORS[14] = getResources().getColor(R.color.taskCardColorDark15);
        MATERIAL_DARK_COLORS[15] = getResources().getColor(R.color.taskCardColorDark16);
        return MATERIAL_DARK_COLORS;
    }

    public int[] getMyMaterialColors() {
        int[] MATERIAL_COLORS = new int[16];
        MATERIAL_COLORS[0] = getResources().getColor(R.color.myTaskCardColor1);
        MATERIAL_COLORS[1] = getResources().getColor(R.color.myTaskCardColor2);
        MATERIAL_COLORS[2] = getResources().getColor(R.color.myTaskCardColor3);
        MATERIAL_COLORS[3] = getResources().getColor(R.color.myTaskCardColor4);
        MATERIAL_COLORS[4] = getResources().getColor(R.color.myTaskCardColor5);
        MATERIAL_COLORS[5] = getResources().getColor(R.color.myTaskCardColor6);
        MATERIAL_COLORS[6] = getResources().getColor(R.color.myTaskCardColor7);
        MATERIAL_COLORS[7] = getResources().getColor(R.color.myTaskCardColor8);
        MATERIAL_COLORS[8] = getResources().getColor(R.color.myTaskCardColor9);
        MATERIAL_COLORS[9] = getResources().getColor(R.color.myTaskCardColor10);
        MATERIAL_COLORS[10] = getResources().getColor(R.color.myTaskCardColor11);
        MATERIAL_COLORS[11] = getResources().getColor(R.color.myTaskCardColor12);
        MATERIAL_COLORS[12] = getResources().getColor(R.color.myTaskCardColor13);
        MATERIAL_COLORS[13] = getResources().getColor(R.color.myTaskCardColor14);
        MATERIAL_COLORS[14] = getResources().getColor(R.color.myTaskCardColor15);
        MATERIAL_COLORS[15] = getResources().getColor(R.color.myTaskCardColor16);
        return MATERIAL_COLORS;
    }
}
