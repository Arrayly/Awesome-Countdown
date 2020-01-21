package project.awesomecountdown.ticketmaster.model.embedded.venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Venue {
    @SerializedName("name")
    @Expose
    private String locationName;

    @SerializedName("postalCode")
    @Expose
    private String locationPostalCode;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public String getLocationPostalCode() {
        return locationPostalCode;
    }

    public void setLocationPostalCode(final String locationPostalCode) {
        this.locationPostalCode = locationPostalCode;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "locationName='" + locationName + '\'' +
                ", locationPostalCode='" + locationPostalCode + '\'' +
                '}';
    }

}
