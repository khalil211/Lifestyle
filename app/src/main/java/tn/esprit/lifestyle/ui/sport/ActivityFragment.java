package tn.esprit.lifestyle.ui.sport;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.adapters.ActivityWithEntriesAdapter;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ActivityWithEntries;

public class ActivityFragment extends Fragment {
    private LifeStyleDB db;
    private List<ActivityWithEntries> activities;
    private RecyclerView activitiesList;
    private ActivityWithEntriesAdapter adapter;
    private FloatingActionButton addActivityButton;
    private TextView noActivitiesText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        db = LifeStyleDB.getDB(getContext());
        activities = db.activityDao().getActivitiesWithEntries();
        adapter = new ActivityWithEntriesAdapter(getContext(), activities, (view1, position) -> {
            if (view1.getId() == R.id.deleteChip)
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete activity")
                        .setMessage("Do you really want to delete this activity ? All entries related to it will be deleted!")
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            ActivityWithEntries activity = activities.get(position);
                            for (ActivityEntry entry : activity.getEntries())
                                db.activityDao().deleteEntry(entry);
                            db.activityDao().deleteActivity(activity.getActivity());
                            activities.remove(activity);
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
            else {
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromList", true);
                bundle.putSerializable("activity", activities.get(position).getActivity());
                Navigation.findNavController(getView()).navigate(R.id.action_activityFragment_to_singleActivityFragment, bundle);
            }
        });
        activitiesList = view.findViewById(R.id.activitiesList);
        activitiesList.setAdapter(adapter);
        addActivityButton = view.findViewById(R.id.addActivityButton);
        addActivityButton.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromList", true);
            Navigation.findNavController(getView()).navigate(R.id.action_activityFragment_to_singleActivityFragment, bundle);
        });
        noActivitiesText = view.findViewById(R.id.noActivitiesText);
        if (!activities.isEmpty())
            noActivitiesText.setVisibility(View.GONE);
        activitiesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    addActivityButton.hide();
                else
                    addActivityButton.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Navigation.findNavController(getView()).navigate(R.id.sportFragment);
        return super.onOptionsItemSelected(item);
    }
}