package project.awesomecountdown;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Expired_Event_Table")
public class ExpiredEvents {

    @PrimaryKey(autoGenerate = true)
    private long ID;

    @NonNull
    @ColumnInfo(name = "eventTitle")
    private String eventTitle;

    @NonNull
    @ColumnInfo(name = "millisLeft")
    private long millisLeft;


    @NonNull
    @ColumnInfo(name = "millisRecordedAtExpiry")
    private long millisRecordedAtExpiry;


    @NonNull
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @NonNull
    @ColumnInfo(name = "imageLoadedFromUserPhone")
    private boolean imageLoadedFromUserPhone;


    @NonNull
    @ColumnInfo(name = "imageLoadedFromUrl")
    private boolean imageLoadedFromUrl;

    @NonNull
    @ColumnInfo(name = "imageId")
    private int imageId;


    @NonNull
    @ColumnInfo(name = "textColorId")
    private int textColorId;

    public ExpiredEvents(@NonNull final String eventTitle, final long millisLeft, @NonNull final String imageUrl,
            final boolean imageLoadedFromUserPhone, final boolean imageLoadedFromUrl, final int imageId,
            final int textColorId, final long millisRecordedAtExpiry) {
        this.eventTitle = eventTitle;
        this.millisLeft = millisLeft;
        this.imageUrl = imageUrl;
        this.imageLoadedFromUserPhone = imageLoadedFromUserPhone;
        this.imageLoadedFromUrl = imageLoadedFromUrl;
        this.imageId = imageId;
        this.textColorId = textColorId;
        this.millisRecordedAtExpiry = millisRecordedAtExpiry;
    }

    public long getMillisRecordedAtExpiry() {
        return millisRecordedAtExpiry;
    }

    public void setMillisRecordedAtExpiry(final long millisRecordedAtExpiry) {
        this.millisRecordedAtExpiry = millisRecordedAtExpiry;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(final int imageId) {
        this.imageId = imageId;
    }

    public long getID() {
        return ID;
    }

    public void setID(final long ID) {
        this.ID = ID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isImageLoadedFromUserPhone() {
        return imageLoadedFromUserPhone;
    }

    public void setImageLoadedFromUserPhone(final boolean imageLoadedFromUserPhone) {
        this.imageLoadedFromUserPhone = imageLoadedFromUserPhone;
    }

    public boolean isImageLoadedFromUrl() {
        return imageLoadedFromUrl;
    }

    public void setImageLoadedFromUrl(final boolean imageLoadedFromUrl) {
        this.imageLoadedFromUrl = imageLoadedFromUrl;
    }

    public int getTextColorId() {
        return textColorId;
    }

    public void setTextColorId(final int textColorId) {
        this.textColorId = textColorId;
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
