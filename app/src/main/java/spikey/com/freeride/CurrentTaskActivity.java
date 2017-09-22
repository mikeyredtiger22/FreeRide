package spikey.com.freeride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class CurrentTaskActivity extends AppCompatActivity
        implements OnMapReadyCallback, OnCompleteListener<Location>{

    private static final String TAG = CurrentTaskActivity.class.getSimpleName();

    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private Task task;
    private int taskColor;
    private CardView currentTaskCardView;
    private FusedLocationProviderClient locationProvider;

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
        String[] locationAddresses = task.getLocationAddresses();
        cardFirstLine.setText(String.format("%s %s",
                getString(R.string.start_colon), locationAddresses[0]));
        int locationCount = task.getLocationCount();
        if (locationCount > 1 && task.getHasDirections()) {
            cardSecondLine.setText(String.format("%s %s",
                    getString(R.string.end_colon), locationAddresses[locationCount-1]));
            cardThirdLine.setText(String.format("%s %s",
                    getString(R.string.duration_colon), task.getDirectionsDuration()));
        } else {
            cardSecondLine.setText(String.format("%s %s",
                    getString(R.string.title_colon), task.getTitle()));
            cardThirdLine.setText(String.format("%s %s",
                    getString(R.string.desc_colon), task.getDescription()));
        }

        final Activity activity = this;
        final Context context = this;
        final OnCompleteListener<Location> getLocation = this;

        taskVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    locationProvider.getLastLocation().addOnCompleteListener(getLocation);

                } else {
                    CustomToastMessage.show("Need Location Permission to verify task", activity);
                    //TODO request user runtime
                }
            }
        });

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

        this.locationProvider = LocationServices.getFusedLocationProviderClient(context);

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

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CustomToastMessage.show("No Location Permission", this);

            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            // todo getLocationPermission();
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        locationProvider.getLastLocation();

        Double[] locationLats = task.getLocationLats();
        Double[] locationLongs = task.getLocationLongs();
        int locationCount = task.getLocationCount();

        MarkerOptions markerOptions = new MarkerOptions().icon(getColoredMarker());
        if (locationCount == 1) {
            //Add single location marker to map
            LatLng locationLatLng = new LatLng(locationLats[0], locationLongs[0]);
            googleMap.addMarker(markerOptions.position(locationLatLng));
            //Animate view to show marker in centre with zoom of 15..?todo
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
        } else if (locationCount > 1) {
            LatLngBounds.Builder markerBoundsBuilder = LatLngBounds.builder();
            //Add all task locations to map
            for (int locationIndex = 0; locationIndex < locationCount; locationIndex++) {
                LatLng locationLatLng = new LatLng(locationLats[locationIndex], locationLongs[locationIndex]);
                googleMap.addMarker(markerOptions.position(locationLatLng));
                markerBoundsBuilder.include(locationLatLng);
            }
            //todo include directions in bounds?
            addCurrentTaskDirectionsToMap(googleMap);
            //Animate google camera to task locations, includes all task location markers
            //in view plus map padding so markers aren't at edges of screen
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    markerBoundsBuilder.build(), MAP_PADDING));
        } else { //should never be true
            Log.d(TAG, "Task has no locations");
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

    @Override
    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> locationTask) {
        if (!locationTask.isSuccessful()) {
            Log.d(TAG, "Task verification error: " + locationTask.getException());
        } else {
            Location location = locationTask.getResult();
            if (location == null) {
                CustomToastMessage.show("Please turn on location to verify task", this);
            } else {
                double accuracyMetres = location.getAccuracy();
                if (accuracyMetres > 50) {
                    CustomToastMessage.show("Your locations accuracy must be less than 50 metres to verify.\n" +
                            "Make sure you are using GPS location", this);
                } else {
                    //todo increment when verified
                    LatLng taskLocation = new LatLng(task.getLocationLats()[0], task.getLocationLongs()[0]);
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    double taskDistanceMetres = SphericalUtil.computeDistanceBetween(
                            taskLocation, userLocation);
                    int difference = (int) (taskDistanceMetres - accuracyMetres);
                    if (difference < 10) {
                        CustomToastMessage.show("LOCATION VERIFIED", this);
                    } else {
                        CustomToastMessage.show(String.format(
                                "You must be %s metres closer to the task location", difference), this);
                    }
                }
            }
        }
    }
}
