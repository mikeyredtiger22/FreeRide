package spikey.com.freeride.taskCardsMapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import spikey.com.freeride.Task;
import spikey.com.freeride.VALUES;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = VALUES.TASK_CARDS_INDICATOR_HEIGHT_PX;
    private static final int BAR_Y_POS = (BAR_HEIGHT_DEFAULT / 2);
    private static final int MAP_TOP_PADDING = 100;
    private static final int MAP_PADDING = 150;

    private final Task[] tasks;
    private final int BAR_SELECTED_COLOR;
    private final int[] MATERIAL_COLORS;
    private final int TASK_CARDS_LAYOUT_HEIGHT;

    private boolean showAllMarkers;
    private GoogleMap googleMap;

    private Paint paint;
    private Canvas canvas;

    private int CURRENT_SELECTED_ITEM_POSITION;

    public TaskIndicatorDecoration(Task[] tasks, int BAR_SELECTED_COLOR, int[] MATERIAL_COLORS) {
        this.tasks = tasks;
        this.paint = new Paint();
        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.BAR_SELECTED_COLOR = BAR_SELECTED_COLOR;
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.TASK_CARDS_LAYOUT_HEIGHT = 802;
    }

    private void draw(float startXPos, float width, int itemPosition) {
        paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
        if (itemPosition == CURRENT_SELECTED_ITEM_POSITION) {
            paint.setColor(BAR_SELECTED_COLOR);
            //todo cool shadow things
        }
        draw(startXPos, width);
    }


    private void draw(float startXPos, float width) {
        //TODO use DP!
        // int px = parent.getResources().getDisplayMetrics().density * dp;
        // or parent.getResources().getDimensionPixelSize(R.dimen.buttonHeight);
        // or Resources.getSystem().getDisplayMetrics().density
        canvas.drawLine(startXPos, BAR_Y_POS, startXPos + width, BAR_Y_POS, paint);
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
        Log.d(TAG, "MAP READY");
        this.googleMap = googleMap;
        // Application needs to show Google logo, terms of service for Google Maps API
        // padding: left, top, right, bottom, top is to show all of marker if at top of screen
        // bottom is because of the task cards at the bottom
        googleMap.setPadding(0, MAP_TOP_PADDING, 0, TASK_CARDS_LAYOUT_HEIGHT);
        updateMap();
    }

    private void updateMap() {
        Log.d(TAG, "UPDATE MAP");
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

        googleMap.addMarker(new MarkerOptions().position(startLatLng).title(selectedTask.getTitle()));
        googleMap.addMarker(new MarkerOptions().position(endLatLng).title("End"));
        LatLngBounds markerBounds = LatLngBounds.builder()
                .include(startLatLng).include(endLatLng).build();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(markerBounds, MAP_PADDING));
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
}
