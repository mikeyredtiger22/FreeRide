package spikey.com.freeride.taskCardsMapView;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener{

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = 20;
    private static final int Y_POS = 20; //Must be half bar height or greater

    private Paint paint;
    private Canvas canvas;

    private Task[] tasks;
    private boolean showAllMarkers;
    private GoogleMap googleMap;

    private int CURRENT_SELECTED_ITEM_POSITION;
    private int BAR_DEFAULT_COLOR;
    private int BAR_SELECTED_COLOR;
    private int[] MATERIAL_COLORS;

    public TaskIndicatorDecoration(Task[] tasks, boolean showAllMarkers,
                                   Resources res, int[] MATERIAL_COLORS) {
        this.tasks = tasks;
        this.showAllMarkers = showAllMarkers; //if default is changed in layout
        this.paint = new Paint();
        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.BAR_DEFAULT_COLOR = res.getColor(R.color.colorBlueDark);
        this.BAR_SELECTED_COLOR = res.getColor(R.color.taskCardColorSELECTED);
        this.MATERIAL_COLORS = MATERIAL_COLORS;
    }

    private void draw(float startXPos, float width, int itemPosition) {
        paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
        if (itemPosition == CURRENT_SELECTED_ITEM_POSITION) {
            paint.setColor(BAR_SELECTED_COLOR);
            //Cool shadow things
        }
        draw(startXPos, width);
    }


    private void draw(float startXPos, float width) {
        //TODO use DP!
        // int px = parent.getResources().getDisplayMetrics().density * dp;
        // or parent.getResources().getDimensionPixelSize(R.dimen.buttonHeight);
        // or Resources.getSystem().getDisplayMetrics().density
        canvas.drawLine(startXPos, Y_POS, startXPos + width, Y_POS, paint);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
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

        float start = xPos;

        for (int itemPosition = 0; itemPosition < itemCount; itemPosition++) {
            draw(xPos, barDrawWidth, itemPosition); //todo deprecated, add theme?
            xPos += totalBarWidth;
        }

        // If layout manager doesn't return item position OR if there are no tasks to be displayed
        if (CURRENT_SELECTED_ITEM_POSITION >= 0) {
            float highlightStart = start + totalBarWidth * CURRENT_SELECTED_ITEM_POSITION;
            //draw(highlightStart, barDrawWidth, true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        updateMap();
    }

    private void updateMap() {
        if (tasks.length == 0) {
            return;
        }
        if (!showAllMarkers) {
            googleMap.clear();
        }
        Task selectedTask = tasks[CURRENT_SELECTED_ITEM_POSITION];
        //todo set limit to title, add ellipses
//        LatLng startLatLng = selectedTask.getStartLatLng();
        LatLng startLatLng = new LatLng(
                selectedTask.getStartLocationLatitude(),
                selectedTask.getStartLocationLongitude());
        LatLng endLatLng = new LatLng(
                selectedTask.getEndLocationLatitude(),
                selectedTask.getEndLocationLongitude());
        googleMap.addMarker(new MarkerOptions().position(startLatLng).title(selectedTask.getTitle()));
        googleMap.addMarker(new MarkerOptions().position(endLatLng).title("End"));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 10f));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 10f));
        //todo use bounds with directions
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        showAllMarkers = isChecked;
    }

    /**
     *
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
