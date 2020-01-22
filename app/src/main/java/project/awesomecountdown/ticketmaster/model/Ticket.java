package project.awesomecountdown.ticketmaster.model;
import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import project.awesomecountdown.ticketmaster.model.embedded.Embed;

public class Ticket {

    @SerializedName("_embedded")
    @Expose
    @Nullable
    private Embed embed;

    @Nullable
    public Embed getEmbed() {
        return embed;
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "embed=" + embed +
                '}';
    }
}
