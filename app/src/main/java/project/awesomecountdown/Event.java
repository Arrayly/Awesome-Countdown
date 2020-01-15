package project.awesomecountdown;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "My_Event_Table")

public class Event {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(name = "eventOrderId")
    private long eventOrderId;

    @ColumnInfo(name = "millisLeft")
    private long millisLeft;

    @NonNull
    @ColumnInfo(name = "eventTitle")
    private String eventTitle;

    public Event(final long eventOrderId,final long millisLeft,@NonNull final String eventTitle) {
        this.millisLeft = millisLeft;
        this.eventTitle = eventTitle;
        this.eventOrderId = eventOrderId;
    }

    public long getEventOrderId() {
        return eventOrderId;
    }

    public void setEventOrderId(final long eventOrderId) {
        this.eventOrderId = eventOrderId;
    }

    public long getID() {
        return ID;
    }

    public void setID(final long ID) {
        this.ID = ID;
    }

    public long getMillisLeft() {
        return millisLeft;
    }

    public void setMillisLeft(final long millisLeft) {
        this.millisLeft = millisLeft;
    }

    @NonNull
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(@NonNull final String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
