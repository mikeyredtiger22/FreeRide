package spikey.com.freeride.taskCardsMapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import spikey.com.freeride.VALUES;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements TaskScrollListener.FocusedTaskListener {

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = VALUES.TASK_CARDS_INDICATOR_HEIGHT_PX;
    private static final int BAR_Y_POS = (BAR_HEIGHT_DEFAULT / 2);

    private final int[] MATERIAL_COLORS;
    private final int taskCount;

    private float drawXStartPos;
    private float drawTotalBarWidth;
    private float drawBarWidth;
    private float drawBarGap;
    private float newHeight;

    private Paint paint;
    private Canvas canvas;

    private int FOCUSED_TASK_POS;

    public TaskIndicatorDecoration(int[] MATERIAL_COLORS, int taskCount,
                                   float screenWidth, float screenDensity) {
        this.paint = new Paint();
        this.newHeight = screenDensity * 5;
        Log.d(TAG, "new height px: " + newHeight);
//        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.paint.setStrokeWidth(newHeight);

        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.FOCUSED_TASK_POS = 0;
        this.taskCount = taskCount;
        calculate(screenWidth, screenDensity);
        //todo use dp for ypos
    }

    private void calculate(float screenWidth, float screenDensity) {
        //Align start of indicators with start of text on card
        float cardLayoutPadding = screenWidth * 0.05f; // card padding from layout manager
        float cardTextPadding = 16 * screenDensity; // text padding from card layout
        float indicatorPadding = cardLayoutPadding + cardTextPadding;

        float totalUseableSpace = screenWidth - (indicatorPadding * 2);
        float toalBarWidth = totalUseableSpace / taskCount;
//        float barDrawWidth = Math.min(400, toalBarWidth * 0.9f); todo and test
        float barWidth = toalBarWidth * 0.9f;
        float xStartPos = indicatorPadding + (barWidth * 0.05f);

        this.drawXStartPos = xStartPos;
        this.drawTotalBarWidth = toalBarWidth;
        this.drawBarWidth = barWidth;


    }

    @Override
    public void focusedTaskChange(int focusedTaskPosition) {
        this.FOCUSED_TASK_POS = focusedTaskPosition;
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
        //todo way to save drawing?
        this.canvas = canvas;

        float drawXPos = drawXStartPos;
        for (int itemPosition = 0; itemPosition < taskCount; itemPosition++) {
            paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
            float height = (itemPosition == FOCUSED_TASK_POS) ? newHeight * 2 : newHeight;
            canvas.drawRect(drawXPos, 0, drawXPos + drawBarWidth, height, paint);
            drawXPos += drawTotalBarWidth;
        }
        //todo item on click listener to re-centre google camera
    }
}
