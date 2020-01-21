package project.awesomecountdown.ticketmaster.model.embedded;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Embed {

    @SerializedName("events")
    @Expose
    private ArrayList<TicketMasterEvents> events;

    public ArrayList<TicketMasterEvents> getEvents() {
        return events;
    }

    public void setEvents(final ArrayList<TicketMasterEvents> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Embed{" +
                "events=" + events +
                '}';
    }

}
