package tn.esprit.lifestyle.ui.sport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.adapters.ActivityEntryAdapter;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.utils.Statics;

public class SportFragment extends Fragment {
    private LifeStyleDB db;
    private List<ActivityEntry> entries;
    private RecyclerView activityEntriesList;
    private FloatingActionButton addActivityEntryButton;
    private ActivityEntryAdapter adapter;
    private TextView noEntriesText;
    private Spinner filterSpinner;
    private ArrayAdapter<Activity> filterAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_sport, container, false);
        db = LifeStyleDB.getDB(getContext());
        entries = db.activityDao().getAllEntries();
        activityEntriesList = view.findViewById(R.id.activityEntriesList);
        adapter = new ActivityEntryAdapter(getContext(), entries, db.activityDao().getAllActivities(), position -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("entry", entries.get(position));
            Navigation.findNavController(getView()).navigate(R.id.action_sportFragment_to_singleActivityEntryFragment, bundle);
        });
        activityEntriesList.setAdapter(adapter);
        addActivityEntryButton = view.findViewById(R.id.addActivityEntryButton);
        addActivityEntryButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_sportFragment_to_singleActivityEntryFragment);
        });
        noEntriesText = view.findViewById(R.id.noEntriesText);
        if (!entries.isEmpty())
            noEntriesText.setVisibility(View.GONE);
        activityEntriesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    addActivityEntryButton.hide();
                else
                    addActivityEntryButton.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        sharedPreferences = getActivity().getSharedPreferences(Statics.sharedPrefFile, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        filterSpinner = view.findViewById(R.id.filterSpinner);
        List<Activity> activities = db.activityDao().getAllActivities();
        int selectedId = sharedPreferences.getInt(Statics.SharedPreferences.ENTRY_FILTER_ACTIVITY, -1);
        filterAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, activities);
        filterSpinner.setAdapter(filterAdapter);
        Activity placeholder = new Activity();
        placeholder.setName("Show all");
        filterAdapter.insert(placeholder, 0);
        Activity selected = activities.stream().filter(a -> a.getId() == selectedId).findFirst().orElse(null);
        if (selected != null)
            filterSpinner.setSelection(activities.indexOf(selected));
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                entries.clear();
                if (i==0) {
                    entries.addAll(db.activityDao().getAllEntries());
                    sharedPreferencesEditor.putInt(Statics.SharedPreferences.ENTRY_FILTER_ACTIVITY, -1);
                    sharedPreferencesEditor.apply();
                } else {
                    entries.addAll(db.activityDao().getEntriesByActivityId(((Activity) filterSpinner.getSelectedItem()).getId()));
                    sharedPreferencesEditor.putInt(Statics.SharedPreferences.ENTRY_FILTER_ACTIVITY, ((Activity)filterSpinner.getSelectedItem()).getId());
                    sharedPreferencesEditor.apply();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "Show all activities").setIcon(R.drawable.ic_baseline_view_list_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0)
            Navigation.findNavController(getView()).navigate(R.id.action_sportFragment_to_activityFragment);
        return super.onOptionsItemSelected(item);
    }
}