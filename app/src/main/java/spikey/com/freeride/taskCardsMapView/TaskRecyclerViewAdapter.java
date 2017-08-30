package spikey.com.freeride.taskCardsMapView;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import spikey.com.freeride.DatabaseOperations;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();
    private Task[] tasks;
    private int[] MATERIAL_COLORS;

    public TaskRecyclerViewAdapter(Task[] tasks, int[] MATERIAL_COLORS) {
        super();
        this.tasks = tasks;
        this.MATERIAL_COLORS = MATERIAL_COLORS;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final Task task = tasks[position];
        //todo strings to resources when UI finalised
        holder.setCardBackgroundColor(position);
        holder.taskTitleText    .setText(String.format("Title: %s", task.getTitle()));
        holder.taskStartLocText .setText(String.format("Start: ")); //, task.getStartLatLng().toString()));
        holder.taskEndLocText   .setText(String.format("End: "));//,   task.getEndLatLng().toString()));
        holder.taskTimeText     .setText(String.format("Time: "));
        holder.taskDistanceText .setText(String.format("Distance: "));
        holder.taskIncentiveText.setText(String.format("Points: ", task.getIncentive()));
        holder.taskAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.secureTask(task.getTaskId());
                //TODO active task screen
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        CardView taskCardView;
        TextView taskTitleText;
        TextView taskStartLocText;
        TextView taskEndLocText;
        TextView taskTimeText;
        TextView taskDistanceText;
        TextView taskIncentiveText;
        Button taskAcceptButton;

        public TaskViewHolder(View itemView) {
            super(itemView);

            this.taskCardView = itemView.findViewById(R.id.task_card);
            taskTitleText = itemView.findViewById(R.id.task_title);
            taskStartLocText = itemView.findViewById(R.id.task_start_location);
            taskEndLocText = itemView.findViewById(R.id.task_end_location);
            taskTimeText = itemView.findViewById(R.id.task_time);
            taskDistanceText = itemView.findViewById(R.id.task_distance);
            taskIncentiveText = itemView.findViewById(R.id.task_incentive);
            taskAcceptButton = itemView.findViewById(R.id.task_accept_button);
        }

        public void setCardBackgroundColor(int itemPosition) {
            taskCardView.setCardBackgroundColor(MATERIAL_COLORS[itemPosition % 16]);
        }
    }

}
