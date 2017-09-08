package spikey.com.freeride.taskCardsMapView;

import android.content.Context;
import android.content.Intent;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.maps.model.DirectionsRoute;

import spikey.com.freeride.DatabaseOperations;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;
import spikey.com.freeride.directions.DirectionsLoader;

public class TaskRecyclerViewAdapter
        extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>
        implements DirectionsLoader.TaskRouteDataLoadedCallback {

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();
    private Task[] tasks;
    private DirectionsRoute[] taskRouteData;
    private int[] MATERIAL_COLORS;
    private Context context;

    public TaskRecyclerViewAdapter(Task[] tasks, int[] MATERIAL_COLORS, Context context) {
        super();
        this.tasks = tasks;
        taskRouteData = new DirectionsRoute[tasks.length];
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.context = context;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final Task task = tasks[position];

        DirectionsRoute taskRoute;
        if ((taskRoute = taskRouteData[position]) != null) {
            holder.taskStartLocText .setText(taskRoute.legs[0].startAddress);
            holder.taskEndLocText   .setText(taskRoute.legs[0].endAddress);
            holder.loadingIcon.setVisibility(View.INVISIBLE);
        }
        //todo strings to resources when UI finalised
        final int color = holder.setCardBackgroundColor(position);
        holder.taskIncentiveText.setText(String.format("Points: %s", task.getIncentive()));

        holder.taskAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.secureTask(task.getTaskId());
                //TODO active task screen
            }
        });

        holder.taskDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO remove from recycler view (without breaking stuff)
                //TODO never let user see this task again - undoable in settings
            }
        });

        holder.taskMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO only after directions data received
                Intent openTaskDetails = new Intent(context, TaskDetailsActivity.class);
                Log.d(TAG, "Opening task details");
                openTaskDetails.putExtra("task", new Gson().toJson(task));
                openTaskDetails.putExtra("color", color);
                context.startActivity(openTaskDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }


    //DIRECTIONS CALLBACK
    @Override
    public void onRouteDataLoaded(DirectionsRoute route, int taskPosition) {
        taskRouteData[taskPosition] = route;
        //TODO reload this task if binded to task card
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        CardView taskCardView;
        TextView taskStartLocText;
        TextView taskEndLocText;
        TextView taskDurationText;
        TextView taskIncentiveText;
        Button taskAcceptButton;
        Button taskDismissButton;
        Button taskMoreInfoButton;
        ProgressBar loadingIcon;

        public TaskViewHolder(View itemView) {
            super(itemView);

            this.taskCardView = itemView.findViewById(R.id.task_card);
            taskStartLocText = itemView.findViewById(R.id.task_start_location);
            taskEndLocText = itemView.findViewById(R.id.task_end_location);
            taskDurationText = itemView.findViewById(R.id.task_duration);
            taskIncentiveText = itemView.findViewById(R.id.task_incentive);
            taskAcceptButton = itemView.findViewById(R.id.task_accept_button);
            taskDismissButton = itemView.findViewById(R.id.task_dismiss_button);
            taskMoreInfoButton = itemView.findViewById(R.id.task_more_info_button);
            loadingIcon = itemView.findViewById(R.id.task_loading_icon);
        }

        public int setCardBackgroundColor(int itemPosition) {
            int color = MATERIAL_COLORS[itemPosition % 16];
            taskCardView.setCardBackgroundColor(color);
            double lum = ColorUtils.calculateLuminance(color);
            taskDurationText.setText("lum: " + lum);
            return color;
        }
    }

}
