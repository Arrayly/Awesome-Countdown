package project.awesomecountdown;


import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import project.awesomecountdown.EventFeedAdapter.EventFeedClickListener;
import project.awesomecountdown.databinding.FragmentEventFeedBinding;
import project.awesomecountdown.ticketmaster.TicketMasterInterface;
import project.awesomecountdown.ticketmaster.model.Ticket;
import project.awesomecountdown.ticketmaster.model.embedded.TicketMasterEvents;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFeedFragment extends ModelFragment implements MyConstants {

    private String ticketMasterDynamicUrl
            = "discovery/v2/events?apikey=q6Qkd179SUqRysAMvAQMdopTkmaKXa3J&locale=*&countryCode=GB";

    private FragmentEventFeedBinding mBinding;

    private EventViewModel mEventViewModel;

    private EventFeedAdapter mFeedAdapter;

    private List<EventFeedDetails> mEventFeedDetails = new ArrayList<>();

    public EventFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_feed, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startFragmentInitProcess();
    }

    @Override
    protected void onCreateInstances() {
        super.onCreateInstances();
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.eventFeedRecycler.setLayoutManager(layoutManager);
        mBinding.eventFeedRecycler.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mEventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
    }

    @Override
    protected void onFragmentInitFinished() {
        super.onFragmentInitFinished();
        getFeed();
    }

    private void getFeed() {

        TicketMasterInterface ticketMasterInterface = RetrofitClient.getClient().create(TicketMasterInterface.class);

        Call<Ticket> call = ticketMasterInterface.getTicketInfoGB(ticketMasterDynamicUrl);

        mBinding.recyclerFeedProgressbar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(final Call<Ticket> call, final Response<Ticket> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Unable to retrieve data" + response.message(), Toast.LENGTH_LONG)
                            .show();

                } else {
                    mEventFeedDetails.clear();
                    ArrayList<TicketMasterEvents> events = response.body().getEmbed().getEvents();

                    String eventId;
                    String eventName;
                    String eventUrl;
                    String eventLocalDate;
                    String eventLocalTime;
                    String eventImage16_9;
                    String eventLocationName;
                    String eventPostalCode;

                    for (TicketMasterEvents in : events) {
                        eventId = in.getId();
                        eventName = in.getName();
                        eventUrl = in.getUrl();
                        eventLocalDate = in.getDates().getStart().getLocalDate();
                        eventLocalTime = in.getDates().getStart().getLocalTime();
                        eventImage16_9 = in.getImages().get(0).getUrl();
                        eventLocationName = in.getVenueObject().getVenues().get(0).getLocationName();
                        eventPostalCode = in.getVenueObject().getVenues().get(0).getLocationPostalCode();

                        EventFeedDetails feedDetails = new EventFeedDetails(eventId, eventName, eventUrl,
                                eventLocalDate,
                                eventLocalTime, eventImage16_9, eventLocationName, eventPostalCode);
                        mEventFeedDetails.add(feedDetails);
                    }

                    mFeedAdapter = new EventFeedAdapter(getActivity(), mEventFeedDetails,
                            new EventFeedClickListener() {
                                @Override
                                public void onClick(final int position) {
                                    Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
                                }
                            });

                    mBinding.eventFeedRecycler.setAdapter(mFeedAdapter);

                    Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();

                    mBinding.recyclerFeedProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(final Call<Ticket> call, final Throwable t) {
                Toast.makeText(getActivity(), "An ERROR HAS OCCURED!", Toast.LENGTH_LONG).show();

            }
        });
    }
}
