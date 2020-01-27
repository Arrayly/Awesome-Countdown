package project.awesomecountdown;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "My_Event_Table")

public class Event {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @NonNull
    @ColumnInfo(name = "alertSet")
    private boolean alertSet;

    @NonNull
    @ColumnInfo(name = "eventOrderId")
    private long eventOrderId;

    @NonNull
    @ColumnInfo(name = "millisLeft")
    private long millisLeft;

    @NonNull
    @ColumnInfo(name = "notificationId")
    private long notificationId;

    @NonNull
    @ColumnInfo(name = "millisAtTimeOfCreation")
    private long millisAtTimeOfCreation;

    @NonNull
    @ColumnInfo(name = "eventTitle")
    private String eventTitle;

    @NonNull
    @ColumnInfo(name = "eventUrl")
    private String eventUrl;

    @NonNull
    @ColumnInfo(name = "eventLocation")
    private String eventLocation;

    @NonNull
    @ColumnInfo(name = "imageId")
    private int imageId;


    @NonNull
    @ColumnInfo(name = "textColorId")
    private int textColorId;

    @NonNull
    @ColumnInfo(name = "dateRawString")
    private String dateRawString;

    @NonNull
    @ColumnInfo(name = "imgUrl")
    private String imgUrl;

    @NonNull
    @ColumnInfo(name = "imageLoadedFromUserPhone")
    private boolean imageLoadedFromUserPhone;

    @NonNull
    @ColumnInfo(name = "imageLoadedFromUrl")
    private boolean imageLoadedFromUrl;


    public Event(@NonNull final long eventOrderId, @NonNull final long millisLeft,
            @NonNull final long millisAtTimeOfCreation,
            @NonNull final String eventTitle, @NonNull final int imageId, @NonNull final int textColorId,
            @NonNull final String dateRawString) {

        this.millisLeft = millisLeft;
        this.millisAtTimeOfCreation = millisAtTimeOfCreation;
        this.eventTitle = eventTitle;
        this.eventOrderId = eventOrderId;
        this.imageId = imageId;
        this.textColorId = textColorId;
        this.dateRawString = dateRawString;

        this.setEventUrl("");
        this.setEventLocation("");
        this.setImgUrl("");
    }

    public boolean isImageLoadedFromUrl() {
        return imageLoadedFromUrl;
    }

    public void setImageLoadedFromUrl(final boolean imageLoadedFromUrl) {
        this.imageLoadedFromUrl = imageLoadedFromUrl;
    }

    public boolean isImageLoadedFromUserPhone() {
        return imageLoadedFromUserPhone;
    }

    public void setImageLoadedFromUserPhone(final boolean imageLoadedFromUserPhone) {
        this.imageLoadedFromUserPhone = imageLoadedFromUserPhone;
    }

    @NonNull
    public String getDateRawString() {
        return dateRawString;
    }

    public void setDateRawString(@NonNull final String dateRawString) {
        this.dateRawString = dateRawString;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(final int imageId) {
        this.imageId = imageId;
    }

    public int getTextColorId() {
        return textColorId;
    }

    public void setTextColorId(final int textColorId) {
        this.textColorId = textColorId;
    }

    @NonNull
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(@NonNull final String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @NonNull
    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(@NonNull final String eventUrl) {
        this.eventUrl = eventUrl;
    }

    @NonNull
    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(@NonNull final String eventLocation) {
        this.eventLocation = eventLocation;
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
