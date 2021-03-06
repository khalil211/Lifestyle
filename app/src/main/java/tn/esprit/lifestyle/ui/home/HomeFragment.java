package tn.esprit.lifestyle.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.adapters.ActivityEntryAdapter;
import tn.esprit.lifestyle.adapters.MedicineAdapter;
import tn.esprit.lifestyle.adapters.ToDoAdapter;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.Medicine;
import tn.esprit.lifestyle.entities.Reminder;
import tn.esprit.lifestyle.entities.ToDo;

public class HomeFragment extends Fragment {
    private LifeStyleDB db;
    private RecyclerView toDosList;
    private RecyclerView medsList;
    private TextView toDosText;
    private TextView medsText;
    private List<ToDo> todayToDos;
    private List<Medicine> todayMeds;
    private ToDoAdapter toDoAdapter;
    private MedicineAdapter medicineAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = LifeStyleDB.getDB(getContext());
        toDosList = view.findViewById(R.id.toDosList);
        toDosText = view.findViewById(R.id.toDosText);
        todayToDos = getTodayToDos();
        medsList = view.findViewById(R.id.medsList);
        medsText = view.findViewById(R.id.medsText);
        todayMeds = getTodayMedicine();
        if (todayToDos.size()!=0)
            toDosText.setVisibility(View.GONE);
            toDoAdapter = new ToDoAdapter(todayToDos, this.getContext(), new ToDoAdapter.ClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                ToDo toDo = todayToDos.get(position);
                toDo.setDone(!toDo.isDone());
                toDo.setDoneDate(new Date());
                db.toDoDao().update(toDo);
            }

            @Override
            public void onItemLongClickListener(int position, View v) {

            }
        });
        toDosList.setAdapter(toDoAdapter);
        Chip addToDoButton = view.findViewById(R.id.addToDoButton);
        addToDoButton.setOnClickListener(view1 -> Navigation.findNavController(getView()).navigate(R.id.singleToDoFragment));
        List<ActivityEntry> activityEntries = db.activityDao().getThreeLastEntries();
        ActivityEntryAdapter adapter = new ActivityEntryAdapter(getContext(), activityEntries, db.activityDao().getAllActivities(), position -> {});
        TextView noActivitiesText = view.findViewById(R.id.noActivitiesText);
        if (!activityEntries.isEmpty())
            noActivitiesText.setVisibility(View.GONE);
        RecyclerView activitiesList = view.findViewById(R.id.activitiesList);
        activitiesList.setAdapter(adapter);
        Chip addActivityEntrtyButton = view.findViewById(R.id.addActivityEntrtyButton);
        addActivityEntrtyButton.setOnClickListener(view1 -> Navigation.findNavController(getView()).navigate(R.id.singleActivityEntryFragment));
        medsList.setAdapter(medicineAdapter);
        if (todayMeds.size()!=0)
            medsText.setVisibility(View.GONE);
        medicineAdapter = new MedicineAdapter(todayMeds, this.getContext(), new MedicineAdapter.ClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                Medicine meds = todayMeds.get(position);
                meds.setDone(!meds.isDone());
                meds.setDoneDate(new Date());
                db.medicineDao().update(meds);

            }

            @Override
            public void onItemLongClickListener(int position, View v) {

            }
        });
        medsList.setAdapter(medicineAdapter);
        return view;
    }

    private List<ToDo> getTodayToDos() {
        String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, 0, new Locale("en", "UK"));
        List<ToDo> list = db.toDoDao().getOneTimeToDos().stream().filter(t -> {
            Calendar c = Calendar.getInstance();
            if (c.get(Calendar.MONTH)+1 != t.getDate().getMonthValue())
                return false;
            return c.get(Calendar.DAY_OF_MONTH) == t.getDate().getDayOfMonth();
        }).collect(Collectors.toList());
        list.addAll(db.toDoDao().getRepeatedToDos().stream().filter(t -> t.getWeekDays().contains(day)).collect(Collectors.toList()));
        list.sort((t1, t2) -> {
            if (t1.getReminder() == t2.getReminder())
                return t1.getTime().compareTo(t2.getTime());
            else {
                if (t1.getReminder() == Reminder.ONE_TIME || t2.getReminder() == Reminder.REPEAT)
                    return 1;
                if (t2.getReminder() == Reminder.ONE_TIME || t1.getReminder() == Reminder.REPEAT)
                    return -1;
            }
            return 0;
        });
        return list;
    }

    private List<Medicine> getTodayMedicine() {
        String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, 0, new Locale("en", "UK"));
        List<Medicine> list = db.medicineDao().getOneTimeMeds().stream().filter(t -> {
            Calendar c = Calendar.getInstance();
            if (c.get(Calendar.MONTH)+1 != t.getDate().getMonthValue())
                return false;
            return c.get(Calendar.DAY_OF_MONTH) == t.getDate().getDayOfMonth();
        }).collect(Collectors.toList());
        list.addAll(db.medicineDao().getRepeatedMeds().stream().filter(t -> t.getWeekDays().contains(day)).collect(Collectors.toList()));
        list.sort((t1, t2) -> {
            if (t1.getReminder() == t2.getReminder())
                return t1.getTime().compareTo(t2.getTime());
            else {
                if (t1.getReminder() == Reminder.ONE_TIME || t2.getReminder() == Reminder.REPEAT)
                    return 1;
                if (t2.getReminder() == Reminder.ONE_TIME || t1.getReminder() == Reminder.REPEAT)
                    return -1;
            }
            return 0;
        });
        return list;
    }
}