package project.awesomecountdown;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Expired_Event_Table")
public class ExpiredEvents {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @NonNull
    @ColumnInfo(name = "eventTitle")
    private String eventTitle;

    @ColumnInfo(name = "millisLeft")
    private long millisLeft;

    public ExpiredEvents(@NonNull final String eventTitle, final long millisLeft) {
        this.eventTitle = eventTitle;
        this.millisLeft = millisLeft;
    }

    public long getID() {
        return ID;
    }

    public void setID(final long ID) {
        this.ID = ID;
    }

    @NonNull
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(@NonNull final String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public long getMillisLeft() {
        return millisLeft;
    }

    public void setMillisLeft(final long millisLeft) {
        this.millisLeft = millisLeft;
    }
}
