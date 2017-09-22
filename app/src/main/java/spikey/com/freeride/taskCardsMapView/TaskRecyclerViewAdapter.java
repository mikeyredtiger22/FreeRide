package spikey.com.freeride.taskCardsMapView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spikey.com.freeride.DatabaseOperations;
import spikey.com.freeride.R;
import spikey.com.freeride.Task;
import spikey.com.freeride.TaskDetailsActivity;

public class TaskRecyclerViewAdapter
        extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>{

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();

    private Task[] tasks;
    private int[] MATERIAL_COLORS;
    private Activity activity;
    private int taskCardWidth;

    public TaskRecyclerViewAdapter(Task[] tasks, int[] MATERIAL_COLORS,
                                   Activity activity, int taskCardWidth) {
        super();
        this.tasks = tasks;
        this.MATERIAL_COLORS = MATERIAL_COLORS;
        this.activity = activity;
        this.taskCardWidth = taskCardWidth;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);

        view.getLayoutParams().width = taskCardWidth;

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        final Task task = tasks[position];
        final int taskColor = MATERIAL_COLORS[position % 16];

        holder.taskCardView.setCardBackgroundColor(taskColor);
        holder.taskIncentiveText.setText(String.format("%s %s",
                activity.getString(R.string.points_colon), task.getIncentive()));
        String[] locationAddresses = task.getLocationAddresses();
        holder.cardFirstLine.setText(String.format("%s %s",
                activity.getString(R.string.start_colon), locationAddresses[0]));
        int locationCount = task.getLocationCount();
        if (locationCount > 1 && task.getHasDirections()) {
            holder.cardSecondLine.setText(String.format("%s %s",
                    activity.getString(R.string.end_colon), locationAddresses[locationCount-1]));
            holder.cardThirdLine.setText(String.format("%s %s",
                    activity.getString(R.string.duration_colon), task.getDirectionsDuration()));
        } else {
            holder.cardSecondLine.setText(String.format("%s %s",
                    activity.getString(R.string.title_colon), task.getTitle()));
            holder.cardThirdLine.setText(String.format("%s %s",
                    activity.getString(R.string.desc_colon), task.getDescription()));
        }

        holder.taskAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations.secureTask(task, activity, taskColor);
            }
        });

        holder.taskMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = Converters.registerLocalDateTime(new GsonBuilder()).create();
                Intent openTaskDetails = new Intent(activity, TaskDetailsActivity.class);
                openTaskDetails.putExtra("task", gson.toJson(task));
                openTaskDetails.putExtra("color", taskColor);
                activity.startActivity(openTaskDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        CardView taskCardView;
        TextView taskIncentiveText;
        TextView cardFirstLine;
        TextView cardSecondLine;
        TextView cardThirdLine;
        Button taskAcceptButton;
        Button taskMoreInfoButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskCardView = itemView.findViewById(R.id.task_card);
            taskIncentiveText = itemView.findViewById(R.id.task_incentive);
            cardFirstLine = itemView.findViewById(R.id.card_first_line);
            cardSecondLine = itemView.findViewById(R.id.card_second_line);
            cardThirdLine = itemView.findViewById(R.id.card_third_line);
            taskAcceptButton = itemView.findViewById(R.id.task_accept_button);
            taskMoreInfoButton = itemView.findViewById(R.id.task_more_info_button);
        }
    }

}
