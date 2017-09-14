package spikey.com.freeride.taskCardsMapView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import java.util.List;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;
import spikey.com.freeride.directions.DirectionsLoader;

public class MapView implements
        OnMapReadyCallback, //When google map is loaded and ready to be used
        DirectionsLoader.TaskPathLoadedCallback, //When task directions have been loaded
        TaskScrollListener.FocusedTaskListener, //When focused task on screen changes
        CompoundButton.OnCheckedChangeListener, //When 'show all markers' switch is changed
        OnSuccessListener<Location>,  //When users location is returned
        ActivityCompat.OnRequestPermissionsResultCallback { //Requesting users location permission

    private static final String TAG = MapView.class.getSimpleName();

    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 7;
    private final FusedLocationProviderClient location;

    private final int[] MATERIAL_COLORS;

    private final Context context;
    private final RecyclerView recyclerView; //only used to get recycler view height at runtime
    private final Task[] tasks;

    private String[] taskEncodedPaths;
    private boolean showAllMarkers;
    private GoogleMap googleMap;
    private int FOCUSED_TASK_POS;


    public MapView(Context context, Task[] tasks, int[] MATERIAL_COLORS, RecyclerView recyclerView) {
        this.context = context;
        this.location = LocationServices.getFusedLocationProviderClient(context);
        this.tasks = tasks;
        this.taskEncodedPaths = new String[tasks.length];
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.recyclerView = recyclerView;
    }

    @Override
    public void focusedTaskChange(int focusedTaskPosition) {
        this.FOCUSED_TASK_POS = focusedTaskPosition;
        updateMap();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Application needs to show Google logo, terms of service for Google Maps API
        // padding: left, top, right, bottom, top is to show all of marker if at top of screen
        // bottom is because of the task cards at the bottom
        googleMap.setPadding(0, MAP_TOP_PADDING, 0, recyclerView.getHeight());


        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(context, "No Location Permission", Toast.LENGTH_SHORT).show();
        }

        location.getLastLocation().addOnSuccessListener(this);

        updateMap();
    }

    private void updateMap() {
        if (tasks.length == 0) {
            return;
        }
        if (!showAllMarkers) {
            googleMap.clear(); //removes markers and paths from map
        }


        Task selectedTask = tasks[FOCUSED_TASK_POS];
        LatLng startLatLng = new LatLng(selectedTask.getStartLat(), selectedTask.getStartLong());
        LatLng endLatLng = new LatLng(selectedTask.getEndLat(), selectedTask.getEndLong());

        BitmapDescriptor coloredMarker = getColoredMarker();

        googleMap.addMarker(new MarkerOptions()
                .position(startLatLng).icon(coloredMarker).title(context.getString(R.string.start)));
        googleMap.addMarker(new MarkerOptions()
                .position(endLatLng).icon(coloredMarker).title(context.getString(R.string.end)));

        addCurrentTaskDirectionsToMap();

        LatLngBounds markerBounds = LatLngBounds.builder()
                .include(startLatLng).include(endLatLng).build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, MAP_PADDING));
    }

    private BitmapDescriptor getColoredMarker(){
        float[] hsv = new float[3];
        int col = MATERIAL_COLORS[FOCUSED_TASK_POS % 16];
        ColorUtils.colorToHSL(col, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }


    @Override
    public void onPathLoaded(String encodedPath, int taskPosition) {
        taskEncodedPaths[taskPosition] = encodedPath;

        //Directions are for current task on screen, add directly to map
        if (FOCUSED_TASK_POS == taskPosition) {
            addCurrentTaskDirectionsToMap();
        }
    }

    private void addCurrentTaskDirectionsToMap() {
        if (googleMap == null) {
            Log.d(TAG, "Map not ready for directions");
            return;
        }

        String taskEncodedPath = taskEncodedPaths[FOCUSED_TASK_POS];
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
            polylineOptions.color(MATERIAL_COLORS[FOCUSED_TASK_POS % 16]);
            googleMap.addPolyline(polylineOptions);
        }
    }



    /**
     * Show all markers switch listener
     * @param compoundButton switch
     * @param isChecked state
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        showAllMarkers = isChecked;
    }

    /**
     * Callback from getting users location.
     * Gets the last known location of user (usually current location)
     * @param location of user
     */
    @Override
    public void onSuccess(Location location) {
        Log.d(TAG, "User Location callback");
        if (googleMap == null) {
            Log.d(TAG, "Map not ready");
            return;
        }
        if (location == null) {
            Log.d(TAG, "Location null");
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }
}
