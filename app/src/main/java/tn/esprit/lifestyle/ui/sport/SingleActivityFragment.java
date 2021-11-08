package tn.esprit.lifestyle.ui.sport;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ActivityType;

public class SingleActivityFragment extends Fragment {
    private LifeStyleDB db;
    private Activity activity;
    private ActivityEntry entry;
    private boolean fromList;
    private EditText nameEdit;
    private RadioButton durationCheck;
    private RadioButton weightCheck;
    private TextView explanationText;
    private FloatingActionButton confirmActivityButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_single_activity, container, false);
        db = LifeStyleDB.getDB(getContext());
        nameEdit = view.findViewById(R.id.nameText);
        durationCheck = view.findViewById(R.id.durationCheck);
        weightCheck = view.findViewById(R.id.weightCheck);
        explanationText = view.findViewById(R.id.explanationText);
        activity = null;
        entry = null;
        if (getArguments() != null) {
            activity = (Activity)getArguments().getSerializable("activity");
            entry = (ActivityEntry) getArguments().getSerializable("entry");
            fromList = getArguments().getBoolean("fromList");
        }
        if (activity != null) {
            nameEdit.setText(activity.getName());
            durationCheck.setChecked(activity.getType() == ActivityType.DURATION);
            weightCheck.setChecked(activity.getType() == ActivityType.WEIGHT);
            setExplanationText();
        } else {
            durationCheck.setChecked(true);
            setExplanationText();
        }
        durationCheck.setOnClickListener(view1 -> setExplanationText());
        weightCheck.setOnClickListener(view1 -> setExplanationText());
        confirmActivityButton = view.findViewById(R.id.confirmActivityButton);
        confirmActivityButton.setOnClickListener(view1 -> {
            String text = nameEdit.getText().toString();
            if (text.isEmpty())
                Toast.makeText(getContext(), "Please enter activity name", Toast.LENGTH_SHORT).show();
            else {
                boolean add = false;
                if (activity == null) {
                    activity = new Activity();
                    add = true;
                }
                activity.setName(text);
                if (durationCheck.isChecked())
                    activity.setType(ActivityType.DURATION);
                else
                    activity.setType(ActivityType.WEIGHT);
                if (add) {
                    int id = (int)db.activityDao().addActivity(activity);
                    activity.setId(id);
                } else
                    db.activityDao().updateActivity(activity);
                if (fromList)
                    Navigation.findNavController(view).navigate(R.id.activityFragment);
                else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("activity", activity);
                    bundle.putSerializable("entry", entry);
                    Navigation.findNavController(view).navigate(R.id.singleActivityEntryFragment, bundle);
                }
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (fromList)
            Navigation.findNavController(getView()).navigate(R.id.activityFragment);
        else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("entry", entry);
            Navigation.findNavController(getView()).navigate(R.id.singleActivityEntryFragment, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setExplanationText() {
        explanationText.setText(durationCheck.isChecked() ? "Duration allow you to set time and distance attributes" : "Weight allow you to set weight, sets and reps attributes");
    }
}