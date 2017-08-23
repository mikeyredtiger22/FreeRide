package spikey.com.freeride;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class TaskIndicatorDecoration extends RecyclerView.ItemDecoration {

    private static final int BAR_HEIGHT_DEFAULT = 30;
    private static final int Y_POS = 30;

    private Paint paint;
    private Canvas canvas;
    private int CURRENT_SELECTED_ITEM_POSITION;
    private int BAR_DEFAULT_COLOR;
    private int BAR_SELECTED_COLOR;

    public TaskIndicatorDecoration(Context context, int BAR_DEFAULT_COLOR, int BAR_SELECTED_COLOR) {
        paint = new Paint();
        paint.setStrokeWidth(BAR_HEIGHT_DEFAULT);
        this.BAR_DEFAULT_COLOR = BAR_DEFAULT_COLOR;
        this.BAR_SELECTED_COLOR = BAR_SELECTED_COLOR;
    }

    public void draw(float startXPos, float width, boolean activeColor) {
        paint.setColor((activeColor ? BAR_SELECTED_COLOR : BAR_DEFAULT_COLOR));
        canvas.drawLine(startXPos, Y_POS, startXPos + width, Y_POS, paint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top += 50;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        this.canvas = canvas;
        int itemCount = parent.getAdapter().getItemCount();
        float layoutPadding = parent.getWidth() * 0.05f;
        float totalUseableSpace = parent.getWidth() - (layoutPadding * 2);
        float toalBarWidth = totalUseableSpace / 7;
//        float barDrawWidth = Math.min(400, toalBarWidth * 0.9f);
        float barDrawWidth = toalBarWidth * 0.9f;
        float barDrawPadding = toalBarWidth * 0.1f;
        float xPos = layoutPadding + barDrawPadding * 0.5f;

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int leftItemPos = layoutManager.findFirstVisibleItemPosition();
        int rightItemPos = layoutManager.findLastVisibleItemPosition();
        Log.d("Selected Item Position", leftItemPos + " : " + rightItemPos);
        if (leftItemPos == rightItemPos) {
            CURRENT_SELECTED_ITEM_POSITION = leftItemPos; //MUST be at least one item
        } else {
            //TODO - big hack - clean up
            int leftDiff = CURRENT_SELECTED_ITEM_POSITION - leftItemPos;
            int rightDiff = CURRENT_SELECTED_ITEM_POSITION - rightItemPos;
            if (leftDiff != 0 && rightDiff != 0){
                int diffAmount = Math.min( Math.abs(leftDiff),  Math.abs(rightDiff));
                if (leftDiff > CURRENT_SELECTED_ITEM_POSITION) {
                    CURRENT_SELECTED_ITEM_POSITION += diffAmount;
                } else {
                    CURRENT_SELECTED_ITEM_POSITION -= diffAmount;
                }
            }
        }

        drawBars(xPos, itemCount, barDrawWidth, barDrawPadding);
    }

    private void drawBars(float xPos, int itemCount, float barDrawWidth, float barDrawPadding) {
        final float totalBarWidth = barDrawWidth + barDrawPadding;

        float start = xPos;

        for (int i=0; i<itemCount; i++) {
            draw(xPos, barDrawWidth, false);
            xPos += totalBarWidth;
        }

        float highlightStart = start + totalBarWidth * CURRENT_SELECTED_ITEM_POSITION;
        draw(highlightStart, barDrawWidth, true);
    }
}
