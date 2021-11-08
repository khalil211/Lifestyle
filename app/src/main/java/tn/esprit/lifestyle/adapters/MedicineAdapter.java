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
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.entities.Medicine;
import tn.esprit.lifestyle.entities.Reminder;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> meds;
    private Context context;
    private MedicineAdapter.ClickListener clickListener;

    public MedicineAdapter(List<Medicine> meds, Context context, MedicineAdapter.ClickListener clickListener) {
        this.meds = meds;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MedicineAdapter.MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_med, parent,false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.MedicineViewHolder holder, int position) {
        Medicine med = meds.get(position);
        holder.medsCheck.setText(med.getText());
        holder.medsCheck.setChecked(med.isDone());
        holder.medsCheck.setPaintFlags(med.isDone() ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
        int color = ContextCompat.getColor(context, isLate(med) ? R.color.red : R.color.black);
        holder.medsCheck.setTextColor(color);
        holder.reminderDayText.setTextColor(color);
        holder.reminderTimeText.setTextColor(color);
        holder.reminderDayText.setText(med.getReminderDayText());
        holder.reminderTimeText.setText(med.getTime() == null ? "" : med.getTime().toString());
    }

    @Override
    public int getItemCount() {
        return meds.size();
    }

    private boolean isLate(Medicine med) {
        if (med.isDone())
            return false;
        if (med.getReminder() == Reminder.NONE)
            return false;
        if (med.getReminder() == Reminder.REPEAT) {
            String day = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, 0, new Locale("en", "UK"));
            if (!med.getWeekDays().contains(day))
                return false;
            if (med.getTime().compareTo(LocalTime.now()) > 0)
                return false;
            return true;
        }
        int comparision = med.getDate().compareTo(LocalDate.now());
        if (comparision == 0) {
            if (med.getTime().compareTo(LocalTime.now()) > 0)
                return false;
            return true;
        }
        if (comparision > 0)
            return false;
        return true;
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final CheckBox medsCheck;
        private final TextView reminderDayText;
        private final TextView reminderTimeText;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            medsCheck = itemView.findViewById(R.id.toDoCheck);
            medsCheck.setOnClickListener(this);
            medsCheck.setOnLongClickListener(this);
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
