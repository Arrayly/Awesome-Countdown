package project.awesomecountdown.ticketmaster.model.embedded.eventdates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventDates {
    @SerializedName("start")
    @Expose
    private Start start;

    public Start getStart() {
        return start;
    }

    public void setStart(final Start start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "EventDates{" +
                "start=" + start +
                '}';
    }

}
