package project.awesomecountdown;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient implements MyConstants {

    private static Retrofit mRetrofit = null;

    public static Retrofit getClient(){
        if (mRetrofit==null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(TICKET_MASTER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

}
