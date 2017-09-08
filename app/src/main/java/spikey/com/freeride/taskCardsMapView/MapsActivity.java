package spikey.com.freeride.taskCardsMapView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

import static android.view.View.LAYER_TYPE_SOFTWARE;

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
                Log.d(TAG, "OnCreate Intent Data: " + allTaskDataString);
                tasks = new Gson().fromJson(allTaskDataString, Task[].class);
                setUpTaskCardsView(tasks);
            }
        }
    }

    private void setUpTaskCardsView(final Task[] tasks) {

        int[] MATERIAL_COLORS = getMyMaterialColors();
//        int[] CREATED_COLORS = createColors(tasks.length);

        RecyclerView tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setHasFixedSize(true);
        tasksRecyclerView.setLayerType(LAYER_TYPE_SOFTWARE, null);

        TaskLayoutManager layoutManager = new TaskLayoutManager(this, tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(layoutManager);

        TaskRecyclerViewAdapter taskAdapter = new TaskRecyclerViewAdapter(tasks, MATERIAL_COLORS, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(tasksRecyclerView);

        TaskIndicatorDecoration taskIndicatorDecoration =
                new TaskIndicatorDecoration(this, tasks, MATERIAL_COLORS);
        tasksRecyclerView.addItemDecoration(taskIndicatorDecoration);


        SwitchCompat markersSwitch = findViewById(R.id.switch_show_all_markers);
        markersSwitch.setOnCheckedChangeListener(taskIndicatorDecoration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        // Map notifies the task indicator decoration class when it is ready,
        // the task indicator is then drawn on top.
        mapFragment.getMapAsync(taskIndicatorDecoration);

    }

    public int[] createColors(int amount) {
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
