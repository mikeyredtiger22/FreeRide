package spikey.com.freeride.taskCardsMapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

import spikey.com.freeride.VALUES;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements TaskScrollListener.FocusedTaskListener {

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private static final int BAR_HEIGHT_DEFAULT = VALUES.TASK_CARDS_INDICATOR_HEIGHT_PX;
    private static final int BAR_Y_POS = (BAR_HEIGHT_DEFAULT / 2);

    private final int[] MATERIAL_COLORS;

    private Paint paint;
    private Canvas canvas;

    private int FOCUSED_TASK_POS;

    public TaskIndicatorDecoration(int[] MATERIAL_COLORS) {
        this.paint = new Paint();
        this.paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.FOCUSED_TASK_POS = 0;
    }

    @Override
    public void focusedTaskChange(int focusedTaskPosition) {
        this.FOCUSED_TASK_POS = focusedTaskPosition;
    }

    private void draw(float startXPos, float width, int itemPosition) {
        //todo clean, hard to read, bar height and ypos change
        if (itemPosition == FOCUSED_TASK_POS) {
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
}
