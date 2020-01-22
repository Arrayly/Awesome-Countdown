package project.awesomecountdown.ticketmaster.model.embedded.eventdates;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventDates {
    @SerializedName("start")
    @Expose
    private Start start;

    @Nullable
    public Start getStart() {
        return start;
    }


    @Override
    public String toString() {
        return "EventDates{" +
                "start=" + start +
                '}';
    }

}
