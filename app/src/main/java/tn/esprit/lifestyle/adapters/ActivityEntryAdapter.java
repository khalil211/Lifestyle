package tn.esprit.lifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ActivityType;

public class ActivityEntryAdapter extends RecyclerView.Adapter<ActivityEntryAdapter.ActivityEntryViewHolder> {
    private List<ActivityEntry> entries;
    private List<Activity> activities;
    private Context context;
    private ClickListener clickListener;

    public ActivityEntryAdapter(Context context, List<ActivityEntry> entries, List<Activity> activities, ClickListener clickListener) {
        this.context = context;
        this.entries = entries;
        this.activities = activities;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ActivityEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_entry, parent,false);
        return new ActivityEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityEntryViewHolder holder, int position) {
        ActivityEntry entry = entries.get(position);
        Activity activity = activities.stream().filter(a -> a.getId() == entry.getActivityId()).findFirst().orElse(null);
        holder.activityNameText.setText(activity == null ? "Unknown" : activity.getName());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(entry.getDate()) + System.getProperty("line.separator");
        date += new SimpleDateFormat("HH:mm").format(entry.getDate());
        holder.dateText.setText(date);
        if (activity.getType() == ActivityType.DURATION) {
            holder.setsDistanceText.setText(entry.getDistance() == null ? "undefined" : entry.getDistance()+" km");
            holder.repsTimeText.setText(entry.getTime() == null ? "undefined" : entry.getTime()+" min");
            holder.weightText.setVisibility(View.GONE);
        } else {
            holder.weightText.setText(entry.getWeight() == null ? "undefined" : entry.getWeight()+" kg");
            holder.setsDistanceText.setText(entry.getSets() == null ? "undefined" : entry.getSets()+" sets");
            holder.repsTimeText.setText(entry.getReps() == null ? "undefined" : entry.getReps()+" reps");
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class ActivityEntryViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView activityNameText;
        private TextView weightText;
        private TextView setsDistanceText;
        private TextView repsTimeText;
        private TextView dateText;

        public ActivityEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            activityNameText = itemView.findViewById(R.id.activityNameText);
            activityNameText.setOnLongClickListener(this);
            weightText = itemView.findViewById(R.id.weigthText);
            weightText.setOnLongClickListener(this);
            setsDistanceText = itemView.findViewById(R.id.setsDistanceText);
            setsDistanceText.setOnLongClickListener(this);
            repsTimeText = itemView.findViewById(R.id.repsTimeText);
            repsTimeText.setOnLongClickListener(this);
            dateText = itemView.findViewById(R.id.dateText);
            dateText.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.longClickListener(getAdapterPosition());
            return true;
        }
    }

    public interface ClickListener {
        void longClickListener(int position);
    }
}
