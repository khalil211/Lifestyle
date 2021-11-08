package tn.esprit.lifestyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.entities.ActivityWithEntries;

public class ActivityWithEntriesAdapter extends RecyclerView.Adapter<ActivityWithEntriesAdapter.ActivityWithEntriesViewHolder> {
    private Context context;
    private List<ActivityWithEntries> activities;
    private ClickListener clickListener;

    public ActivityWithEntriesAdapter(Context context, List<ActivityWithEntries> activities, ClickListener clickListener) {
        this.context = context;
        this.activities =activities;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ActivityWithEntriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_with_entries, parent,false);
        return new ActivityWithEntriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityWithEntriesViewHolder holder, int position) {
        ActivityWithEntries activity = activities.get(position);
        holder.nameText.setText(activity.getActivity().getName());
        holder.entriesCountText.setText(activity.getEntries().size()+" entries");
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ActivityWithEntriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameText;
        private TextView entriesCountText;
        private Chip editChip;
        private Chip deleteChip;

        public ActivityWithEntriesViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            entriesCountText = itemView.findViewById(R.id.entriesCountText);
            editChip = itemView.findViewById(R.id.editChip);
            editChip.setOnClickListener(this);
            deleteChip = itemView.findViewById(R.id.deleteChip);
            deleteChip.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.clickListener(view, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void clickListener(View view, int position);
    }
}
