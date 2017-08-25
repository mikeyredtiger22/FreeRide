package spikey.com.freeride.taskCardsMapView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();
    private Task[] tasks;
    private int edgePadding;
    private int itemCount;

    public TaskRecyclerViewAdapter(Task[] tasks) {
        super();
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);

        //height - 50 for drawing of task card indicator. See TaskIndicatorDecoration.java class.
        int height = parent.getMeasuredHeight() -50;
        //todo calculate outside if possible
        int width = (int) (parent.getMeasuredWidth() * 0.90); //TODO change for larger screens
        edgePadding = (int) (parent.getMeasuredWidth() * 0.05);
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks[position];
        //todo android not happy with these set texts
        holder.taskTitleText.setText("Title: " + task.getTitle());
        holder.taskStartLocText.setText("Start: " + task.getStartLatLng().toString());
        holder.taskEndLocText.setText("End: " + task.getEndLatLng().toString());
        String test = "";
        for (int i=0; i<position; i++) {
            test += "hello hello hello.";
        }
        holder.taskTimeText.setText("Desc: " + test);
        holder.taskDistanceText.setText("Distance: ");
        if (position % 2 == 0) {
            holder.taskIncentiveText.setText("\uD83D\uDCB5  \uD83D\uDCB0: £" + task.getIncentive());
        } else {
            holder.taskIncentiveText.setText("I");
        }
        Log.d(TAG, "Binding View Holder");
        if (position == 0) {
//            holder.setLeftPadding(edgePadding);
        } else if (position == tasks.length - 1) {
//            holder.setRightPadding(edgePadding);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView taskTitleText;
        TextView taskStartLocText;
        TextView taskEndLocText;
        TextView taskTimeText;
        TextView taskDistanceText;
        TextView taskIncentiveText;

        public TaskViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            taskTitleText = itemView.findViewById(R.id.task_title);
            taskStartLocText = itemView.findViewById(R.id.task_start_location);
            taskEndLocText = itemView.findViewById(R.id.task_end_location);
            taskTimeText = itemView.findViewById(R.id.task_time);
            taskDistanceText = itemView.findViewById(R.id.task_distance);
            taskIncentiveText = itemView.findViewById(R.id.task_incentive);
        }

        public void setLeftPadding(int padding) {
            itemView.setPadding(padding, itemView.getPaddingTop(),
                    itemView.getPaddingRight(), itemView.getPaddingBottom());
            Log.d(TAG, "paddings: " + itemView.getPaddingRight() + ", " + itemView.getPaddingLeft());
        }

        public void setRightPadding(int padding) {
            itemView.setPadding(itemView.getPaddingLeft(), itemView.getPaddingTop(),
                    padding, itemView.getPaddingBottom());
        }
    }

}
