package project.awesomecountdown.ticketmaster.model.embedded.venue;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class VenueObject {

    @SerializedName("venues")
    @Expose
    @Nullable
    private ArrayList<Venue> venues;

    @Nullable
    public ArrayList<Venue> getVenues() {
        return venues;
    }

    @Override
    public String toString() {
        return "VenueObject{" +
                "venues=" + venues +
                '}';
    }

}
