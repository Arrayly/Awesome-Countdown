package project.awesomecountdown;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import project.awesomecountdown.EventFeedAdapter.EventFeedClickListener;
import project.awesomecountdown.databinding.FragmentEventFeedBinding;
import project.awesomecountdown.ticketmaster.TicketMasterInterface;
import project.awesomecountdown.ticketmaster.model.Ticket;
import project.awesomecountdown.ticketmaster.model.embedded.TicketMasterEvents;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFeedFragment extends ModelFragment
        implements MyConstants, BottomSheetDialogFeedFragment.FeedFragmentBottomSheetListener {

    private String ticketMasterGetAllEvents
            = "discovery/v2/events?apikey=q6Qkd179SUqRysAMvAQMdopTkmaKXa3J&locale=*&countryCode=GB";

    private String ticketMasterSearchEvents
            = "https://app.ticketmaster.com/discovery/v2/events?apikey=q6Qkd179SUqRysAMvAQMdopTkmaKXa3J&keyword={search}&locale=*&city=London";

    private FragmentEventFeedBinding mBinding;

    private EventViewModel mEventViewModel;

    private DataTransactionViewModel mTransactionViewModel;

    private EventFeedAdapter mFeedAdapter;

    private List<EventFeedDetails> mEventFeedDetails = new ArrayList<>();

    private boolean onRefresh;

    private int adapterPositionOfItemSelected;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.eventFeedRecycler.setLayoutManager(layoutManager);
        mBinding.eventFeedRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();
    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mEventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(DataTransactionViewModel.class);
    }

    @Override
    protected void onFragmentInitFinished() {
        super.onFragmentInitFinished();
        getFeed(ticketMasterGetAllEvents);

        //Observe for SearchView queries being submitted - coming from main activity
        mTransactionViewModel.searchQueryTabTwo.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(final String s) {
                String search = s.trim();
                String query = ticketMasterSearchEvents.replace("{search}", search);

                getFeed(query);
            }
        });

        //Method called when we refresh the recycler view
        mBinding.swipeRefreshEventfeed.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefresh = true;
                getFeed(ticketMasterGetAllEvents);
            }
        });

        //Get max order id and save the event
        mEventViewModel.getEventOrderID().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(final Long aLong) {

                if (mEventFeedDetails.size() != 0) {

                    String rawDateFormat = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventLocalDate()
                            .trim();
                    String rawTimeFormat = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventLocalTime()
                            .trim();

                    String rawDateString = rawDateFormat + " · " + rawTimeFormat + " GMT";

                    long futureMillis = getTimeStamp(rawTimeFormat, rawDateFormat);

                    if (futureMillis != 0) {
                        long notificationId = aLong + 1;
                        long eventOrderId = aLong + 1;
                        String eventTitle = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventName();
                        String eventUrl = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventUrl();
                        String eventLocation = mEventFeedDetails.get(adapterPositionOfItemSelected)
                                .getEventLocationName();
                        String imageUrl = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventImage16_9();

                        long millisAtTimeOfCreation = futureMillis - System.currentTimeMillis();

                        int defaultColor = AppHelperClass.getDefaultColorId(getActivity());

                        Event event = new Event(eventOrderId, futureMillis, millisAtTimeOfCreation, eventTitle,
                                DEFAULT_IMG_ID, defaultColor, rawDateString);
                        event.setNotificationId(notificationId);
                        event.setEventUrl(eventUrl);
                        event.setEventLocation(eventLocation);
                        event.setImgUrl(imageUrl);
                        event.setImageLoadedFromUrl(true);

                        mEventViewModel.addEvent(event);

                        Toast.makeText(getActivity(), "Event Successfully Added!", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getActivity(), "Invalid Date - Cannot add to countdown", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }

        });
    }

    @Override
    public void onBottomSheetItemSelected(final int position) {
        switch (position) {

            //Add event to countdown selected
            case 1:
                //Query for the max order id so that we can add new event at correct position in recycler view
                mEventViewModel.queryMaxOrderID();
                break;

            //Share event selected
            case 2:
                shareEvent();
                break;

            //Browse event selected
            case 3:
                browseInternetWithUrl();
                break;
        }
    }

    private void shareEvent() {
        String url = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventUrl();
        String eventTitle = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventName();

        Intent share = new Intent(android.content.Intent.ACTION_SEND);

        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Share Event!");

        if (URLUtil.isValidUrl(url)) {
            share.putExtra(Intent.EXTRA_TEXT, url);
        } else {
            share.putExtra(Intent.EXTRA_TEXT, eventTitle);
        }

        // Add data to the intent, the receiving app will decide
        // what to do with it.

        startActivity(Intent.createChooser(share, "Share Event!"));

    }

    private void browseInternetWithUrl() {
        String url = mEventFeedDetails.get(adapterPositionOfItemSelected).getEventUrl();

        //Check if URL is valid
        if (URLUtil.isValidUrl(url)) {
            Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browseIntent);
        } else {
            Toast.makeText(getActivity(), "The URL is not valid for this event", Toast.LENGTH_SHORT).show();
        }

    }

    private void getFeed(String Url) {

        TicketMasterInterface ticketMasterInterface = RetrofitClient.getClient().create(TicketMasterInterface.class);

        Call<Ticket> call = ticketMasterInterface.getTicketInfoGB(Url);

        if (!onRefresh) {
            mBinding.swipeRefreshEventfeed.setRefreshing(true);
        }

        call.enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(final Call<Ticket> call, final Response<Ticket> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Unable to retrieve data" + response.message(), Toast.LENGTH_LONG)
                            .show();

                } else {

                    try {
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

                        //Add a click listener to the items of the recyclerVIew
                        mFeedAdapter = new EventFeedAdapter(getActivity(), mEventFeedDetails,
                                new EventFeedClickListener() {
                                    @Override
                                    public void onClick(final int position) {
                                        adapterPositionOfItemSelected = position;
                                        showBottomSheetDialog();
                                    }
                                });

                        mBinding.eventFeedRecycler.setAdapter(mFeedAdapter);

                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();

                        mBinding.swipeRefreshEventfeed.setRefreshing(false);

                    } catch (Exception e) {
                        e.printStackTrace();
                        mBinding.swipeRefreshEventfeed.setRefreshing(false);
                        Toast.makeText(getActivity(), "An ERROR HAS OCCURED!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(final Call<Ticket> call, final Throwable t) {
                Toast.makeText(getActivity(), "An ERROR HAS OCCURED!" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialogFeedFragment bottomSheet = new BottomSheetDialogFeedFragment();
        bottomSheet.setFeedFragmentBottomSheetListener(this);
        assert getFragmentManager() != null;
        bottomSheet.show(getFragmentManager().beginTransaction(), "EXECUTE");
    }

    private long getTimeStamp(String localTime, String date) {

        try {
            String[] hourMin = localTime.split(":");
            int hour = Integer.parseInt(hourMin[0]);
            int min = Integer.parseInt(hourMin[1]);

            String[] eventDate = date.split("-");
            int year = Integer.parseInt(eventDate[0]);
            int month = Integer.parseInt(eventDate[1]) - 1; //-1 because JANUARY is zero-based.
            int day = Integer.parseInt(eventDate[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, min, 0);

            return calendar.getTimeInMillis();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
