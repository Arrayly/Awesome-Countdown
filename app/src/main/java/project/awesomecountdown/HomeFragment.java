package project.awesomecountdown;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import project.awesomecountdown.EventAdapter.EventClickedListener;
import project.awesomecountdown.EventAdapter.EventExpiredListener;
import project.awesomecountdown.databinding.FragmentHomeBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends ModelFragment
        implements MyConstants, BottomSheetDialogHomeFragment.BottomSheetListener {

    private FragmentHomeBinding mHomeBinding;

    private List<Event> sortedEventList = new ArrayList<>();

    private ArrayList<Event> recentlyDeletedEvent = new ArrayList<>();

    private ArrayList<Long> deletedEventIDs = new ArrayList<>();

    private EventAdapter mEventAdapter;

    private MyConstants mConstants;

    private EventViewModel mEventViewModel;

    private DataTransactionViewModel mTransactionViewModel;

    private ItemTouchHelper mItemTouchHelper;

    private long chosenEventId;

    private int chosenEventPosition;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mHomeBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startFragmentInitProcess();
    }


    @Override
    protected void onCreateInstances() {
        super.onCreateInstances();
        mEventAdapter = new EventAdapter(getActivity());
        mConstants = new HomeFragment();
        mItemTouchHelper = new ItemTouchHelper(mSimpleCallback);
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mHomeBinding.eventRecycler.setLayoutManager(layoutManager);
        mHomeBinding.eventRecycler.setAdapter(mEventAdapter);
        mItemTouchHelper.attachToRecyclerView(mHomeBinding.eventRecycler);
    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(DataTransactionViewModel.class);
        mEventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);

        mTransactionViewModel.undoEventDelete.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean aBoolean) {
                if (aBoolean) {
                    undoDelete();
                }
            }
        });

        mTransactionViewModel.deleteAllEventsSelected.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean aBoolean) {
                if (aBoolean) {
                    sortedEventList.clear();
                    mEventViewModel.deleteAllEvents();
                    mEventAdapter.notifyDataSetChanged();
                }
            }
        });

        mEventViewModel.getEvent().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(final List<Event> events) {
                sortedEventList.clear();
                sortedEventList.addAll(events);
                mEventAdapter.submitList(events);
                mEventAdapter.notifyDataSetChanged();
                ifEmptyRecyclerViewLoadAnimation();
            }
        });

        mTransactionViewModel.viewPagerPosition.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                if (integer == 1 || integer == 2) {
                    updateDatabase();
                }
            }
        });
    }

    @Override
    protected void onFragmentInitFinished() {
        super.onFragmentInitFinished();

        mEventAdapter.setEventClickedListener(new EventClickedListener() {
            @Override
            public void onClick(int position) {
                chosenEventPosition = position;
                chosenEventId = sortedEventList.get(position).getID();
                showBottomSheetDialog();

            }
        });

        mEventAdapter.setEventExpiredListener(new EventExpiredListener() {
            @Override
            public void onExpired(final long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteExpiredEvent(id);
                    }
                }, 100);
            }
        });

    }

    private void ifEmptyRecyclerViewLoadAnimation() {
        if (sortedEventList.size() == 0) {
            mHomeBinding.homeFragEmptyRvAnimation.setVisibility(View.VISIBLE);
        } else {
            mHomeBinding.homeFragEmptyRvAnimation.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnBottomSheetSelected(final int position) {
        switch (position) {

            //Edit Event
            case 0:
                editEvent();
                break;

            //Delete Event
            case 1:
                deleteEvent();
                break;
        }
    }

    private void editEvent() {
        Intent intent = new Intent(getActivity(), AddEditActivity.class);
        intent.putExtra(mConstants.CHOSEN_EVENT_ID, chosenEventId);
        startActivityForResult(intent, mConstants.REQUEST_EDIT_EVENT);

    }

    private void deleteEvent() {
        recentlyDeletedEvent.clear();
        recentlyDeletedEvent.add(0, sortedEventList.get(chosenEventPosition));
        deletedEventIDs.add(0, sortedEventList.get(chosenEventPosition).getID());
        sortedEventList.remove(chosenEventPosition);
        ifEmptyRecyclerViewLoadAnimation();
        mEventAdapter.submitList(sortedEventList);
        mEventAdapter.notifyDataSetChanged();
        showUndoSnackBar();
    }

    private void deleteExpiredEvent(long id) {

        ListIterator<Event> ev_iter = sortedEventList.listIterator();
        while (ev_iter.hasNext()) {
            Event ev = ev_iter.next();
            if (ev.getID() == id) {
                deletedEventIDs.add(id);

                long millisAtExpiry = System.currentTimeMillis();

                ExpiredEvents expiredEvents = new ExpiredEvents(ev.getEventTitle(), ev.getMillisLeft(),
                        ev.getImgUrl(), ev.isImageLoadedFromUserPhone(), ev.isImageLoadedFromUrl(), ev.getImageId(),
                        ev.getTextColorId(), millisAtExpiry,ev.getDateRawString());
                if (ev.isLocationSet() && !ev.getEventLocation().isEmpty()){
                    expiredEvents.setLocationSet(true);
                    expiredEvents.setEventLocation(ev.getEventLocation());
                }

                mEventViewModel.addExpiredEvent(expiredEvents);
                ev_iter.remove();

                mEventAdapter.submitList(sortedEventList);
                mEventAdapter.notifyDataSetChanged();
            }
        }


    }

    private void undoDelete() {
        try {
            deletedEventIDs.remove(0);
            sortedEventList.add(chosenEventPosition, recentlyDeletedEvent.get(0));
            mEventAdapter.submitList(sortedEventList);
            mEventAdapter.notifyDataSetChanged();
            ifEmptyRecyclerViewLoadAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUndoSnackBar() {
        mTransactionViewModel.setShowUndoSnackBar(true);
    }

    private void showBottomSheetDialog() {
        BottomSheetDialogHomeFragment bottomSheetDialogHomeFragment = new BottomSheetDialogHomeFragment();
        bottomSheetDialogHomeFragment.setListener(this);
        bottomSheetDialogHomeFragment.show(getFragmentManager().beginTransaction(), "EXECUTE");
    }


    ItemTouchHelper.SimpleCallback mSimpleCallback = new SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull final RecyclerView recyclerView,
                @NonNull final RecyclerView.ViewHolder viewHolder,
                @NonNull final RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(sortedEventList, fromPosition, toPosition);
            mHomeBinding.eventRecycler.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int direction) {

        }

        @Override
        public void clearView(@NonNull final RecyclerView recyclerView,
                @NonNull final RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            sortList();

        }
    };

    private void sortList() {
        for (int x = 0; x < sortedEventList.size(); x++) {
            sortedEventList.get(x).setEventOrderId(sortedEventList.size() - x);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ifEmptyRecyclerViewLoadAnimation();
        updateDatabase();
        checkIfEventsExpiredNotifyUser();
    }

    private void checkIfEventsExpiredNotifyUser() {
        if (deletedEventIDs.size() > 0){
            mTransactionViewModel.numberOfExpiredEventsSinceLastVisit.setValue(deletedEventIDs.size());
            deletedEventIDs.clear();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        updateDatabase();

    }

    private void updateDatabase() {
        mEventViewModel.updateEvent(sortedEventList);

        if (deletedEventIDs.size() != 0) {
            for (int x = 0; x < deletedEventIDs.size(); x++) {
                mEventViewModel.deleteEventById(deletedEventIDs.get(x));
            }
        }
    }
}

