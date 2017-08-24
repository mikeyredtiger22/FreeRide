package spikey.com.freeride.taskCardsMapView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import spikey.com.freeride.R;
import spikey.com.freeride.Task;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG = TaskRecyclerViewAdapter.class.getSimpleName();
    private Task[] tasks;

    public TaskRecyclerViewAdapter(Task[] tasks) {
        super();
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_view, parent, false);
        //todo set view layout properties
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks[position];
        holder.taskTitleText.setText(task.getTitle());

    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        //TODO textfield for each field in task class
        TextView taskTitleText;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitleText = itemView.findViewById(R.id.task_title);
        }
    }

}
