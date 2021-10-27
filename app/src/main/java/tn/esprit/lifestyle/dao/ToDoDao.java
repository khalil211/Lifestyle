package tn.esprit.lifestyle.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.lifestyle.entities.ToDo;

@Dao
public interface ToDoDao {
    @Insert
    void add(ToDo toDo);

    @Delete
    void delete(ToDo toDo);

    @Query("SELECT * FROM ToDo")
    List<ToDo> getAll();
}
