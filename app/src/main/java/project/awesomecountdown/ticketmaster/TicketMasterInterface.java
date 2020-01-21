package project.awesomecountdown.ticketmaster;

import project.awesomecountdown.ticketmaster.model.Ticket;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface TicketMasterInterface {

    String BASE_URL = "https://app.ticketmaster.com/";

    @Headers("Content-Type: application/json")
    @GET("discovery/v2/events?apikey=q6Qkd179SUqRysAMvAQMdopTkmaKXa3J&locale=*")
    Call<Ticket> getTicketInfo();

    @Headers("Content-Type: application/json")
    @GET
    Call<Ticket> getTicketInfoGB(@Url String url);

}
