package project.awesomecountdown.ticketmaster.model.embedded.eventdates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Start {

    @SerializedName("localDate")
    @Expose
    private String localDate;

    @SerializedName("localTime")
    @Expose
    private String localTime;

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(final String localDate) {
        this.localDate = localDate;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(final String localTime) {
        this.localTime = localTime;
    }

    @Override
    public String toString() {
        return "Start{" +
                "localDate='" + localDate + '\'' +
                ", localTime='" + localTime + '\'' +
                '}';
    }
}
