package project.awesomecountdown;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
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

    private boolean isEventEditing;

    private long eventEditId;

    private long eventEditOrderId;

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

        mViewModel.getEventOrderID().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(final Long aLong) {
                saveData(aLong + 1);
            }
        });

        mViewModel.getEventByIdResult.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(final List<Event> events) {
                mEventList.clear();
                mEventList.addAll(events);
                long millis = 0;
                for (Event event : events) {
                    charInput = event.getEventTitle();
                    eventEditId = event.getID();
                    eventEditOrderId = event.getEventOrderId();
                    millis = event.getMillisLeft();
                }

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(millis);

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR);
                mMinute = c.get(Calendar.MINUTE);

                updateDateUI();
                updateTimeUI();
                updateEventNameUI();


            }
        });


    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(mConstants.CHOSEN_EVENT)) {
            setTitle("Edit Event");
            long id = intent.getLongExtra(mConstants.CHOSEN_EVENT, 0);
            if (id != 0) {
                isEventEditing = true;
                getEventFromDb(id);
            }
        }
    }

    private void getEventFromDb(final long id) {
        mViewModel.getEventById(id);
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
        c.set(mYear, mMonth, mDay, mHour, mMinute, mConstants.DEFAULT_VALUE);
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
            }else{
                updateEventDb();

            }
        }

        Log.i("TEST", "CURRENT MILLIS: " + currentMillis);
        Log.i("TEST", "FUTURE MILLIS: " + chosenMillis);

    }

    private void updateEventDb() {
        Event event = new Event(eventEditOrderId,futureTime,chosenTitle);
        event.setID(eventEditId);
        mViewModel.updateEditedEvent(event);
        finishActivity();

    }

    private void saveData(long eventOrderId) {
        Event event = new Event(eventOrderId, futureTime, chosenTitle);
        mViewModel.addEvent(event);
        finishActivity();
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
        outState.putBoolean("isEventEditing",isEventEditing);
        outState.putLong("eventEditId",eventEditId);
        outState.putLong("eventEditOrderId",eventEditOrderId);
    }

    private void loadDestroyedData(final Bundle savedInstanceState) {
        mYear = savedInstanceState.getInt("mYear", mConstants.DEFAULT_VALUE);
        mMonth = savedInstanceState.getInt("mMonth", mConstants.DEFAULT_VALUE);
        mDay = savedInstanceState.getInt("mDay", mConstants.DEFAULT_VALUE);
        mHour = savedInstanceState.getInt("mHour", mConstants.DEFAULT_VALUE);
        mMinute = savedInstanceState.getInt("mMinute", mConstants.DEFAULT_VALUE);
        charInput = savedInstanceState.getCharSequence("charInput", charInput);
        isEventEditing = savedInstanceState.getBoolean("isEventEditing", isEventEditing);
        eventEditId = savedInstanceState.getLong("eventEditId", mConstants.DEFAULT_VALUE);
        eventEditOrderId = savedInstanceState.getLong("eventEditOrderId", mConstants.DEFAULT_VALUE);


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
}
