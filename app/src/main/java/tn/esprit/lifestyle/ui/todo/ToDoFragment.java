package tn.esprit.lifestyle.ui.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.adapters.ToDoAdapter;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Reminder;
import tn.esprit.lifestyle.entities.ToDo;
import tn.esprit.lifestyle.utils.Statics;

public class ToDoFragment extends Fragment {

    private LifeStyleDB db;
    private List<ToDo> allToDos;
    private List<ToDo> displayedTodos;
    private RecyclerView toDoList;
    private ToDoAdapter adapter;
    private FloatingActionButton addToDoButton;
    private TextView noToDosText;
    private ChipGroup chipGroup;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private HashMap<String, Chip> chips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        db = LifeStyleDB.getDB(this.getContext());
        toDoList = view.findViewById(R.id.toDoList);
        allToDos = db.toDoDao().getAll();
        displayedTodos = new ArrayList<>();
        if (!allToDos.isEmpty()) {
            noToDosText = view.findViewById(R.id.noToDosText);
            noToDosText.setVisibility(View.GONE);
        }
        adapter = new ToDoAdapter(displayedTodos, this.getContext(), new ToDoAdapter.ClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                ToDo toDo = displayedTodos.get(position);
                toDo.setDone(!toDo.isDone());
                db.toDoDao().update(toDo);
                updateToDosAndChipsDisplay();
            }

