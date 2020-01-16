package project.awesomecountdown;


import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import project.awesomecountdown.ExpiredEventsAdapter.ExpiredEventClickListener;
import project.awesomecountdown.databinding.FragmentArchiveBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArchiveFragment extends ModelFragment implements MyConstants {

    private FragmentArchiveBinding mArchiveBinding;

    private List<ExpiredEvents> mExpiredEvents = new ArrayList<>();

    private EventViewModel mEventViewModel;

    private ExpiredEventsAdapter mAdapter;

    private long chosenExpiredEventId;

    public ArchiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mArchiveBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_archive, container, false);
        return mArchiveBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startFragmentInitProcess();
    }

    @Override
    protected void onCreateInstances() {
        super.onCreateInstances();
        mAdapter = new ExpiredEventsAdapter(getActivity());
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mArchiveBinding.archiveRecycler.setLayoutManager(layoutManager);
        mArchiveBinding.archiveRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mEventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);

        mEventViewModel.getExpiredEvents().observe(getViewLifecycleOwner(), new Observer<List<ExpiredEvents>>() {
            @Override
            public void onChanged(final List<ExpiredEvents> expiredEvents) {
                if (expiredEvents != null) {
                    mExpiredEvents.addAll(expiredEvents);
                    mAdapter.submitList(expiredEvents);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onFragmentInitFinished() {
        super.onFragmentInitFinished();

        mAdapter.setOnClickListener(new ExpiredEventClickListener() {
            @Override
            public void OnClick(final int position) {
                chosenExpiredEventId = mExpiredEvents.get(position).getID();
                Toast.makeText(getActivity(), "ID: " + chosenExpiredEventId, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
