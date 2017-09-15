package spikey.com.freeride.taskCardsMapView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class TaskScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = TaskScrollListener.class.getSimpleName();

    private final int LAST_TASK_INDEX;
    private final int CARD_PADDING;
    private int focusedTaskPosition;
    private ArrayList<FocusedTaskListener> focusedTaskListeners;


    public TaskScrollListener(int taskCount, int cardPadding) {
        this.LAST_TASK_INDEX = taskCount - 1;
        focusedTaskListeners = new ArrayList<>();
        this.CARD_PADDING = cardPadding;
    }

    public interface FocusedTaskListener {
        void focusedTaskChange(int focusedTaskPosition);
    }

    public void addFocusedTaskListener(FocusedTaskListener listener) {
        focusedTaskListeners.add(listener);
    }

    //If user scrolls quickly to first or last item, scroll state does not reach idle.
    //This method calculates when the first or last item in the recycler view is centered
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildCount() == 2) {
            View view = recyclerView.getChildAt(0);
            int layoutPos = recyclerView.getChildLayoutPosition(view);
            if (layoutPos == 0) {
                //First item in recycler view
                int left = view.getLeft();
                if (left == CARD_PADDING) {
                    setNewPositionAndNotifyListeners(0);
                }
            } else {
                //Last item in recycler view
                View viewLast = recyclerView.getChildAt(1);
                int left = viewLast.getLeft();
                if (left == CARD_PADDING) {
                    setNewPositionAndNotifyListeners(LAST_TASK_INDEX);
                }
            }
        }


    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int newFocusedTaskPosition = getFocusedTaskPosition(recyclerView);
            if (newFocusedTaskPosition != this.focusedTaskPosition) {
                setNewPositionAndNotifyListeners(newFocusedTaskPosition);
            }
        }
    }

    private void setNewPositionAndNotifyListeners(int newFocusedTaskPosition) {
        this.focusedTaskPosition = newFocusedTaskPosition;
        for (FocusedTaskListener listener : focusedTaskListeners) {
            listener.focusedTaskChange(newFocusedTaskPosition);
        }
    }

    private int getFocusedTaskPosition(RecyclerView recyclerView) {
        // Recycler view displays three cards, we get the centre card and return its layout position.
        if (recyclerView.getChildCount() == 3) {
            View view = recyclerView.getChildAt(1);
            return recyclerView.getChildLayoutPosition(view);
        } else { //count == 2 when the first or last item is focused
            View view = recyclerView.getChildAt(0);
            int layoutPos = recyclerView.getChildLayoutPosition(view);
            if (layoutPos == 0) {
                //First item in recycler view
                return layoutPos;
            } else {
                //Last item in recycler view
                return layoutPos + 1;
            }
        }
    }
}
