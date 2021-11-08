package tn.esprit.lifestyle.ui.sport;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ActivityType;

public class SingleActivityEntryFragment extends Fragment {
    private LifeStyleDB db;
    private List<Activity> activities;
    private ActivityEntry entry;
    private Spinner activitySpinner;
    ArrayAdapter<Activity> spinnerAdapter;
    private Button addActivityButton;
    private FloatingActionButton confirmEntryButton;
    private TextView weightDistanceText;
    private EditText weightDistanceEdit;
    private TextView weightDistanceUnitText;
    private TextView timeText;
    private EditText timeEdit;
    private TextView setsText;
    private EditText setsEdit;
    private TextView repsText;
    private EditText repsEdit;
    private TextView timeUnitText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_single_activity_entry, container, false);
        db = LifeStyleDB.getDB(getContext());
        activities = db.activityDao().getAllActivities();
        activitySpinner = view.findViewById(R.id.activitySpinner);
        addActivityButton = view.findViewById(R.id.addActivityButton);
        weightDistanceText = view.findViewById(R.id.weightDistanceText);
        weightDistanceEdit = view.findViewById(R.id.weightDistanceEdit);
        weightDistanceUnitText = view.findViewById(R.id.weightDistanceUnitText);
        confirmEntryButton = view.findViewById(R.id.confirmEntryButton);
        timeUnitText = view.findViewById(R.id.timeUnitText);
        timeText = view.findViewById(R.id.timeText);
        timeEdit = view.findViewById(R.id.timeEdit);
        setsText = view.findViewById(R.id.setsText);
        setsEdit = view.findViewById(R.id.setsEdit);
        repsText = view.findViewById(R.id.repsText);
        repsEdit = view.findViewById(R.id.repsEdit);
        addActivityButton.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromList", false);
            if (entry != null) {
                bundle.putSerializable("entry", entry);
                Navigation.findNavController(view).navigate(R.id.action_singleActivityEntryFragment_to_singleActivityFragment, bundle);
            } else
                Navigation.findNavController(view).navigate(R.id.action_singleActivityEntryFragment_to_singleActivityFragment);
        });
        spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, activities);
        activitySpinner.setAdapter(spinnerAdapter);
        Activity placeholder = new Activity();
        placeholder.setName("Choose an activity");
        spinnerAdapter.insert(placeholder, 0);
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateDisplay(i == 0 ? null : activities.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        entry = null;
        Activity activity = null;
        if (getArguments() != null) {
            entry = (ActivityEntry) getArguments().getSerializable("entry");
            activity = (Activity) getArguments().getSerializable("activity");
        }
        if (entry != null) {
            if (activity == null)
                activity = activities.stream().filter(a -> a.getId() == entry.getActivityId()).findFirst().orElse(null);
            displayEntry(entry, activity);
        }
        final Activity labmda = activity;
        if (activity != null)
            activitySpinner.setSelection(activities.indexOf(activities.stream().filter(a -> a.getId() == labmda.getId()).findFirst().get()));
        updateDisplay(activity);
        confirmEntryButton.setOnClickListener(view1 -> {
            boolean goodValues = true;
            if (activitySpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Select an activity", Toast.LENGTH_SHORT).show();
                goodValues = false;
            }
            else if (((Activity)activitySpinner.getSelectedItem()).getType() == ActivityType.DURATION) {
                if (weightDistanceEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter distance", Toast.LENGTH_SHORT).show();
                    goodValues = false;
                } else if(timeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter time", Toast.LENGTH_SHORT).show();
                    goodValues = false;
                }
            } else {
                if (weightDistanceEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter weight", Toast.LENGTH_SHORT).show();
                    goodValues = false;
                } else if (setsEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter number of sets", Toast.LENGTH_SHORT).show();
                    goodValues = false;
                } else if (repsEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter number of reps", Toast.LENGTH_SHORT).show();
                    goodValues = false;
                }
            }
            if (goodValues) {
                boolean add = false;
                if (entry == null) {
                    entry = new ActivityEntry();
                    add = true;
                }
                entry.setActivityId(((Activity)activitySpinner.getSelectedItem()).getId());
                if (((Activity)activitySpinner.getSelectedItem()).getType() == ActivityType.DURATION) {
                    entry.setDistance(Double.parseDouble(weightDistanceEdit.getText().toString()));
                    entry.setTime(Integer.parseInt(timeEdit.getText().toString()));
                    entry.setWeight(null);
                    entry.setSets(null);
                    entry.setReps(null);
                } else {
                    entry.setDistance(null);
                    entry.setTime(null);
                    entry.setWeight(Double.parseDouble(weightDistanceEdit.getText().toString()));
                    entry.setSets(Integer.parseInt(setsEdit.getText().toString()));
                    entry.setReps(Integer.parseInt(repsEdit.getText().toString()));
                }
                if (add) {
                    entry.setDate(new Date());
                    db.activityDao().addEntry(entry);
                } else
                    db.activityDao().updateEntry(entry);
                Navigation.findNavController(getView()).navigate(R.id.sportFragment);
            }

        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (entry != null)
            menu.add(0, 0, 0, "Delete").setIcon(R.drawable.ic_baseline_delete_outline).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete entry")
                    .setMessage("Do you really want to delete this entry ?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        db.activityDao().deleteEntry(entry);
                        Navigation.findNavController(getView()).navigate(R.id.sportFragment);
                    })
                    .setNegativeButton("Cancel", null)
                    .create().show();
            return true;
        }
        Navigation.findNavController(getView()).navigate(R.id.sportFragment);
        return true;
    }

    private void updateDisplay(Activity activity) {
        weightDistanceText.setVisibility(activity == null ? View.GONE : View.VISIBLE);
        weightDistanceEdit.setVisibility(activity == null ? View.GONE : View.VISIBLE);
        weightDistanceUnitText.setVisibility(activity == null ? View.GONE : View.VISIBLE);
        if (activity == null) {
            timeText.setVisibility(View.GONE);
            timeEdit.setVisibility(View.GONE);
            timeUnitText.setVisibility(View.GONE);
            setsText.setVisibility(View.GONE);
            setsEdit.setVisibility(View.GONE);
            repsText.setVisibility(View.GONE);
            repsEdit.setVisibility(View.GONE);
        } else {
            System.out.println(activity);
            timeText.setVisibility(activity.getType() == ActivityType.DURATION ? View.VISIBLE : View.GONE);
            timeEdit.setVisibility(activity.getType() == ActivityType.DURATION ? View.VISIBLE : View.GONE);
            timeUnitText.setVisibility(activity.getType() == ActivityType.DURATION ? View.VISIBLE : View.GONE);
            setsText.setVisibility(activity.getType() == ActivityType.WEIGHT ? View.VISIBLE : View.GONE);
            setsEdit.setVisibility(activity.getType() == ActivityType.WEIGHT ? View.VISIBLE : View.GONE);
            repsText.setVisibility(activity.getType() == ActivityType.WEIGHT ? View.VISIBLE : View.GONE);
            repsEdit.setVisibility(activity.getType() == ActivityType.WEIGHT ? View.VISIBLE : View.GONE);
            weightDistanceText.setText(activity.getType() == ActivityType.DURATION ? "Distance" : "Weight");
            weightDistanceUnitText.setText(activity.getType() == ActivityType.DURATION ? "km" : "kg");
        }
    }

    private void displayEntry(ActivityEntry entry, Activity activity) {
        weightDistanceEdit.setText(activity.getType() == ActivityType.DURATION ? Double.toString(entry.getDistance() == null ? 0 : entry.getDistance()) : Double.toString(entry.getWeight() == null ? 0 : entry.getWeight()));
        if (activity.getType() == ActivityType.DURATION) {
            timeEdit.setText(Integer.toString(entry.getTime() == null ? 0 : entry.getTime()));
        } else {
            setsEdit.setText(Integer.toString(entry.getSets() == null ? 0 : entry.getSets()));
            repsEdit.setText(Integer.toString(entry.getReps() == null ? 0 : entry.getReps()));
        }
    }
}