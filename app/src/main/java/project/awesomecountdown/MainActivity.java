package project.awesomecountdown;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.ViewAnimationUtils;
import android.widget.Toast;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
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

    private static final int[] TAB_ICONS = {R.drawable.tab_layout_countdown_icon, R.drawable.tab_icon_event,
            R.drawable.tab_icon_expired};


    private ActivityMainBinding mMainBinding;

    private MyConstants mMyConstants;

    private Toolbar mToolbar;

    private ClickHandler mClickHandler;

    private DataTransactionViewModel mTransactionViewModel;

    private ViewPager mViewPager;

    private int viewPagerPosition;

    private TabLayout mTabLayout;

    private boolean feedFragmentInFocus;

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
            mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.tab_countdown));

            mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
                @Override
                public void onTabSelected(final Tab tab) {
                    final View view = tab.view;
                    YoYo.with(Techniques.Bounce).playOn(view);

                    //Tab layout indicator colors
                    int tabPosition = mTabLayout.getSelectedTabPosition();
                    switch (tabPosition) {
                        case 0:
                            mTabLayout.setSelectedTabIndicatorColor(
                                    ContextCompat.getColor(MainActivity.this, R.color.tab_countdown));
                            break;
                        case 1:
                            mTabLayout.setSelectedTabIndicatorColor(
                                    ContextCompat.getColor(MainActivity.this, R.color.tab_event));
                            break;
                        case 2:
                            mTabLayout.setSelectedTabIndicatorColor(
                                    ContextCompat.getColor(MainActivity.this, R.color.tab_expired));
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

        //Make collapsing toolbar constricted on first run
        mMainBinding.mainAcitivtyAppbar.setExpanded(false);
    }

    private void setUpTabIcons() {
        for (int x = 0; x < 3; x++) {
            mTabLayout.getTabAt(x).setIcon(TAB_ICONS[x]);
        }
    }


    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mTransactionViewModel = ViewModelProviders.of(this).get(DataTransactionViewModel.class);

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
                    mTransactionViewModel.viewPagerPosition.setValue(position);
                    if (position == 1) {
                        feedFragmentInFocus = true;
                        invalidateOptionsMenu();
                    } else {
                        feedFragmentInFocus = false;
                        invalidateOptionsMenu();
                    }


                }

                @Override
                public void onPageScrollStateChanged(final int state) {

                }
            });
        }

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

        mTransactionViewModel.numberOfExpiredEventsSinceLastVisit.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                if (integer > 0) {
                    Snackbar snackbar = Snackbar
                            .make(mMainBinding.eventCoordinatorLayout, "You have " + integer + " expired events",
                                    Snackbar.LENGTH_LONG)
                            .setAction("View", new OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    int currentTab = mViewPager.getCurrentItem();
                                    if (currentTab != 2) {
                                        mViewPager.setCurrentItem(2, true);
                                    }
                                }
                            }).setActionTextColor(getResources().getColor(R.color.colorAccent));
                    snackbar.show();
                }
            }
        });
    }


    public class ClickHandler {

        Context mContext;

        public ClickHandler(final Context context) {
            mContext = context;
        }

        public void fabClicked(final View view) {
            MainActivityTransitionAnimation transitionAnimation = new MainActivityTransitionAnimation();
            transitionAnimation.fadeOutFabandToolbar();
        }
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

        if (feedFragmentInFocus) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

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
                Toast.makeText(this, "Event successfully updated!", Toast.LENGTH_SHORT).show();
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

    private class MainActivityTransitionAnimation {

        private void fadeOutFabandToolbar() {
            YoYo.with(Techniques.FadeOut).duration(450).playOn(mMainBinding.mainAcitivtyAppbar);
            YoYo.with(Techniques.FadeOut).duration(100).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    super.onAnimationEnd(animation);
                    revealButton();
                }
            }).playOn(mMainBinding.MainActivityFab);
        }


        private void revealButton() {
            mMainBinding.MainActivityFab.setElevation(0f);
            mMainBinding.mainRootLayout.setVisibility(View.VISIBLE);
            int cx = mMainBinding.mainRootLayout.getWidth();
            int cy = mMainBinding.mainRootLayout.getHeight();

            int x = (int) (AppHelperClass.getFabWidth(MainActivity.this) / 2 + mMainBinding.MainActivityFab.getX());
            int y = (int) (AppHelperClass.getFabWidth(MainActivity.this) / 2 + mMainBinding.MainActivityFab.getY());

            float finalRadius = Math.max(cx, cy) * 1.2f;

            Animator reveal = ViewAnimationUtils
                    .createCircularReveal(mMainBinding.mainRootLayout, x, y,
                            AppHelperClass.getFabWidth(MainActivity.this), finalRadius);
            reveal.setDuration(300);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    super.onAnimationEnd(animation);
                    startNewActivity();
                }
            });
            reveal.start();

        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainBinding.mainRootLayout.setVisibility(View.INVISIBLE);
        YoYo.with(Techniques.FadeIn).duration(500).playOn(mMainBinding.MainActivityFab);
        YoYo.with(Techniques.FadeIn).duration(300).playOn(mMainBinding.mainAcitivtyAppbar);
        mMainBinding.MainActivityFab.setElevation(10f);
    }
}

