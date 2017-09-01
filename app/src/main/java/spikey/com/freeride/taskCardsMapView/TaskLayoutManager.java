package spikey.com.freeride.taskCardsMapView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import spikey.com.freeride.VALUES;

/**
 * Layout Manager padding works differently to setting the padding of the recycler view or its
 * items.
 *
 * TODO calls many times!
 * This means the same calculations will be made once, instead of every time a
 * new item is created or comes into view??
 *
 * The methods overwrite the left, right and top padding. For more information read the method
 * comments.
 *
 * TODO still not sure how padding affects items and layout.. so much work for such little code
 *
 * add super.getXPadding returns 0 for my tests.
 */
public class TaskLayoutManager extends LinearLayoutManager {

    private static final String TAG = TaskLayoutManager.class.getSimpleName();

    private int edgePadding;
    RecyclerView tasksRecyclerView;

    public TaskLayoutManager(Context context, RecyclerView tasksRecyclerView) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
        this.tasksRecyclerView = tasksRecyclerView;
    }

    /**
     * TThis decreases the width of the cards slightly. It also centers the first and
     * last card letting the user see the edges of cards either side of the centre card.
     * @return new Padding
     */
    @Override
    public int getPaddingLeft() {
        return super.getPaddingLeft() + getEdgePadding();
    }

    /**
     * This decreases the width of the cards slightly. It also centers the first and
     * last card letting the user see the edges of cards either side of the centre card.
     * @return new Padding
     */
    @Override
    public int getPaddingRight() {
        return super.getPaddingRight() + getEdgePadding();
    }

    /**
     * This decreases the height of the cards slightly. It also lowers the card by the
     * same amount. This is used so the task indicator can be drawn above the cards.
     * @return new Padding
     */
    @Override
    public int getPaddingTop() {
        return super.getPaddingTop() + VALUES.TASK_CARDS_INDICATOR_HEIGHT_PX;
    }

    /**
     * This calculates the padding for each edge of the recycler view. I want the cards to be
     * 0.9 * parent width, so I add a padding of 0.05 * parent width to each side.
     * todo how it affects cards and such. not the same as recycler view padding
     * @return padding for each edge
     */
    private int getEdgePadding() {
        if (edgePadding <= 0) {
            edgePadding = (int) (tasksRecyclerView.getWidth() * 0.05);
        }
        return edgePadding;
    }
}
