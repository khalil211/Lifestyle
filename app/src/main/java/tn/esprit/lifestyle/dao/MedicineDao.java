package tn.esprit.lifestyle.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.lifestyle.entities.Medicine;

@Dao
public interface MedicineDao {
    @Insert
    void add(Medicine med);

    @Delete
    void delete(Medicine med);

    @Update
    void update(Medicine med);

    @Query("SELECT * FROM Medicine")
    List<Medicine> getAll();

    @Query("DELETE FROM Medicine WHERE done = 1")
    void deleteDone();

    @Query("SELECT * FROM Medicine WHERE reminder = 'REPEAT'")
    List<Medicine> getRepeatedMeds();

    @Query("SELECT * FROM Medicine WHERE reminder = 'ONE_TIME'")
    List<Medicine> getOneTimeMeds();
}
