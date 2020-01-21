package project.awesomecountdown;

public class EventFeedDetails {

    private String eventId;

    private String eventName;

    private String eventUrl;

    private String eventLocalDate;

    private String eventLocalTime;

    private String eventImage16_9;

    private String eventLocationName;

    private String eventPostalCode;

    public EventFeedDetails(final String eventId, final String eventName, final String eventUrl,
            final String eventLocalDate,
            final String eventLocalTime, final String eventImage16_9, final String eventLocationName,
            final String eventPostalCode) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventUrl = eventUrl;
        this.eventLocalDate = eventLocalDate;
        this.eventLocalTime = eventLocalTime;
        this.eventImage16_9 = eventImage16_9;
        this.eventLocationName = eventLocationName;
        this.eventPostalCode = eventPostalCode;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public String getEventLocalDate() {
        return eventLocalDate;
    }

    public String getEventLocalTime() {
        return eventLocalTime;
    }

    public String getEventImage16_9() {
        return eventImage16_9;
    }

    public String getEventLocationName() {
        return eventLocationName;
    }

    public String getEventPostalCode() {
        return eventPostalCode;
    }
}
