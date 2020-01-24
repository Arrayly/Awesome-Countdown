package project.awesomecountdown;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import project.awesomecountdown.AddEditActivity.ClickHandler;
import project.awesomecountdown.databinding.ActivityMainBinding;

public class MainActivity extends ModelActivity implements MyConstants {

    private static final String TAB_TITLES[] = new String[]{"Countdown", "Events", "Expired"};

    private static final int[] TAB_ICONS = {R.drawable.tab_icon_countd, R.drawable.tab_icon_event,
            R.drawable.tab_icon_expired};


    private ActivityMainBinding mMainBinding;

    private MyConstants mMyConstants;

    private Toolbar mToolbar;

    private ClickHandler mClickHandler;

    private DataTransactionViewModel mTransactionViewModel;

    private ViewPager mViewPager;

    private int viewPagerPosition;

    private TabLayout mTabLayout;

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

        mToolbar = findViewById(R.id.customToolBar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }


        getWindow().setNavigationBarColor(getResources().getColor(R.color.app_theme_primary));
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_sub));

        //Connect state pager to view with special state pager adapter
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.MainActivity_ViewPager);

        if (mViewPager != null) {
            mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            mViewPager.setAdapter(pagerAdapter);
        }

        mTabLayout = findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            setUpTabIcons();
            mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this,R.color.tab_countdown));

            mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
                @Override
                public void onTabSelected(final Tab tab) {
                    final View view = tab.view;
                    YoYo.with(Techniques.Bounce).playOn(view);

                    //Tab layout indicator colors
                    int tabPosition = mTabLayout.getSelectedTabPosition();
                    switch (tabPosition){
                        case 0:
                            mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this,R.color.tab_countdown));
                            break;
                        case 1:
                            mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this,R.color.tab_event));
                            break;
                        case 2:
                            mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this,R.color.tab_expired));
                            break;
                    }

                }

                @Override
                public void onTabUnselected(final Tab tab) {
                }

                @Override
                public void onTabReselected(final Tab tab) {

                }
            });
        }

    }

    private void setUpTabIcons() {
        for (int x = 0; x<3; x++){
            mTabLayout.getTabAt(x).setIcon(TAB_ICONS[x]);
        }
    }


    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mTransactionViewModel = ViewModelProviders.of(this).get(DataTransactionViewModel.class);

        mTransactionViewModel.showUndoSnackBar.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean aBoolean) {
                if (aBoolean) {

                    Snackbar snackbar = Snackbar
                            .make(mMainBinding.eventCoordinatorLayout, "Undo Delete", Snackbar.LENGTH_LONG)
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

        if (mViewPager != null) {

            //Use this listener to identify which tabs we are on
            mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                        final int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(final int position) {
                    viewPagerPosition = position;

                }

                @Override
                public void onPageScrollStateChanged(final int state) {

                }
            });
        }
    }


    public class ClickHandler {

        Context mContext;

        public ClickHandler(final Context context) {
            mContext = context;
        }

        public void fabClicked(final View view) {

            startAddEditEventActivity(view);
        }
    }

    private void startAddEditEventActivity(View view) {

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");

        int revealX = view.getRight() - getDips(44);
        int revealY = view.getBottom() - getDips(44);

        Intent intent = new Intent(this, AddEditActivity.class);

        intent.putExtra(AddEditActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(AddEditActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());

        mTransactionViewModel.activityTransition.setValue(true);
        mTransactionViewModel.activityTransition.setValue(false);


    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    //State pager adapter for fragments
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() { //Number of pages
            return 3;
        }

        @Override
        public Fragment getItem(final int position) { //fragment to be displayed
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new EventFeedFragment();
                case 2:
                    return new ArchiveFragment();
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchIconQueryListener());

        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_NEW_EVENT) {
                Toast.makeText(this, "Event successfully created!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_EDIT_EVENT) {
                Toast.makeText(this, "Event successfuly updated!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Listen for text changes in the search bar
    private class SearchIconQueryListener implements OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(final String query) {
            switch (viewPagerPosition) {
                case 0:
                    mTransactionViewModel.searchQueryTabOne.setValue(query);
                    break;

                case 1:
                    mTransactionViewModel.searchQueryTabTwo.setValue(query);
                    break;

                case 2:
                    mTransactionViewModel.searchQueryTabThree.setValue(query);
                    break;
            }

            return false;
        }

        @Override
        public boolean onQueryTextChange(final String newText) {
            return false;
        }
    }
}

