package project.awesomecountdown;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import project.awesomecountdown.databinding.ActivityAddEditBinding;
import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

public class AddEditActivity extends ModelActivity implements MyConstants {

    private ActivityAddEditBinding mBinding;

    private ClickHandler mClickHandler;

    private MyConstants mConstants;

    private int mYear, mMonth, mDay, mHour, mMinute, revealX, revealY, randomImageButtonClickedTimes, colorDefault;

    private CharSequence charInput = "";

    private EventViewModel mViewModel;

    private long futureTime, millisAtTimeOfCreation;

    private String chosenTitle;

    private List<Event> mEventList = new ArrayList<>();

    private boolean isEventEditing, isAlertSetInActivity, isAlertSetInDatabase, isExpiredEventBeingUpdated,
            savedInstanceIsNull;

    private long eventEditId, eventOrderId, notificationId, eventExpiredId;

    private View rootLayout;

    private int[] randomImageId = new int[7];


    private void showDatePickerDialog() {
        // Get Current Date
        Calendar c = Calendar.getInstance();
        int yr = c.get(Calendar.YEAR);
        int mth = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;

                        updateDateUI();

                    }
                }, yr, mth, day);
        datePickerDialog.show();
        mBinding.inputDateEtext.clearFocus();
    }

    private void showTimePickerDialog() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        updateTimeUI();
                    }
                }, hr, min, false);
        timePickerDialog.show();
        mBinding.inputTimeEtext.clearFocus();

    }


    @Override
    protected void onCreateInstances() {
        super.onCreateInstances();
        mClickHandler = new ClickHandler(AddEditActivity.this);
        mConstants = new AddEditActivity();
    }

    @Override
    protected void onViewInit() {
        super.onViewInit();

        //Load up array with out image references
        for (int i = 0; i < 7; i++) {
            randomImageId[i] = getResources()
                    .getIdentifier("drawable/" + "add_edit_pic_" + i, null, getPackageName());
        }

        colorDefault = ContextCompat.getColor(AddEditActivity.this, R.color.white);

        mBinding = DataBindingUtil.setContentView(AddEditActivity.this, R.layout.activity_add_edit);

        mBinding.addEditCustomImage.setImageResource(randomImageId[0]);

        mBinding.setClickListener(mClickHandler);
        rootLayout = findViewById(R.id.root_layout);
        if (!savedInstanceIsNull) {
            rootLayout.setVisibility(View.GONE);
        }
        Toolbar toolbar = findViewById(R.id.AddEdit_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        //Set random image in our cardView

        getWindow().setNavigationBarColor(getResources().getColor(R.color.app_theme_buttons));
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_buttons));
    }

    @Override
    protected void onBindViewModel() {
        super.onBindViewModel();
        mViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
    }

    @Override
    protected void onActivityInitFinished() {
        super.onActivityInitFinished();
        checkIntent();

        mBinding.inputTimeEtext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    showTimePickerDialog();
                }
            }
        });

        mBinding.inputDateEtext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog();
                }
            }
        });

        mBinding.inputNameEtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                charInput = s;
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });

        mBinding.alertSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                isAlertSetInActivity = isChecked;
                Toast.makeText(AddEditActivity.this, "Alert set Succesfully!", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getEventOrderID().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(final Long aLong) {
                eventOrderId = aLong + 1;
                notificationId = aLong + 1;
                saveData();
            }
        });

        mViewModel.getEventByIdResult.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(final List<Event> events) {
                processDbResult(null, events);
            }
        });

        mViewModel.getExpiredEventByIdResult.observe(this, new Observer<List<ExpiredEvents>>() {
            @Override
            public void onChanged(final List<ExpiredEvents> expiredEvents) {
                processDbResult(expiredEvents, null);
            }
        });


    }

    private void processDbResult(final List<ExpiredEvents> expiredEvents, final List<Event> currentEvent) {
        mEventList.clear();
        long millis = 0;

        if (expiredEvents != null) {

            for (ExpiredEvents expired : expiredEvents) {
                charInput = expired.getEventTitle();
                eventExpiredId = expired.getID();
                millis = expired.getMillisLeft();
            }

            isExpiredEventBeingUpdated = true;
            setUpUi(millis);


        } else if (currentEvent != null) {
            mEventList.addAll(currentEvent);

            for (Event event : currentEvent) {
                charInput = event.getEventTitle();
                eventEditId = event.getID();
                eventOrderId = event.getEventOrderId();
                isAlertSetInDatabase = event.isAlertSet();
                notificationId = event.getNotificationId();
                millis = event.getMillisLeft();
            }

            if (isAlertSetInDatabase) {
                isAlertSetInActivity = true;
                mBinding.alertSwitch.toggle();
            }

            setUpUi(millis);

        }


    }

    private void setUpUi(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        updateDateUI();
        updateTimeUI();
        updateOtherUI();

    }


    private void checkIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(CHOSEN_EVENT_ID)) {
            setTitle("Edit Event");
            long id = intent.getLongExtra(mConstants.CHOSEN_EVENT_ID, 0);
            if (id != 0) {
                isEventEditing = true;
                getEventFromDb(id);
            }
        } else if (intent.hasExtra(CHOSEN_EXPIRED_EVENT_ID)) {
            setTitle("Update Event");
            long id = intent.getLongExtra(CHOSEN_EXPIRED_EVENT_ID, 0);
            if (id != 0) {
                getExpiredEventFromDb(id);
            }
        } else if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
                && savedInstanceIsNull) {
            rootLayout.setVisibility(View.INVISIBLE);
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            processActivityInfo(revealX, revealY);

        }
    }

    private void processActivityInfo(final int revealX, final int revealY) {
        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    revealActivity(revealX, revealY);
                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void revealActivity(final int revealX, final int revealY) {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils
                .createCircularReveal(rootLayout, revealX, revealY, 0, finalRadius);
        circularReveal.setDuration(300);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);

        circularReveal.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                FadeOutView();
            }

            @Override
            public void onAnimationCancel(final Animator animation) {

            }

            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        circularReveal.start();

    }

    private void FadeOutView() {
        YoYo.with(Techniques.FadeOut)
                .duration(300)
                .withListener(new AnimatorListener() {
                    @Override
                    public void onAnimationStart(final Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        rootLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(final Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(final Animator animation) {

                    }
                })
                .playOn(rootLayout);
    }


    private void getEventFromDb(final long id) {
        mViewModel.getEventById(id);
    }

    private void getExpiredEventFromDb(final long id) {
        mViewModel.getExpiredEventById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceIsNull = true;
        } else {
            savedInstanceIsNull = false;
        }

        startActivityInitProcess();

        if (savedInstanceState != null) {
            loadDestroyedData(savedInstanceState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

            case R.id.finish_edit:
                validationProcessor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unRevealActivity() {
        HideSoftKeyboard.hideKeyboard(this);
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    private void validationProcessor() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, mHour, mMinute, DEFAULT_VALUE);
        long chosenMillis = c.getTimeInMillis();
        long currentMillis = System.currentTimeMillis();
        long diff = chosenMillis - currentMillis;
        if (diff <= 0) {
            Toast.makeText(this, "Invalid End Date. End Date Must Be After Start Date", Toast.LENGTH_SHORT).show();
        } else if (charInput.length() == 0) {
            mBinding.inputNameEtext.setError("Error");
        } else {
            chosenTitle = charInput.toString().trim();
            futureTime = chosenMillis;
            millisAtTimeOfCreation = futureTime - System.currentTimeMillis();
            if (!isEventEditing) {
                mViewModel.queryMaxOrderID();
            } else {
                updateEventDb();
            }
        }

        Log.i("TEST", "CURRENT MILLIS: " + currentMillis);
        Log.i("TEST", "FUTURE MILLIS: " + chosenMillis);

    }

    private void updateEventDb() {
        Event event = new Event(eventOrderId, futureTime, millisAtTimeOfCreation, chosenTitle);
        event.setID(eventEditId);

        //Cancel notification
        if (!isAlertSetInActivity && isAlertSetInDatabase) {
            event.setAlertSet(false);
            cancelNotification();
            event.setAlertSet(false);

            //update Notification
        } else {
            setNotification();
        }
        mViewModel.updateEditedEvent(event);
        unRevealActivity();


    }


    private void saveData() {
        Event event = new Event(eventOrderId, futureTime, millisAtTimeOfCreation, chosenTitle);
        event.setNotificationId(notificationId);
        if (isAlertSetInActivity) {
            event.setAlertSet(true);
            setNotification();
        }

        mViewModel.addEvent(event);

        if (isExpiredEventBeingUpdated) {
            mViewModel.deleteExpiredEventById(eventExpiredId);
        }

        unRevealActivity();
    }


    private void setNotification() {

        //Require a handler because finish() is fired before the alarm intent is registered
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AddEditActivity.this, ReminderBroadcast.class);
                intent.putExtra(BROADCAST_TITLE, chosenTitle);
                intent.putExtra(BROADCAST_ID_IDENTITY, notificationId);

                Log.i("BROADCAST", "setNotification " + notificationId + " Event TITLE: " + chosenTitle);

                PendingIntent pendingIntent = PendingIntent
                        .getBroadcast(AddEditActivity.this, (int) notificationId, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, futureTime, pendingIntent);
            }
        }, 300);
    }

    private void cancelNotification() {

        //Require a handler because finish() is fired before the alarm intent is registered
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(AddEditActivity.this, ReminderBroadcast.class);
                Log.i("BROADCAST", "cancelNotification: ID = " + notificationId);
                intent.putExtra(BROADCAST_ID_IDENTITY, notificationId);

                PendingIntent pendingIntent = PendingIntent
                        .getBroadcast(AddEditActivity.this, (int) notificationId, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

            }
        }, 300);

    }


    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mYear", mYear);
        outState.putInt("mMonth", mMonth);
        outState.putInt("mDay", mDay);
        outState.putInt("mHour", mHour);
        outState.putInt("mMinute", mMinute);
        outState.putInt("randomImageButtonClickedTimes", randomImageButtonClickedTimes);

        outState.putCharSequence("charInput", charInput);

        outState.putLong("eventEditId", eventEditId);
        outState.putLong("eventOrderId", eventOrderId);
        outState.putLong("notificationId", notificationId);
        outState.putLong("eventExpiredId", eventExpiredId);
        outState.putLong("millisAtTimeOfCreation", millisAtTimeOfCreation);
        outState.putLong("futureTime", futureTime);

        outState.putBoolean("isEventEditing", isEventEditing);
        outState.putBoolean("isAlertSetInActivity", isAlertSetInActivity);
        outState.putBoolean("isAlertSetInDatabase", isAlertSetInDatabase);
        outState.putBoolean("isExpiredEventBeingUpdated", isExpiredEventBeingUpdated);
    }

    private void loadDestroyedData(final Bundle savedInstanceState) {
        mYear = savedInstanceState.getInt("mYear", mConstants.DEFAULT_VALUE);
        mMonth = savedInstanceState.getInt("mMonth", mConstants.DEFAULT_VALUE);
        mDay = savedInstanceState.getInt("mDay", mConstants.DEFAULT_VALUE);
        mHour = savedInstanceState.getInt("mHour", mConstants.DEFAULT_VALUE);
        mMinute = savedInstanceState.getInt("mMinute", mConstants.DEFAULT_VALUE);
        randomImageButtonClickedTimes = savedInstanceState
                .getInt("randomImageButtonClickedTimes", mConstants.DEFAULT_VALUE);

        charInput = savedInstanceState.getCharSequence("charInput", charInput);

        eventEditId = savedInstanceState.getLong("eventEditId", mConstants.DEFAULT_VALUE);
        eventOrderId = savedInstanceState.getLong("eventOrderId", mConstants.DEFAULT_VALUE);
        notificationId = savedInstanceState.getLong("notificationId", mConstants.DEFAULT_VALUE);
        eventExpiredId = savedInstanceState.getLong("eventExpiredId", mConstants.DEFAULT_VALUE);
        millisAtTimeOfCreation = savedInstanceState.getLong("millisAtTimeOfCreation", mConstants.DEFAULT_VALUE);
        futureTime = savedInstanceState.getLong("futureTime", mConstants.DEFAULT_VALUE);

        isEventEditing = savedInstanceState.getBoolean("isEventEditing", isEventEditing);
        isAlertSetInActivity = savedInstanceState.getBoolean("isAlertSetInActivity", isAlertSetInActivity);
        isAlertSetInDatabase = savedInstanceState.getBoolean("isAlertSetInDatabase", isAlertSetInDatabase);
        isExpiredEventBeingUpdated = savedInstanceState
                .getBoolean("isExpiredEventBeingUpdated", isExpiredEventBeingUpdated);

        updateDateUI();
        updateTimeUI();
        updateOtherUI();

    }

    public class ClickHandler {

        Context mContext;

        public ClickHandler(final Context context) {
            mContext = context;
        }

        public void randomBackground(View view) {
            randomImageButtonClickedTimes++;
            if (randomImageButtonClickedTimes >= randomImageId.length) {
                randomImageButtonClickedTimes = 0;
            }
            mBinding.addEditCustomImage.setImageResource(randomImageId[randomImageButtonClickedTimes]);
        }

        public void pickColorButton(View view) {
            showColorPickerDialog();
        }

        private void showColorPickerDialog() {
            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(AddEditActivity.this, colorDefault,
                    new OnAmbilWarnaListener() {
                        @Override
                        public void onCancel(final AmbilWarnaDialog dialog) {

                        }

                        @Override
                        public void onOk(final AmbilWarnaDialog dialog, final int color) {
                            colorDefault = color;
                            mBinding.colorBtn.setBackgroundColor(colorDefault);
                        }
                    });

            ambilWarnaDialog.show();


        }


    }

    private void updateDateUI() {
        mBinding.inputDateEtext.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
    }

    private void updateTimeUI() {
        mBinding.inputTimeEtext.setText(mHour + ":" + mMinute);
    }

    private void updateOtherUI() {
        mBinding.inputNameEtext.setText(charInput);
        mBinding.addEditCustomImage.setImageResource(randomImageId[randomImageButtonClickedTimes]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("cycle", "onDestroy: ");
    }
}