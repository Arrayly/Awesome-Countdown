package project.awesomecountdown.ticketmaster.model.embedded;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Embed {

    @SerializedName("events")
    @Expose
    @Nullable
    private ArrayList<TicketMasterEvents> events;

    @Nullable
    public ArrayList<TicketMasterEvents> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "Embed{" +
                "events=" + events +
                '}';
    }

}
