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
import com.google.android.gms.maps.model.MarkerOptions;

import spikey.com.freeride.Task;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = 30;
    private static final int Y_POS = 30;

    private Paint paint;
    private Canvas canvas;

    private Task[] tasks;
    private boolean mapReady;
    private boolean showAllMarkers;
    private GoogleMap googleMap;

    private int CURRENT_SELECTED_ITEM_POSITION;
    private int BAR_DEFAULT_COLOR;
    private int BAR_SELECTED_COLOR;

    public TaskIndicatorDecoration(Task[] tasks, boolean showAllMarkers,
                                   int BAR_DEFAULT_COLOR, int BAR_SELECTED_COLOR) {
        this.tasks = tasks;
        this.mapReady = false;
//        this.showAllMarkers = showAllMarkers;
        this.paint = new Paint();
        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.BAR_DEFAULT_COLOR = BAR_DEFAULT_COLOR;
        this.BAR_SELECTED_COLOR = BAR_SELECTED_COLOR;
    }

    public void draw(float startXPos, float width, boolean activeColor) {
        paint.setColor((activeColor ? BAR_SELECTED_COLOR : BAR_DEFAULT_COLOR));
        canvas.drawLine(startXPos, Y_POS, startXPos + width, Y_POS, paint);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        this.canvas = canvas;
        int itemCount = parent.getAdapter().getItemCount();
        float layoutPadding = parent.getWidth() * 0.05f;
        float totalUseableSpace = parent.getWidth() - (layoutPadding * 2);
        float toalBarWidth = totalUseableSpace / itemCount;
//        float barDrawWidth = Math.min(400, toalBarWidth * 0.9f);
        float barDrawWidth = toalBarWidth * 0.9f;
        float barDrawPadding = toalBarWidth * 0.1f;
        float xPos = layoutPadding + barDrawPadding * 0.5f;

        //Smaller card width:
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        boolean change = setSelectedItemPosition(layoutManager.findFirstCompletelyVisibleItemPosition());
        if (change) {
            drawBars(xPos, itemCount, barDrawWidth, barDrawPadding);
            updateMap();
        }
        //TODO always UI improvements. find glitches

        /* IF CARD WIDTH = PARENT WIDTH
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int leftItemPos = layoutManager.findFirstVisibleItemPosition();
        int rightItemPos = layoutManager.findLastVisibleItemPosition();
        Log.d("Selected Item Position", leftItemPos + " : " + rightItemPos);

        if (leftItemPos == rightItemPos) {
            // If cards are stationary and centered
            setSelectedItemPosition(leftItemPos);
        } else {
            // If user is scrolling
            int leftDiff = CURRENT_SELECTED_ITEM_POSITION - leftItemPos;
            int rightDiff = CURRENT_SELECTED_ITEM_POSITION - rightItemPos;
            if (leftDiff != 0 && rightDiff != 0){
                // If user scrolls past many cards, indicator must still be updated
                if (leftItemPos > CURRENT_SELECTED_ITEM_POSITION) {
                    setSelectedItemPosition(leftItemPos);
                } else {
                    setSelectedItemPosition(rightDiff);
                }
            }
        }*/

        drawBars(xPos, itemCount, barDrawWidth, barDrawPadding);
    }

    private void drawBars(float xPos, int itemCount, float barDrawWidth, float barDrawPadding) {
        final float totalBarWidth = barDrawWidth + barDrawPadding;

        float start = xPos;

        for (int i=0; i<itemCount; i++) {
            draw(xPos, barDrawWidth, false);
            xPos += totalBarWidth;
        }

        // If layout manager doesn't return item position OR if there are no tasks to be displayed
        if (CURRENT_SELECTED_ITEM_POSITION >= 0) {
            float highlightStart = start + totalBarWidth * CURRENT_SELECTED_ITEM_POSITION;
            draw(highlightStart, barDrawWidth, true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapReady = true;
        updateMap();
    }

    private void updateMap() {
        if (!mapReady || tasks.length == 0) {
            return;
        }
        if (!showAllMarkers) {
            googleMap.clear();
        }
        Task selectedTask = tasks[CURRENT_SELECTED_ITEM_POSITION];
        //todo set limit to title, add ellipses
        LatLng startLatLng = selectedTask.getStartLatLng();
        googleMap.addMarker(new MarkerOptions().position(startLatLng).title(selectedTask.getTitle()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 10f));
        //TODO ANIMATE cos its cool - use bounds
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG, "SWITCH: " + isChecked);
        showAllMarkers = isChecked;
    }

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
