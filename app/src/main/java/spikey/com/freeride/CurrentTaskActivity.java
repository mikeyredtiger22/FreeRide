package spikey.com.freeride;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;

import java.util.List;

import spikey.com.freeride.taskCardsMapView.TaskDetailsActivity;

public class CurrentTaskActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = CurrentTaskActivity.class.getSimpleName();

    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private Task task;
    private int taskColor;
    private CardView currentTaskCardView;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_task);

        getSupportActionBar().hide();

        //Get task data
        Intent intent = getIntent();
        if (intent.hasExtra("task") && intent.hasExtra("color")) {
            Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
            taskColor = intent.getIntExtra("color", 0);
            String taskData = intent.getStringExtra("task");
            task = gson.fromJson(taskData, Task.class);
        }

        currentTaskCardView = findViewById(R.id.current_task_card_view);
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (int) (0.85 * screenWidth);
        currentTaskCardView.getLayoutParams().width = cardWidth;
        currentTaskCardView.setCardBackgroundColor(taskColor);

        TextView taskIncentiveText = currentTaskCardView.findViewById(R.id.current_task_incentive);
        TextView cardFirstLine = currentTaskCardView.findViewById(R.id.current_card_first_line);
        TextView cardSecondLine = currentTaskCardView.findViewById(R.id.current_card_second_line);
        TextView cardThirdLine = currentTaskCardView.findViewById(R.id.current_card_third_line);
        Button taskVerifyButton = currentTaskCardView.findViewById(R.id.current_task_verify_button);
        Button taskCancelButton = currentTaskCardView.findViewById(R.id.current_task_cancel_button);
        Button taskMoreInfoButton = currentTaskCardView.findViewById(R.id.current_task_more_info_button);


        taskIncentiveText.setText(String.format("%s %s",
                getString(R.string.points_colon), task.getIncentive()));
        cardFirstLine.setText(String.format("%s %s",
                getString(R.string.start_colon), task.getStartAddress()));

        if (!task.getOneLocation()) {
            cardSecondLine.setText(String.format("%s %s",
                    getString(R.string.end_colon), task.getEndAddress()));
            cardThirdLine.setText(String.format("%s %s",
                    getString(R.string.duration_colon), task.getDirectionsDuration()));
        } else {
            cardSecondLine.setText(String.format("%s %s",
                    getString(R.string.title_colon), task.getTitle()));
            cardThirdLine.setText(String.format("%s %s",
                    getString(R.string.desc_colon), task.getDescription()));
        }

        final Context context = this;
        taskMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
                Intent openTaskDetails = new Intent(context, TaskDetailsActivity.class);
                openTaskDetails.putExtra("task", gson.toJson(task));
                openTaskDetails.putExtra("color", taskColor);
                context.startActivity(openTaskDetails);
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.current_task_map_view);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Application needs to show Google logo, terms of service for Google Maps API
        // padding: left, top, right, bottom, top is to show all of marker if at top of screen
        // bottom is because of the task cards at the bottom
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) currentTaskCardView.getLayoutParams();
        int height = currentTaskCardView.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
        googleMap.setPadding(0, MAP_TOP_PADDING, 0, height);

        MarkerOptions markerOptions = new MarkerOptions().icon(getColoredMarker());
        LatLng startLatLng = new LatLng(task.getStartLat(), task.getStartLong());

        if (task.getEndLat() != null) {//two location (start and end) task
            googleMap.addMarker(markerOptions.position(startLatLng).title(getString(R.string.start)));
            LatLng endLatLng = new LatLng(task.getEndLat(), task.getEndLong());
            googleMap.addMarker(markerOptions.position(endLatLng).title(getString(R.string.end)));

            addCurrentTaskDirectionsToMap(googleMap);

            LatLngBounds markerBounds = LatLngBounds.builder()
                    .include(startLatLng).include(endLatLng).build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, MAP_PADDING));
        } else {
            googleMap.addMarker(markerOptions.position(startLatLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
        }
    }

    private BitmapDescriptor getColoredMarker(){
        float[] hsv = new float[3];
        ColorUtils.colorToHSL(taskColor, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void addCurrentTaskDirectionsToMap(GoogleMap googleMap) {

        String taskEncodedPath = task.getDirectionsPath();
        if (taskEncodedPath == null) {
            Log.d(TAG, "Selected task directions not loaded.");
            return;
        }
        List<LatLng> points = PolyUtil.decode(taskEncodedPath);

        if (points != null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(points);
            polylineOptions.startCap(new RoundCap());
            polylineOptions.endCap(new RoundCap());

            polylineOptions.width(25);
            polylineOptions.color(Color.BLACK);
            googleMap.addPolyline(polylineOptions);

            polylineOptions.width(15);
            polylineOptions.color(taskColor);
            googleMap.addPolyline(polylineOptions);
        }
    }
}
