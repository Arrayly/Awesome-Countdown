package project.awesomecountdown;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addEvent(Event event);

    @NonNull
    @Query("SELECT * FROM My_Event_Table WHERE ID = :id")
    List<Event> getEventById(long id);

    @Query("SELECT * FROM My_Event_Table ORDER BY eventOrderId DESC")
    LiveData<List<Event>> getEvent();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEvent(List<Event> event);

    @Query("DELETE FROM My_Event_Table")
    void deleteAll();

    @Query("SELECT MAX(eventOrderId) FROM My_Event_Table")
    long getMaxOrderID();

    @Query("DELETE FROM My_Event_Table WHERE ID = :id")
    void deleteEventById(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEditedEvent(Event event);




}

