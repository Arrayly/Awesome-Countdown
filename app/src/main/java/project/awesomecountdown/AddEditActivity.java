package project.awesomecountdown;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat.AutoSizeTextType;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import project.awesomecountdown.databinding.ActivityAddEditBinding;

public class AddEditActivity extends ModelActivity implements MyConstants {

    private ActivityAddEditBinding mBinding;

    private ClickHandler mClickHandler;

    private MyConstants mConstants;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private CharSequence charInput = "";

    private EventViewModel mViewModel;

    private long futureTime;

    private String chosenTitle;

    private List<Event> mEventList = new ArrayList<>();

    private boolean isEventEditing, isAlertSetInActivity, isAlertSetInDatabase, isExpiredEventBeingUpdated;

    private long eventEditId, eventOrderId, notificationId, eventExpiredId;


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
        mBinding = DataBindingUtil.setContentView(AddEditActivity.this, R.layout.activity_add_edit);
        mBinding.setClickListener(mClickHandler);
        Toolbar toolbar = findViewById(R.id.newEvent_toolbar);
        setSupportActionBar(toolbar);
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
        updateEventNameUI();

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
        }
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
        startActivityInitProcess();

        if (savedInstanceState != null) {
            loadDestroyedData(savedInstanceState);
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

            case R.id.finish_edit:
                validationProcessor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishActivity() {
        Intent replyIntent = new Intent();
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    private void validationProcessor() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, mHour, mMinute,DEFAULT_VALUE);
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
        Event event = new Event(eventOrderId, futureTime, chosenTitle);
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
        finishActivity();


    }


    private void saveData() {
        Event event = new Event(eventOrderId, futureTime, chosenTitle);
        if (isAlertSetInActivity) {
            event.setAlertSet(true);
            event.setNotificationId(notificationId);
            setNotification();
        }

        mViewModel.addEvent(event);

        if (isExpiredEventBeingUpdated){
            mViewModel.deleteExpiredEventById(eventExpiredId);
        }

        finishActivity();
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

        outState.putCharSequence("charInput", charInput);

        outState.putLong("eventEditId", eventEditId);
        outState.putLong("eventOrderId", eventOrderId);
        outState.putLong("notificationId", notificationId);
        outState.putLong("eventExpiredId", eventExpiredId);

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

        charInput = savedInstanceState.getCharSequence("charInput", charInput);

        eventEditId = savedInstanceState.getLong("eventEditId", mConstants.DEFAULT_VALUE);
        eventOrderId = savedInstanceState.getLong("eventOrderId", mConstants.DEFAULT_VALUE);
        notificationId = savedInstanceState.getLong("notificationId", mConstants.DEFAULT_VALUE);
        eventExpiredId = savedInstanceState.getLong("eventExpiredId", mConstants.DEFAULT_VALUE);

        isEventEditing = savedInstanceState.getBoolean("isEventEditing", isEventEditing);
        isAlertSetInActivity = savedInstanceState.getBoolean("isAlertSetInActivity", isAlertSetInActivity);
        isAlertSetInDatabase = savedInstanceState.getBoolean("isAlertSetInDatabase", isAlertSetInDatabase);
        isExpiredEventBeingUpdated = savedInstanceState.getBoolean("isExpiredEventBeingUpdated", isExpiredEventBeingUpdated);

        updateDateUI();
        updateTimeUI();
        updateEventNameUI();

    }

    public class ClickHandler {

        Context mContext;

        public ClickHandler(final Context context) {
            mContext = context;
        }
    }

    private void updateDateUI() {
        mBinding.inputDateEtext.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
    }

    private void updateTimeUI() {
        mBinding.inputTimeEtext.setText(mHour + ":" + mMinute);
    }

    private void updateEventNameUI() {
        mBinding.inputNameEtext.setText(charInput);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("cycle", "onDestroy: ");
    }
}
