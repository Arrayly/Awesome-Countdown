package project.awesomecountdown;

public interface MyConstants {

    int DEFAULT_VALUE = 0, REQUEST_NEW_EVENT = 1, REQUEST_EDIT_EVENT = 2, REQUEST_UPDATE_EVENT = 3;

    int DEFAULT_IMG_ID = 0;

    String CHOSEN_TIME = "CHOSEN_TIME",
            CHOSEN_EVENT_ID = "CHOSEN_EVENT_ID",
            CHOSEN_EXPIRED_EVENT_ID = "CHOSEN_EXPIRED_EVENT_ID",
            CHANNEL_ID = "CHANNEL_ID",
            CHANNEL_NAME = "CHANNEL_NAME",
            CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION!",
            ALERT_EXTRA_ID = "ALERT_EXTRA_ID",
            BROADCAST_TITLE = "BROADCAST_TITLE",
            BROADCAST_ID_IDENTITY = "BROADCAST_ID_IDENTITY";

    boolean EDIT_EVENT = true;

    String TICKET_MASTER_BASE_URL = "https://app.ticketmaster.com/";

    String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    String IMAGE_BITMAP_ID = "IMAGE_BITMAP_ID";


    //Reveal animation circular
    //https://android.jlelse.eu/a-little-thing-that-matter-how-to-reveal-an-activity-with-circular-revelation-d94f9bfcae28

}

