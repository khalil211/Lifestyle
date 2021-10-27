package tn.esprit.lifestyle.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.lifestyle.dao.ToDoDao;
import tn.esprit.lifestyle.entities.ToDo;

@Database(entities = {ToDo.class}, version = 1, exportSchema = false)
public abstract class LifeStyleDB extends RoomDatabase {
    private static LifeStyleDB instance;

    public abstract ToDoDao toDoDao();

    public static LifeStyleDB getDB(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), LifeStyleDB.class, "lifestyle").allowMainThreadQueries().build();
        return instance;
    }
}
