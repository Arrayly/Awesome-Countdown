package project.awesomecountdown.ticketmaster.model.embedded.venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class VenueObject {

    @SerializedName("venues")
    @Expose
    private ArrayList<Venue> venues;

    public ArrayList<Venue> getVenues() {
        return venues;
    }

    public void setVenues(final ArrayList<Venue> venues) {
        this.venues = venues;
    }

    @Override
    public String toString() {
        return "VenueObject{" +
                "venues=" + venues +
                '}';
    }

}
