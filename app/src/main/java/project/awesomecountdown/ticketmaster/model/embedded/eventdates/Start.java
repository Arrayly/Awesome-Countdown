package project.awesomecountdown.ticketmaster.model.embedded.eventdates;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Start {

    @SerializedName("localDate")
    @Expose
    @Nullable
    private String localDate;

    @SerializedName("localTime")
    @Expose
    @Nullable
    private String localTime;

    @Nullable
    public String getLocalDate() {
        return localDate;
    }

    @Nullable
    public String getLocalTime() {
        return localTime;
    }


    @Override
    public String toString() {
        return "Start{" +
                "localDate='" + localDate + '\'' +
                ", localTime='" + localTime + '\'' +
                '}';
    }
}