            @Override
            public void onItemLongClickListener(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("toDo", displayedTodos.get(position));
                Navigation.findNavController(getView()).navigate(R.id.singleToDoFragment, bundle);
            }
        });
        toDoList.setAdapter(adapter);
        addToDoButton = view.findViewById(R.id.addToDoButton);
        addToDoButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_toDoFragment_to_singleToDoFragment));;
        toDoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    addToDoButton.hide();
                else
                    addToDoButton.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chipGroup = view.findViewById(R.id.chipGroup);
        sharedPreferences = getActivity().getSharedPreferences(Statics.sharedPrefFile, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        chips = new HashMap<>();
        chips.put(Statics.SharedPreferences.TODO_FILTER_DONE, view.findViewById(R.id.doneFilterChip));
        chips.put(Statics.SharedPreferences.TODO_FILTER_UNDONE, view.findViewById(R.id.undoneFilterChip));
        chips.put(Statics.SharedPreferences.TODO_FILTER_NO_REMINDER, view.findViewById(R.id.noReminderFilterChip));
        chips.put(Statics.SharedPreferences.TODO_FILTER_ONE_TIME, view.findViewById(R.id.oneTimeFilterChip));
        chips.put(Statics.SharedPreferences.TODO_FILTER_REPEATED, view.findViewById(R.id.repeatedFilterChip));
        chips.put(Statics.SharedPreferences.TODO_SORT_CREATION_DATE, view.findViewById(R.id.creationSortChip));
        chips.put(Statics.SharedPreferences.TODO_SORT_REMINDER_DATE, view.findViewById(R.id.reminderSortChip));
        for (Map.Entry<String, Chip> e : chips.entrySet())
            e.getValue().setOnClickListener(view1 -> {
                boolean old = sharedPreferences.getBoolean(e.getKey(), false);
                sharedPreferencesEditor.putBoolean(e.getKey(), !old);
                e.getValue().setVisibility(old ? View .GONE : View.VISIBLE);
                sharedPreferencesEditor.apply();
                updateToDosAndChipsDisplay();
            });
        checkSharedPrefs();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 2, 0, "Delete done").setIcon(R.drawable.ic_baseline_delete_outline).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 0, 0, "Filter").setIcon(R.drawable.ic_baseline_filter_alt_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 0, "Sort").setIcon(R.drawable.ic_baseline_sort_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                boolean[] checked = new boolean[] {
                        sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_DONE, false),
                        sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_UNDONE, false),
                        sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_NO_REMINDER, false),
                        sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_ONE_TIME, false),
                        sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_REPEATED, false)
                };
                new AlertDialog.Builder(getContext())
                        .setMultiChoiceItems(new CharSequence[]{"Done", "Undone", "No reminder", "One time", "Repeated"}, checked, (dialogInterface, i, b) -> checked[i]=b)
                        .setTitle("Show")
                        .setPositiveButton("Filter", (dialogInterface, i) -> {
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_FILTER_DONE, checked[0]);
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_FILTER_UNDONE, checked[1]);
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_FILTER_NO_REMINDER, checked[2]);
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_FILTER_ONE_TIME, checked[3]);
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_FILTER_REPEATED, checked[4]);
                            sharedPreferencesEditor.apply();
                            updateToDosAndChipsDisplay();
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
                break;
            case 1:
                AtomicInteger choice = new AtomicInteger();
                choice.set(sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_SORT_REMINDER_DATE, false) ? 1 : 0);
                new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(new CharSequence[]{"Creation date", "Reminder"}, sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_SORT_REMINDER_DATE, false) ? 1 : 0, (dialogInterface, i) -> choice.set(i))
                        .setTitle("Sort by")
                        .setPositiveButton("Sort", (dialogInterface, i) -> {
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_SORT_REMINDER_DATE, choice.intValue()==1);
                            sharedPreferencesEditor.putBoolean(Statics.SharedPreferences.TODO_SORT_CREATION_DATE, choice.intValue()==0);
                            sharedPreferencesEditor.apply();
                            updateToDosAndChipsDisplay();
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
                break;
            case 2:
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete done")
                        .setMessage("Do you really want to delete done To Dos?")
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            db.toDoDao().deleteDone();
                            updateToDosAndChipsDisplay();
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateToDosAndChipsDisplay() {
        boolean showChipGroup = false;
        for (Map.Entry<String, Chip> e : chips.entrySet()) {
            boolean preference = sharedPreferences.getBoolean(e.getKey(), false);
            e.getValue().setVisibility(preference ? View.VISIBLE : View.GONE);
            if (preference)
                showChipGroup = true;
        }
        chipGroup.setVisibility(showChipGroup ? View.VISIBLE : View.GONE);
        displayedTodos.clear();
        adapter.notifyDataSetChanged();
        allToDos = db.toDoDao().getAll();
        displayedTodos.addAll(allToDos.stream().filter(t -> {
            boolean done = sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_DONE, false);
            boolean undone = sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_UNDONE, false);
            boolean noReminder = sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_NO_REMINDER, false);
            boolean oneTime = sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_ONE_TIME, false);
            boolean repeated = sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_FILTER_REPEATED, false);
            boolean show = true;
            if (done != undone) {
                if (done != t.isDone())
                    return false;
            }
            if (!(noReminder && oneTime && repeated) && !(!noReminder && !oneTime && !repeated)) {
                if (!((noReminder && (t.getReminder() == Reminder.NONE)) || (oneTime && (t.getReminder() == Reminder.ONE_TIME)) || (repeated && t.getReminder() == Reminder.REPEAT)))
                    return false;
            }
            return show;
        }).collect(Collectors.toList()));
        if (sharedPreferences.getBoolean(Statics.SharedPreferences.TODO_SORT_REMINDER_DATE, false))
            displayedTodos.sort((t1, t2) -> {
                if (t1.getReminder() == t2.getReminder()) {
                    if (t1.getReminder() == Reminder.REPEAT)
                        return t1.getTime().compareTo(t2.getTime());
                    if (t1.getReminder() == Reminder.ONE_TIME) {
                        if (t1.getDate().compareTo(t2.getDate()) != 0)
                            return t1.getDate().compareTo(t2.getDate());
                        return t1.getTime().compareTo(t2.getTime());
                    }
                } else {
                    if (t1.getReminder() == Reminder.NONE || t2.getReminder() == Reminder.REPEAT)
                        return 1;
                    if (t2.getReminder() == Reminder.NONE || t1.getReminder() == Reminder.REPEAT)
                        return -1;
                }
                return 0;
            });
        adapter.notifyDataSetChanged();
    }

    private void checkSharedPrefs() {
        for (Map.Entry<String, Chip> e : chips.entrySet()) {
            if (!sharedPreferences.contains(e.getKey()))
                sharedPreferencesEditor.putBoolean(e.getKey(), false);
        }
        updateToDosAndChipsDisplay();
        sharedPreferencesEditor.apply();
    }
}
