package project.awesomecountdown;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ExpiredEventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addExpiredEvent(ExpiredEvents expiredEvents);

    @Query("SELECT * FROM Expired_Event_Table ORDER BY ID ASC")
    LiveData<List<ExpiredEvents>> getExpiredEvent();

    @Query("DELETE FROM Expired_Event_Table WHERE ID = :id")
    void deleteExpiredEventById(long id);

    @Query("DELETE FROM Expired_Event_Table")
    void deleteAllExpiredEvents();
}
