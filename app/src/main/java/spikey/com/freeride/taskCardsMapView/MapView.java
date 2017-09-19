package spikey.com.freeride.taskCardsMapView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;

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

import spikey.com.freeride.CustomToastMessage;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class MapView implements
        OnMapReadyCallback, //When google map is loaded and ready to be used
        TaskScrollListener.FocusedTaskListener, //When focused task on screen changes
        CompoundButton.OnCheckedChangeListener, //When 'show all markers' switch is changed
        OnSuccessListener<Location> { //When users location is returned

    private static final String TAG = MapView.class.getSimpleName();

    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 7;
    private final FusedLocationProviderClient locationProvider;

    private final int[] MATERIAL_COLORS;

    private final Activity activity;
    private final Context context;
    private final RecyclerView recyclerView; //only used to get recycler view height at runtime
    private final Task[] tasks;

    private boolean showAllMarkers;
    private GoogleMap googleMap;
    private int FOCUSED_TASK_POS;


    public MapView(Activity activity, Task[] tasks, int[] MATERIAL_COLORS, RecyclerView recyclerView) {
        this.activity = activity;
        this.context = activity;
        this.locationProvider = LocationServices.getFusedLocationProviderClient(context);
        this.tasks = tasks;
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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CustomToastMessage.show("No Location Permission", activity);
            /*
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocationPermission();
        }
             */
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        locationProvider.getLastLocation().addOnSuccessListener(this);

        updateMap();
    }

    private void updateMap() {
        if (tasks.length == 0 || googleMap == null) {
            return;
        }
        if (!showAllMarkers) {
            googleMap.clear(); //removes markers and paths from map
        }

        Task selectedTask = tasks[FOCUSED_TASK_POS];
        MarkerOptions markerOptions = new MarkerOptions().icon(getColoredMarker());
        LatLng startLatLng = new LatLng(selectedTask.getStartLat(), selectedTask.getStartLong());

        if (selectedTask.getEndLat() != null) {//two locationProvider (start and end) task
            googleMap.addMarker(markerOptions.position(startLatLng).title(context.getString(R.string.start)));
            LatLng endLatLng = new LatLng(selectedTask.getEndLat(), selectedTask.getEndLong());
            googleMap.addMarker(markerOptions.position(endLatLng).title(context.getString(R.string.end)));

            addCurrentTaskDirectionsToMap();

            LatLngBounds markerBounds = LatLngBounds.builder()
                    .include(startLatLng).include(endLatLng).build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, MAP_PADDING));
        } else {
            googleMap.addMarker(markerOptions.position(startLatLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
        }
    }

    private BitmapDescriptor getColoredMarker(){
        float[] hsv = new float[3];
        int col = MATERIAL_COLORS[FOCUSED_TASK_POS % 16];
        ColorUtils.colorToHSL(col, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void addCurrentTaskDirectionsToMap() {
        if (googleMap == null) {
            Log.d(TAG, "Map not ready for directions");
            return;
        }

        String taskEncodedPath = tasks[FOCUSED_TASK_POS].getDirectionsPath();
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
}
