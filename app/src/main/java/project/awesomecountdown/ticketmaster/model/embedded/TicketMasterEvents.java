package project.awesomecountdown.ticketmaster.model.embedded;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import project.awesomecountdown.ticketmaster.model.embedded.eventdates.EventDates;
import project.awesomecountdown.ticketmaster.model.embedded.images.EventImages;
import project.awesomecountdown.ticketmaster.model.embedded.venue.VenueObject;

public class TicketMasterEvents {
    @SerializedName("name")
    @Expose
    @Nullable
    private String name;

    @SerializedName("type")
    @Expose
    @Nullable
    private String type;

    @SerializedName("id")
    @Expose
    @Nullable
    private String id;

    @SerializedName("url")
    @Expose
    @Nullable
    private String url;

    @SerializedName("images")
    @Expose
    @Nullable
    private ArrayList<EventImages> images;

    @SerializedName("dates")
    @Expose
    @Nullable
    private EventDates dates;

    @SerializedName("_embedded")
    @Expose
    @Nullable
    private VenueObject venueObject;

    @Nullable
    public VenueObject getVenueObject() {
        return venueObject;
    }

    @Nullable
    public EventDates getDates() {
        return dates;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public ArrayList<EventImages> getImages() {
        return images;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TicketMasterEvents{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", images=" + images +
                ", dates=" + dates +
                ", venueObject=" + venueObject +
                '}';
    }

}
