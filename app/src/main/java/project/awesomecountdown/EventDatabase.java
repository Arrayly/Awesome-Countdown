package project.awesomecountdown;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class, ExpiredEvents.class}, version = 6, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {

    private static EventDatabase INSTANCE;
    public abstract EventDao mEventDao();
    public abstract ExpiredEventDao mExpiredEventDao();

    public static EventDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventDatabase.class,"AppDatabasev4.db").
                            fallbackToDestructiveMigration().
                            build();
                }
            }
        }

        return INSTANCE;
    }

}
