package spikey.com.freeride.taskCardsMapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration
        implements TaskScrollListener.FocusedTaskListener {

    private static final String TAG = TaskIndicatorDecoration.class.getSimpleName();

    private final int[] MATERIAL_COLORS;
    private final int taskCount;

    private int FOCUSED_TASK_POS;
    private Paint paint;
    private float drawXStartPos;
    private float drawTotalBarWidth;
    private float drawBarWidth;
    private float barHeight;

    public TaskIndicatorDecoration(int[] MATERIAL_COLORS, int taskCount,
                                   float screenWidth, float screenDensity) {
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.taskCount = taskCount;
        this.FOCUSED_TASK_POS = 0;
        this.paint = new Paint();
        calculate(screenWidth, screenDensity);
    }

    private void calculate(float screenWidth, float screenDensity) {
        //Align start of indicators with start of text on card
        float cardLayoutPadding = screenWidth * 0.075f; // screen to card padding from adapter
        float cardTextPadding = 16 * screenDensity; // card to text padding from layout
        float indicatorPadding = cardLayoutPadding + cardTextPadding;


        //totalBarWidth includes (90%) bar (drawing) and (10%) gap between bars
        //for N items, there will be N bars and N-1 gaps. Which equals N totalBarWidths - 1 gap
        float totalUseableSpace = screenWidth - (indicatorPadding * 2);
        float totalBarWidth = totalUseableSpace / ((float) taskCount - 0.1f);
        float barDrawWidth = totalBarWidth * 0.9f;
        float xStartPos = indicatorPadding;

        this.drawXStartPos = xStartPos;
        this.drawTotalBarWidth = totalBarWidth;
        this.drawBarWidth = barDrawWidth;
        this.barHeight = screenDensity * 5;
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

        float drawXPos = drawXStartPos;
        for (int itemPosition = 0; itemPosition < taskCount; itemPosition++) {
            paint.setColor(MATERIAL_COLORS[itemPosition % 16]);
            float height = (itemPosition == FOCUSED_TASK_POS) ? barHeight * 2 : barHeight;
            canvas.drawRect(drawXPos, 0, drawXPos + drawBarWidth, height, paint);
            drawXPos += drawTotalBarWidth;
        }
    }
}
