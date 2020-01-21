package project.awesomecountdown;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "My_Event_Table")

public class Event {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @ColumnInfo(name = "alertSet")
    private boolean alertSet;

    @ColumnInfo(name = "eventOrderId")
    private long eventOrderId;

    @ColumnInfo(name = "millisLeft")
    private long millisLeft;

    @ColumnInfo(name = "notificationId")
    private long notificationId;

    @ColumnInfo(name = "millisAtTimeOfCreation")
    private long millisAtTimeOfCreation;

    @NonNull
    @ColumnInfo(name = "eventTitle")
    private String eventTitle;

    public Event(final long eventOrderId, final long millisLeft, final long millisAtTimeOfCreation,
            @NonNull final String eventTitle) {
        this.millisLeft = millisLeft;
        this.millisAtTimeOfCreation = millisAtTimeOfCreation;
        this.eventTitle = eventTitle;
        this.eventOrderId = eventOrderId;
    }

    public long getMillisAtTimeOfCreation() {
        return millisAtTimeOfCreation;
    }

    public void setMillisAtTimeOfCreation(final long millisAtTimeOfCreation) {
        this.millisAtTimeOfCreation = millisAtTimeOfCreation;
    }

    public boolean isAlertSet() {
        return alertSet;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(final long notificationId) {
        this.notificationId = notificationId;
    }

    public void setAlertSet(final boolean alertSet) {
        this.alertSet = alertSet;
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
