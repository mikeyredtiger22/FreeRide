package spikey.com.freeride.taskCardsMapView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spikey.com.freeride.Task;
import spikey.com.freeride.VALUES;
import spikey.com.freeride.directions.DirectionsLoader;
import spikey.com.freeride.directions.DirectionsLoader2;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener,
        OnSuccessListener<Location>, ActivityCompat.OnRequestPermissionsResultCallback,
        DirectionsLoader.DirectionsResult{

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = VALUES.TASK_CARDS_INDICATOR_HEIGHT_PX;
    private static final int BAR_Y_POS = (BAR_HEIGHT_DEFAULT / 2);
    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 7;
    private final FusedLocationProviderClient location;

    private final int[] MATERIAL_COLORS;
    private final int TASK_CARDS_LAYOUT_HEIGHT;

    private final Context context;
    private final Task[] tasks;
    private List<LatLng>[] taskDirections;

    private boolean showAllMarkers;
    private GoogleMap googleMap;

    private Paint paint;
    private Canvas canvas;

    private int CURRENT_SELECTED_ITEM_POSITION;

    public TaskIndicatorDecoration(Context context, Task[] tasks, int[] MATERIAL_COLORS) {
        this.context = context;
        this.location = LocationServices.getFusedLocationProviderClient(context);
        this.tasks = tasks;
        this.taskDirections = (ArrayList<LatLng>[]) new ArrayList[tasks.length];
        this.paint = new Paint();
        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.TASK_CARDS_LAYOUT_HEIGHT = 802; //todo

        for (int i=tasks.length - 1; i>= 0; i--) {
            loadTaskDirections(i);
        }
    }

    private void draw(float startXPos, float width, int itemPosition) {
        //todo clean, hard to read, bar height and ypos change
        if (itemPosition == CURRENT_SELECTED_ITEM_POSITION) {
            paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
            this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT * 2);

            draw(startXPos, width, true);

            this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        } else {
            paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
            draw(startXPos, width, false);
        }
    }


    private void draw(float startXPos, float width, boolean selected) {
        //TODO use DP!
        // int px = parent.getResources().getDisplayMetrics().density * dp;
        // or parent.getResources().getDimensionPixelSize(R.dimen.buttonHeight);
        // or Resources.getSystem().getDisplayMetrics().density
        int yPos = selected ? BAR_Y_POS * 2 : BAR_Y_POS;
        canvas.drawLine(startXPos, yPos, startXPos + width, yPos, paint);
    }

    /**
     * Called repeatedly while user is scrolling though task cards
     * @param canvas to draw on
     * @param parent recycler view
     * @param state not used
     */
    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        //todo way to save drawing, only redraw/calculate if change in selected card?
        this.canvas = canvas;
        int itemCount = parent.getAdapter().getItemCount();
        float layoutPadding = 0; //parent.getWidth() * 0.05f;
        int cardPadding = 150; //todo - 16dp (task card padding)
        float totalUseableSpace = parent.getWidth() - ((layoutPadding + cardPadding) * 2);
        float toalBarWidth = totalUseableSpace / itemCount;
