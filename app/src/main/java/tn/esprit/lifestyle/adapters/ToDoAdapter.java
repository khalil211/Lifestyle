package tn.esprit.lifestyle.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.entities.Reminder;
import tn.esprit.lifestyle.entities.ToDo;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {
    private List<ToDo> toDos;
    private Context context;
    private ClickListener clickListener;

    public ToDoAdapter(List<ToDo> toDos, Context context, ClickListener clickListener) {
        this.toDos = toDos;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_to_do, parent,false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        ToDo toDo = toDos.get(position);
        holder.toDoCheck.setText(toDo.getText());
        holder.toDoCheck.setChecked(toDo.isDone());
        holder.toDoCheck.setPaintFlags(toDo.isDone() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        int color = ContextCompat.getColor(context, isLate(toDo) ? R.color.red : R.color.black);
        holder.toDoCheck.setTextColor(color);
        holder.reminderDayText.setTextColor(color);
        holder.reminderTimeText.setTextColor(color);
        holder.reminderDayText.setText(toDo.getReminderDayText());
        holder.reminderTimeText.setText(toDo.getTime() == null ? "" : toDo.getTime().toString());
    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

    private boolean isLate(ToDo toDo) {
        if (toDo.isDone())
            return false;
        if (toDo.getReminder() == Reminder.NONE)
            return false;
        if (toDo.getReminder() == Reminder.REPEAT) {
            String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, 0, new Locale("en", "UK"));
            if (!toDo.getWeekDays().contains(day))
                return false;
            if (toDo.getTime().compareTo(LocalTime.now()) > 0)
                return false;
            return true;
        }
        int comparision = toDo.getDate().compareTo(LocalDate.now());
        if (comparision == 0) {
            if (toDo.getTime().compareTo(LocalTime.now()) > 0)
                return false;
            return true;
        }
        if (comparision > 0)
            return false;
        return true;
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final CheckBox toDoCheck;
        private final TextView reminderDayText;
        private final TextView reminderTimeText;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            toDoCheck = itemView.findViewById(R.id.toDoCheck);
            toDoCheck.setOnClickListener(this);
            toDoCheck.setOnLongClickListener(this);
            reminderDayText = itemView.findViewById(R.id.reminderDayText);
            reminderDayText.setSingleLine(false);
            reminderTimeText = itemView.findViewById(R.id.reminderTimeText);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClickListener(getAdapterPosition(), view);
            return true;
        }

    }

    public interface ClickListener {
        void onItemClickListener(int position, View view);
        void onItemLongClickListener(int position, View view);
    }
}
