package project.awesomecountdown.ticketmaster.model.embedded;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import project.awesomecountdown.ticketmaster.model.embedded.eventdates.EventDates;
import project.awesomecountdown.ticketmaster.model.embedded.images.EventImages;
import project.awesomecountdown.ticketmaster.model.embedded.venue.VenueObject;

public class TicketMasterEvents {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("images")
    @Expose
    private ArrayList<EventImages> images;

    @SerializedName("dates")
    @Expose
    private EventDates dates;

    @SerializedName("_embedded")
    @Expose
    private VenueObject venueObject;

    public VenueObject getVenueObject() {
        return venueObject;
    }

    public void setVenueObject(final VenueObject venueObject) {
        this.venueObject = venueObject;
    }

    public EventDates getDates() {
        return dates;
    }

    public void setDates(final EventDates dates) {
        this.dates = dates;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public ArrayList<EventImages> getImages() {
        return images;
    }

    public void setImages(final ArrayList<EventImages> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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
