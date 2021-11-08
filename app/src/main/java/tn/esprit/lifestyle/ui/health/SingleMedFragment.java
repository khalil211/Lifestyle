package tn.esprit.lifestyle.ui.health;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.Reminder;
import tn.esprit.lifestyle.entities.Medicine;

public class SingleMedFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private LifeStyleDB db;
    private FloatingActionButton confirmMedButton;
    private EditText medName;
    private CheckBox reminderCheck;
    private RadioGroup radioGroup;
    private RadioButton oneTimeRadio;
    private RadioButton repeatRadio;
    private TextView timeText;
    private EditText timeEdit;
    private TextView dateText;
    private EditText dateEdit;
    private TextView weekDaysText;
    private List<CheckBox> weekDaysChecks;
    private Medicine med;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_single_med, container, false);
        db = LifeStyleDB.getDB(this.getContext());
        medName = view.findViewById(R.id.name);
        confirmMedButton = view.findViewById(R.id.confirmToDoButton);
        reminderCheck = view.findViewById(R.id.reminderCheck);
        radioGroup = view.findViewById(R.id.radioGroup);
        oneTimeRadio = view.findViewById(R.id.oneTimeRadio);
        repeatRadio = view.findViewById(R.id.repeatRadio);
        timeText = view.findViewById(R.id.timeText);
        timeEdit = view.findViewById(R.id.timeEdit);
        dateText = view.findViewById(R.id.dateText);
        dateEdit = view.findViewById(R.id.dateEdit);
        weekDaysText = view.findViewById(R.id.weekDaysText);
        weekDaysChecks = new ArrayList<>();
        weekDaysChecks.add(view.findViewById(R.id.mondayCheck));
        weekDaysChecks.add(view.findViewById(R.id.tuesdayCheck));
        weekDaysChecks.add(view.findViewById(R.id.wednesdayCheck));
        weekDaysChecks.add(view.findViewById(R.id.thursdayCheck));
        weekDaysChecks.add(view.findViewById(R.id.fridayCheck));
        weekDaysChecks.add(view.findViewById(R.id.saturdayCheck));
        weekDaysChecks.add(view.findViewById(R.id.sundayCheck));
        timeEdit.setOnClickListener(view1 -> {
            Calendar c = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(getContext(), this,c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
            dialog.show();
        });
        dateEdit.setOnClickListener(view1 -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), this,c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        reminderCheck.setOnClickListener(v -> reminderDisplay(reminderCheck.isChecked()));
        View.OnClickListener radioListener = v -> {
            oneTimeDisplay(oneTimeRadio.isChecked());
            repeatDisplay(repeatRadio.isChecked());
        };
        oneTimeRadio.setOnClickListener(radioListener);
        repeatRadio.setOnClickListener(radioListener);
        Medicine m = (Medicine) (getArguments() != null ? getArguments().getSerializable("medicine") : null);
        if (m == null) {
            reminderDisplay(false);
            med = new Medicine();
        } else {
            med = m;
            displayMedicine(med);
        }
        confirmMedButton.setOnClickListener(view1 -> {
            String text = medName.getText().toString();
            if (text.isEmpty())
                Toast.makeText(getContext(),"Please enter a Medicine", Toast.LENGTH_LONG).show();
            else if (reminderCheck.isChecked() && timeEdit.getText().toString().isEmpty())
                Toast.makeText(getContext(),"Please enter reminder time", Toast.LENGTH_LONG).show();
            else if (reminderCheck.isChecked() && oneTimeRadio.isChecked() && dateEdit.getText().toString().isEmpty())
                Toast.makeText(getContext(),"Please enter reminder date", Toast.LENGTH_LONG).show();
            else {
                med.setText(text);
                if (reminderCheck.isChecked()) {
                    if (oneTimeRadio.isChecked()) {
                        med.setReminder(Reminder.ONE_TIME);
                        med.setWeekDays("");
                    } else {
                        med.setDate(null);
                        med.setReminder(Reminder.REPEAT);
                        StringBuilder weekDays;
                        weekDays = new StringBuilder();
                        for (CheckBox c : weekDaysChecks) {
                            if (c.isChecked())
                                weekDays.append(c.getText().toString()).append(",");
                        }
                        med.setWeekDays(weekDays.toString());
                    }
                } else {
                    med.setReminder(Reminder.NONE);
                    med.setDate(null);
                    med.setTime(null);
                    med.setWeekDays("");
                }
                if (m == null)
                    db.medicineDao().add(med);
                else
                    db.medicineDao().update(med);
                Navigation.findNavController(requireView()).navigate(R.id.healthFragment);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if ((Medicine) (getArguments() != null ? getArguments().getSerializable("medicine") : null) != null)
            menu.add(0, 0, 0, "Delete").setIcon(R.drawable.ic_baseline_delete_outline).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            new AlertDialog.Builder(getContext()).setTitle("Confirmation").setMessage("Do you really want to delete this Medicine?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        db.medicineDao().delete(med);
                        Navigation.findNavController(getView()).navigate(R.id.toDoFragment);
                    })
                    .setNegativeButton("Cancel", null).show();
            return true;
        }
        Navigation.findNavController(getView()).navigate(R.id.healthFragment);
        return true;
    }

    private void reminderDisplay(boolean show) {
        radioGroup.setVisibility(show ? View.VISIBLE : View.GONE);
        timeText.setVisibility(show ? View.VISIBLE : View.GONE);
        timeEdit.setVisibility(show ? View.VISIBLE : View.GONE);
        oneTimeDisplay(oneTimeRadio.isChecked() && show);
        repeatDisplay(repeatRadio.isChecked() && show);
    }

    private void oneTimeDisplay(boolean show) {
        dateText.setVisibility(show ? View.VISIBLE : View.GONE);
        dateEdit.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void repeatDisplay(boolean show) {
        weekDaysText.setVisibility(show ? View.VISIBLE : View.GONE);
        for (CheckBox c : weekDaysChecks)
            c.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void displayMedicine(Medicine medicine) {
        medName.setText(medicine.getText());
        if (medicine.getReminder() == Reminder.NONE)
            reminderDisplay(false);
        else {
            reminderCheck.setChecked(true);
            timeEdit.setText(String.format("%d:%d", medicine.getTime().getHour(), medicine.getTime().getMinute()));
            if (medicine.getReminder() == Reminder.ONE_TIME) {
                oneTimeRadio.setChecked(true);
                dateEdit.setText(String.format("%d/%d/%d", medicine.getDate().getDayOfMonth(), medicine.getDate().getMonthValue()-1, medicine.getDate().getYear()));
            } else if (medicine.getReminder() == Reminder.REPEAT) {
                repeatRadio.setChecked(true);
                for (CheckBox c : weekDaysChecks)
                    c.setChecked(medicine.getWeekDays().contains(c.getText()));
            }
            reminderDisplay(true);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        dateEdit.setText(String.format("%d/%d/%d", i2, i1+1, i));
        med.setDate(LocalDate.of(i, i1+1, i2));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        timeEdit.setText(String.format("%d:%d", i, i1));
        med.setTime(LocalTime.of(i, i1));
    }


}
