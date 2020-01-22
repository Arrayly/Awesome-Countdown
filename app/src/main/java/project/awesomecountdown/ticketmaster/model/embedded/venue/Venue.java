package project.awesomecountdown.ticketmaster.model.embedded.venue;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Venue {
    @SerializedName("name")
    @Expose
    @Nullable
    private String locationName;

    @SerializedName("postalCode")
    @Expose
    @Nullable
    private String locationPostalCode;

    @Nullable
    public String getLocationName() {
        return locationName;
    }

    @Nullable
    public String getLocationPostalCode() {
        return locationPostalCode;
    }


    @Override
    public String toString() {
        return "Venue{" +
                "locationName='" + locationName + '\'' +
                ", locationPostalCode='" + locationPostalCode + '\'' +
                '}';
    }

}
