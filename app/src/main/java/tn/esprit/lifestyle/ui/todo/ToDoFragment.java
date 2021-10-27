package tn.esprit.lifestyle.ui.todo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tn.esprit.lifestyle.R;
import tn.esprit.lifestyle.database.LifeStyleDB;
import tn.esprit.lifestyle.entities.ToDo;

public class ToDoFragment extends Fragment {

    private LifeStyleDB db;
    private List<ToDo> toDos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = LifeStyleDB.getDB(this.getContext());
        toDos = db.toDoDao().getAll();
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }
}