package project.awesomecountdown.ticketmaster.model.embedded.images;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventImages {
    @SerializedName("ratio")
    @Expose
    @Nullable
    private String ratio;

    @SerializedName("url")
    @Expose
    @Nullable
    private String url;

    @SerializedName("width")
    @Expose
    @Nullable
    private String width;

    @SerializedName("height")
    @Expose
    @Nullable
    private String height;

    @Nullable
    public String getRatio() {
        return ratio;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getWidth() {
        return width;
    }

    @Nullable
    public String getHeight() {
        return height;
    }


    @Override
    public String toString() {
        return "EventImages{" +
                "ratio='" + ratio + '\'' +
                ", url='" + url + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

}
