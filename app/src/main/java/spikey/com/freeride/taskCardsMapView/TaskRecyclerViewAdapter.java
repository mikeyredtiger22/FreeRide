package spikey.com.freeride.taskCardsMapView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.maps.model.DirectionsLeg;

import spikey.com.freeride.DatabaseOperations;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;
import spikey.com.freeride.directions.DirectionsLoader;

public class TaskRecyclerViewAdapter
        extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>
        implements DirectionsLoader.TaskRouteDataLoadedCallback {

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();
    private Task[] tasks;
    private DirectionsLeg[] allTasksRouteData;
    private boolean[] taskRouteLoaded;
    private int[] MATERIAL_COLORS;
    private Context context;

    public TaskRecyclerViewAdapter(Task[] tasks, int[] MATERIAL_COLORS, Context context) {
        super();
        this.tasks = tasks;
        allTasksRouteData = new DirectionsLeg[tasks.length];
        taskRouteLoaded = new boolean[tasks.length]; //all array elements are false when created
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.context = context;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        final Task task = tasks[position];
        final int taskColor = MATERIAL_COLORS[position % 16];

        holder.taskCardView.setCardBackgroundColor(taskColor);
        holder.taskIncentiveText.setText(String.format("%s %s",
                context.getString(R.string.points_colon), task.getIncentive()));

        if (taskRouteLoaded[position]) {
            //Task directions have been loaded
            holder.loadingIcon.setVisibility(View.INVISIBLE);
            DirectionsLeg tasksRouteData = allTasksRouteData[position];
            if (tasksRouteData != null) {
                holder.taskStartLocText.setText(String.format("%s %s",
                        context.getString(R.string.start_colon), tasksRouteData.startAddress));
                holder.taskEndLocText.setText(String.format("%s %s",
                        context.getString(R.string.end_colon), tasksRouteData.endAddress));
                holder.taskDurationText.setText(String.format("%s %s",
                        context.getString(R.string.duration_colon), tasksRouteData.duration.humanReadable));
            } else {
                //No directions returned from loader
                holder.taskStartLocText.setText(R.string.directions_cant_be_loaded);
            }
        } else {
            holder.taskStartLocText.setText(R.string.loading_directions);
        }

        holder.taskAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.secureTask(task.getTaskId());
                //confirmation -> active task screen
            }
        });

        holder.taskDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        holder.taskMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                Intent openTaskDetails = new Intent(context, TaskDetailsActivity.class);
                openTaskDetails.putExtra("task", gson.toJson(task));
                openTaskDetails.putExtra("color", taskColor);
                if (taskRouteLoaded[holder.getLayoutPosition()]) {
                    String routeDataJson = gson.toJson(allTasksRouteData[holder.getLayoutPosition()]);
                    openTaskDetails.putExtra("routeData", routeDataJson);
                }
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
    public void onRouteDataLoaded(DirectionsLeg route, int taskPosition) {
        allTasksRouteData[taskPosition] = route;
        taskRouteLoaded[taskPosition] = true;
        notifyItemChanged(taskPosition);
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
    }

}
