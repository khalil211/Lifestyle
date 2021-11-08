package tn.esprit.lifestyle.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import tn.esprit.lifestyle.dao.ActivityDao;
import tn.esprit.lifestyle.dao.ToDoDao;
import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ToDo;

@Database(entities = {ToDo.class, Activity.class, ActivityEntry.class}, version = 8, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class LifeStyleDB extends RoomDatabase {
    private static LifeStyleDB instance;

    public abstract ToDoDao toDoDao();
    public abstract ActivityDao activityDao();

    public static LifeStyleDB getDB(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), LifeStyleDB.class, "lifestyle").allowMainThreadQueries().build();
        return instance;
    }
}
