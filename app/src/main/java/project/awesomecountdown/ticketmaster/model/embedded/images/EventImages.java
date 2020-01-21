package project.awesomecountdown.ticketmaster.model.embedded.images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventImages {
    @SerializedName("ratio")
    @Expose
    private String ratio;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("width")
    @Expose
    private String width;

    @SerializedName("height")
    @Expose
    private String height;

    public String getRatio() {
        return ratio;
    }

    public void setRatio(final String ratio) {
        this.ratio = ratio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(final String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(final String height) {
        this.height = height;
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
