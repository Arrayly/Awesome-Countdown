package project.awesomecountdown.ticketmaster.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import project.awesomecountdown.ticketmaster.model.embedded.Embed;

public class Ticket {

    @SerializedName("_embedded")
    @Expose
    private Embed embed;

    public Embed getEmbed() {
        return embed;
    }

    public void setEmbed(final Embed embed) {
        this.embed = embed;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "embed=" + embed +
                '}';
    }
}
