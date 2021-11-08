package tn.esprit.lifestyle.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import tn.esprit.lifestyle.entities.Activity;
import tn.esprit.lifestyle.entities.ActivityEntry;
import tn.esprit.lifestyle.entities.ActivityWithEntries;

@Dao
public interface ActivityDao {
    @Insert
    long addActivity(Activity activity);

    @Update
    void updateActivity(Activity activity);

    @Delete
    void deleteActivity(Activity activity);

    @Query("SELECT * FROM Activity")
    List<Activity> getAllActivities();

    @Query("SELECT * FROM Activity WHERE id = :id")
    Activity getActivityById(int id);

    @Insert
    void addEntry(ActivityEntry activityEntry);

    @Update
    void updateEntry(ActivityEntry activityEntry);

    @Delete
    void deleteEntry(ActivityEntry activityEntry);

    @Query("SELECT * FROM ActivityEntry ORDER BY date DESC")
    List<ActivityEntry> getAllEntries();

    @Transaction
    @Query("SELECT * FROM Activity")
    List<ActivityWithEntries> getActivitiesWithEntries();

    @Query("SELECT * FROM ActivityEntry ORDER BY date DESC LIMIT 3")
    List<ActivityEntry> getThreeLastEntries();

    @Query("SELECT * FROM ActivityEntry WHERE activityId = :activityId ORDER BY date DESC")
    List<ActivityEntry> getEntriesByActivityId(int activityId);
}