//        float barDrawWidth = Math.min(400, toalBarWidth * 0.9f);
        float barDrawWidth = toalBarWidth * 0.9f;
        float barDrawGap = toalBarWidth * 0.1f;
        float xPos = layoutPadding + cardPadding + (barDrawGap * 0.5f);


        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        boolean change = setSelectedItemPosition(layoutManager.findFirstCompletelyVisibleItemPosition());
        // The map should not update while the user is scrolling
        if (change) {
//            loadTaskAndNeighbourDirections();
            updateMap();
        }
        drawBars(xPos, itemCount, barDrawWidth, barDrawGap);

        //todo item on click listener to re-centre google camera
    }

    private void drawBars(float xPos, int itemCount, float barDrawWidth, float barDrawGap) {

        final float totalBarWidth = barDrawWidth + barDrawGap;

        for (int itemPosition = 0; itemPosition < itemCount; itemPosition++) {
            draw(xPos, barDrawWidth, itemPosition); //todo deprecated, add theme?
            xPos += totalBarWidth;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Application needs to show Google logo, terms of service for Google Maps API
        // padding: left, top, right, bottom, top is to show all of marker if at top of screen
        // bottom is because of the task cards at the bottom
        googleMap.setPadding(0, MAP_TOP_PADDING, 0, TASK_CARDS_LAYOUT_HEIGHT);


        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(context, "Need Location Permission", Toast.LENGTH_SHORT).show();
            //TODO need location?
        }

        location.getLastLocation().addOnSuccessListener(this);

//        DirectionsApi

        updateMap();
    }

    private void updateMap() {
        if (tasks.length == 0) {
            return;
        }
        if (!showAllMarkers) {
            googleMap.clear(); //removes markers from map
        }


        Task selectedTask = tasks[CURRENT_SELECTED_ITEM_POSITION];
        //todo set limit to title, add ellipses
        LatLng startLatLng = new LatLng(
                selectedTask.getStartLocationLatitude(),
                selectedTask.getStartLocationLongitude());
        LatLng endLatLng = new LatLng(
                selectedTask.getEndLocationLatitude(),
                selectedTask.getEndLocationLongitude());

        BitmapDescriptor markerIcon = getMarkerColor();

        googleMap.addMarker(new MarkerOptions().position(startLatLng)
                .title(selectedTask.getTitle()).icon(markerIcon));
        googleMap.addMarker(new MarkerOptions().position(endLatLng)
                .title("End").icon(markerIcon));

        addCurrentTaskDirectionsToMap();

        LatLngBounds markerBounds = LatLngBounds.builder()
                .include(startLatLng).include(endLatLng).build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, MAP_PADDING));
    }

    private BitmapDescriptor getMarkerColor(){
        float[] hsv = new float[3];
        int col = MATERIAL_COLORS[CURRENT_SELECTED_ITEM_POSITION % 16];
        ColorUtils.colorToHSL(col, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    /**
     * Called repeatedly while user is scrolling through task cards.
     * Task card selected position is only changed if the position is valid and different to the
     * previous position.
     * @param position given from layout manager
     * @return true is selected position changed and is a valid position
     */
    private boolean setSelectedItemPosition(int position) {
        if (position != RecyclerView.NO_POSITION) {
            if (CURRENT_SELECTED_ITEM_POSITION != position) {
                CURRENT_SELECTED_ITEM_POSITION = position;
                return true;
            }
        }
        return false;
    }

    /**
     * TODO This one will require a lot of explaining
     */
    private void loadTaskAndNeighbourDirections() {
        final int position = CURRENT_SELECTED_ITEM_POSITION;
//        Log.d(TAG, "loading neighbours" + position);
        loadTaskDirections(position);
        if (position > 0) {
            loadTaskDirections(position - 1);
        }
        if (position < tasks.length - 1) {
            loadTaskDirections(position + 1);
        }
    }

    private void loadTaskDirections(int taskPosition) {
//        Log.d(TAG, "loading task" + taskPosition);
        if (taskDirections[taskPosition] != null) {
            return;
        }
        Task selectedTask = tasks[taskPosition];
        LatLng startLatLng = new LatLng(
                selectedTask.getStartLocationLatitude(),
                selectedTask.getStartLocationLongitude());
        LatLng endLatLng = new LatLng(
                selectedTask.getEndLocationLatitude(),
                selectedTask.getEndLocationLongitude());

/*        Log.d(TAG, "Adding loader for task: " + taskPosition);
        DirectionsLoader loader = new DirectionsLoader(
                this, taskPosition, startLatLng, endLatLng);
        loader.execute();
        Log.d(TAG, "Started loader for task: " + taskPosition);*/

        DirectionsLoader2 loader = new DirectionsLoader2();
        DirectionsResult results = loader.getDirections(startLatLng, endLatLng);
        Log.d(TAG, taskPosition + " result routes: " + Arrays.toString(results.routes));
        if (results.routes.length == 0) {
            Log.d(TAG, "noice tho");
            return;
        }
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        Log.d(TAG, taskPosition + " decoded poly: " + decodedPath);
        taskDirections[taskPosition] = decodedPath;

        if (taskPosition == CURRENT_SELECTED_ITEM_POSITION && googleMap != null) {
            addCurrentTaskDirectionsToMap();
        }
    }

    @Override
    public void receiveDirectionsResult(ArrayList<LatLng> points, int loaderId) {

        //Each task takes about 1 seconds to receive directions
        if (points == null) {
            Log.d(TAG, "Received empty directions");
            return;
        }

        //Cancel if directions already loaded.
        if (taskDirections[loaderId] != null) {
            Log.d(TAG, "Task Directions already loaded");
            Log.d(TAG, "Prev task directions: " + taskDirections[loaderId]);
            Log.d(TAG, "New task directions: " + points);
            return;
        }

        taskDirections[loaderId] = points;
        Log.d(TAG, "LOADED TASK DIRECTIONS: " + loaderId);

        //Directions are for current task on screen, add directly to map
        if (CURRENT_SELECTED_ITEM_POSITION == loaderId) {
            addCurrentTaskDirectionsToMap();
        }

    }

    private void addCurrentTaskDirectionsToMap() {
        if (googleMap == null) {
            Log.d(TAG, "Map not ready for directions");
            return;
        }

        if (taskDirections[CURRENT_SELECTED_ITEM_POSITION] == null) {
            Log.d(TAG, "Directions not loaded yet");
            return;
        }
        List<LatLng> points = taskDirections[CURRENT_SELECTED_ITEM_POSITION];

        if (points != null) {
            PolylineOptions polylineOptions = new PolylineOptions();

            //todo configure

            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(MATERIAL_COLORS[CURRENT_SELECTED_ITEM_POSITION % 16]);
            polylineOptions.geodesic(true);
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
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Location"));

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
