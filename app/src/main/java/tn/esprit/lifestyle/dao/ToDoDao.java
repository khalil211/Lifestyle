package tn.esprit.lifestyle.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.lifestyle.entities.ToDo;

@Dao
public interface ToDoDao {
    @Insert
    void add(ToDo toDo);

    @Delete
    void delete(ToDo toDo);

    @Update
    void update(ToDo toDo);

    @Query("SELECT * FROM ToDo")
    List<ToDo> getAll();

    @Query("DELETE FROM ToDo WHERE done = 1")
    void deleteDone();

    @Query("SELECT * FROM ToDo WHERE reminder = 'REPEAT'")
    List<ToDo> getRepeatedToDos();

    @Query("SELECT * FROM ToDo WHERE reminder = 'ONE_TIME'")
    List<ToDo> getOneTimeToDos();
}
