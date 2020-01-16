package project.awesomecountdown;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import project.awesomecountdown.AddEditActivity.ClickHandler;
import project.awesomecountdown.databinding.ActivityMainBinding;

public class MainActivity extends ModelActivity implements MyConstants {

    private ActivityMainBinding mMainBinding;

    private MyConstants mMyConstants;

    private Toolbar mToolbar;

    private ClickHandler mClickHandler;

    private DataTransactionViewModel mTransactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivityInitProcess();
    }

    @Override
    protected void onCreateInstances() {
        super.onCreateInstances();
        mMyConstants = new MainActivity();
        mClickHandler = new ClickHandler(this);
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();
        mMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        mMainBinding.setClickListener(mClickHandler);

        mToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mToolbar);

        //Connect state pager to view with special state pager adapter
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.MainActivity_ViewPager);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setAdapter(pagerAdapter);

    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mTransactionViewModel = ViewModelProviders.of(this).get(DataTransactionViewModel.class);

        mTransactionViewModel.showUndoSnackBar.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean aBoolean) {
                if (aBoolean){

                    Snackbar snackbar = Snackbar.make(mMainBinding.eventCoordinatorLayout, "Undo Delete", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    mTransactionViewModel.setUndoEventDelete(true);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorAccent));
                    snackbar.show();
                }

            }
        });
    }

    @Override
    protected void onActivityInitFinished() {
        super.onActivityInitFinished();

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    public class ClickHandler {

        Context mContext;

        public ClickHandler(final Context context) {
            mContext = context;
        }

        public void fabClicked(View view) {
            startAddEditEventActivity();

        }
    }

    private void startAddEditEventActivity(){
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        startActivityForResult(intent, mMyConstants.REQUEST_NEW_EVENT);
    }

    //State pager adapter for fragments
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { //Number of pages
            return 2;
        }

        @Override
        public Fragment getItem(final int position) { //fragment to be displayed
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new ArchiveFragment();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.menu_home_removeAll) {
            mTransactionViewModel.setDeleteAllEventsSelected(true);
        }
        return super.onOptionsItemSelected(item);
    }
}

