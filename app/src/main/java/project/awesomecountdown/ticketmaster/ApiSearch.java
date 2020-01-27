package project.awesomecountdown.ticketmaster;

public class ApiSearch {
    private String baseUrl = "https://app.ticketmaster.com/";

    private String allEventsByCountryCode = "discovery/v2/events?apikey=q6Qkd179SUqRysAMvAQMdopTkmaKXa3J&locale=*&countryCode={CountryCode}";

    private String getAllEventsByCountryCode(String countryCode){
        return allEventsByCountryCode.replace("{CountryCode}",countryCode);
    }


}
