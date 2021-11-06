package tn.esprit.lifestyle.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.adapters.ToDoAdapter;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Reminder;
import tn.esprit.lifestyle.entities.ToDo;

public class HomeFragment extends Fragment {
    private LifeStyleDB db;
    private RecyclerView toDosList;
    private TextView toDosText;
    private List<ToDo> todayTodos;
    private ToDoAdapter toDoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = LifeStyleDB.getDB(getContext());
        toDosList = view.findViewById(R.id.toDosList);
        toDosText = view.findViewById(R.id.toDosText);
        todayTodos = getTodayToDos();
        if (todayTodos.size()!=0)
            toDosText.setVisibility(View.GONE);
        toDoAdapter = new ToDoAdapter(todayTodos, this.getContext(), new ToDoAdapter.ClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                ToDo toDo = todayTodos.get(position);
                toDo.setDone(!toDo.isDone());
                toDo.setDoneDate(new Date());
                db.toDoDao().update(toDo);

            }

            @Override
            public void onItemLongClickListener(int position, View v) {

            }
        });
        toDosList.setAdapter(toDoAdapter);
        return view;
    }

    private List<ToDo> getTodayToDos() {
        String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, 0, new Locale("en", "UK"));
        List<ToDo> list = db.toDoDao().getOneTimeToDos().stream().filter(t -> {
            Calendar c = Calendar.getInstance();
            System.out.println(c.get(Calendar.DAY_OF_MONTH));
            System.out.println(t.getDate().getDayOfMonth());
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
}