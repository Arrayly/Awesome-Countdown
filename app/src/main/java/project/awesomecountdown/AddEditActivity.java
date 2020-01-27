package project.awesomecountdown;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract.Colors;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.ViewGroupBindingAdapter.OnAnimationEnd;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.squareup.picasso.Picasso;
import java.io.IOException;
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

    private int mYear, mMonth, mDay, mHour, mMinute, randomImageButtonClickedTimes, colorDefault,
            chosenImageId;

    private CharSequence charInput = "";

    private EventViewModel mViewModel;

    private long futureTime, millisAtTimeOfCreation;

    private String chosenTitle, imageUrl;

    private List<Event> mEventList = new ArrayList<>();

    private boolean isEventEditing, isAlertSetInActivity, isAlertSetInDatabase, isExpiredEventBeingUpdated,
            savedInstanceIsNull, imageLoadedFromUserPhone, imageLoadedFromUrl;

    private long eventEditId, eventOrderId, notificationId, eventExpiredId;

    private View rootLayout;

    private int[] randomImageIdArray = new int[7];

    int expanded = 0;

    private Context mContext;


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

        //Load up array with out image references
        for (int i = 0; i < 7; i++) {
            randomImageIdArray[i] = getResources()
                    .getIdentifier("drawable/" + "add_edit_pic_" + i, null, getPackageName());
        }

        if (savedInstanceIsNull) {
            YoYo.with(Techniques.FadeOutLeft).duration(300).playOn(mBinding.rootLayout2);
        } else {
            mBinding.rootLayout2.setVisibility(View.INVISIBLE);
        }

        colorDefault = AppHelperClass.getDefaultColorId(AddEditActivity.this);

        mBinding.addEditExpandableLayout.toggle();

        mBinding.setClickListener(mClickHandler);
        rootLayout = findViewById(R.id.root_layout);
        if (!savedInstanceIsNull) {
            rootLayout.setVisibility(View.GONE);
        }

        //Set random image in our cardView

        getWindow().setNavigationBarColor(getResources().getColor(R.color.app_theme_primary));
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_theme_sub));

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
                    YoYo.with(Techniques.Pulse)
                            .duration(400)
                            .playOn(mBinding.inputTimeLayout);
                    showTimePickerDialog();
                }
            }
        });

        mBinding.inputDateEtext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    YoYo.with(Techniques.Pulse)
                            .duration(400)
                            .playOn(mBinding.inputDateLayout);
                    showDatePickerDialog();
                }
            }
        });

        mBinding.inputNameEtext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    YoYo.with(Techniques.Pulse)
                            .duration(400)
                            .playOn(mBinding.inputNameLayout);
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
                if (isChecked) {
                    Toast.makeText(AddEditActivity.this, "Alert set Succesfully!", Toast.LENGTH_SHORT).show();
                    mBinding.alertSwitch.setChipBackgroundColorResource(R.color.tab_expired);
                } else {
                    mBinding.alertSwitch.setChipBackgroundColorResource(R.color.app_theme_buttons);
                }

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

    //Here we are retrieving the list of data from DB
    private void processDbResult(final List<ExpiredEvents> expiredEvents, final List<Event> currentEvent) {
        mEventList.clear();
        long millis = 0;

        //Expired Events loaded here
        if (expiredEvents != null) {

            for (ExpiredEvents expired : expiredEvents) {
                charInput = expired.getEventTitle();
                eventExpiredId = expired.getID();
                imageUrl = expired.getImageUrl();
                chosenImageId = expired.getImageId();
                millis = expired.getMillisLeft();
            }

            isExpiredEventBeingUpdated = true;
            setUpUi(millis);

            //Ongoing events loaded here
        } else if (currentEvent != null) {
            mEventList.addAll(currentEvent);

            for (Event event : currentEvent) {
                charInput = event.getEventTitle();
                eventEditId = event.getID();
                imageUrl = event.getImgUrl();
                eventOrderId = event.getEventOrderId();
                isAlertSetInDatabase = event.isAlertSet();
                notificationId = event.getNotificationId();
                chosenImageId = event.getImageId();

                millis = event.getMillisLeft();

            }

            setUpUi(millis);

        }

        if (isAlertSetInDatabase) {
            isAlertSetInActivity = true;
            mBinding.alertSwitch.toggle();
            mBinding.alertSwitch.setChipBackgroundColorResource(R.color.tab_expired);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(mBinding.addEditCustomImage);
            imageLoadedFromUrl = true;
        } else {

            loadDefaultImage();
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
            long id = intent.getLongExtra(mConstants.CHOSEN_EVENT_ID, 0);

            if (id != 0) {
                isEventEditing = true;
                getEventFromDb(id);
                mBinding.addEditBtnTextView.setText("Update");

            }
        } else if (intent.hasExtra(CHOSEN_EXPIRED_EVENT_ID)) {
            long id = intent.getLongExtra(CHOSEN_EXPIRED_EVENT_ID, 0);
            if (id != 0) {
                getExpiredEventFromDb(id);
                mBinding.addEditDeleteEventIcon.setVisibility(View.VISIBLE);
                mBinding.addEditBtnTextView.setText("Update");
            }

        } else {
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        mBinding.addEditCustomImage.setBackgroundResource(randomImageIdArray[0]);
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

        mContext = AddEditActivity.this;

        startActivityInitProcess();

        if (savedInstanceState != null) {
            loadDestroyedData(savedInstanceState);
        }
    }


    private void unRevealActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HideSoftKeyboard.hideKeyboard(AddEditActivity.this);
                onBackPressed();
            }
        }, 100);

    }


    private boolean validationProcessor() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth, mDay, mHour, mMinute, DEFAULT_VALUE);
        long chosenMillis = c.getTimeInMillis();
        long currentMillis = System.currentTimeMillis();
        long diff = chosenMillis - currentMillis;
        if (diff <= 0) {
            Toast.makeText(this, "Invalid End Date. End Date Must Be After Start Date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (charInput.length() == 0) {
            mBinding.inputNameEtext.setError("Please fill");
            return false;

        } else {
            chosenTitle = charInput.toString().trim();
            futureTime = chosenMillis;
            millisAtTimeOfCreation = futureTime - System.currentTimeMillis();

            //If validation successful, update DB and perform animation

            if (!isEventEditing) {
                mViewModel.queryMaxOrderID();
            } else {
                updateEventDb();
            }
        }

        return true;
    }

    private void updateEventDb() {
        Event event = new Event(eventOrderId, futureTime, millisAtTimeOfCreation, chosenTitle,
                chosenImageId, colorDefault,
                AppHelperClass.getRawDateString(mYear, mMonth, mDay, mHour, mMinute));
        event.setID(eventEditId);
        event.setImageLoadedFromUserPhone(imageLoadedFromUserPhone);
        event.setImageLoadedFromUrl(imageLoadedFromUrl);
        event.setImgUrl(imageUrl);

        //Cancel notification
        if (!isAlertSetInActivity && isAlertSetInDatabase) {
            event.setAlertSet(false);
            cancelNotification();

            //update Notification
        } else {
            setNotification();
        }
        mViewModel.updateEditedEvent(event);

    }


    private void saveData() {
        Event event = new Event(eventOrderId, futureTime, millisAtTimeOfCreation, chosenTitle,
                randomImageIdArray[randomImageButtonClickedTimes], colorDefault,
                AppHelperClass.getRawDateString(mYear, mMonth, mDay, mHour, mMinute));

        event.setImageLoadedFromUserPhone(imageLoadedFromUserPhone);
        event.setNotificationId(notificationId);
        if (isAlertSetInActivity) {
            event.setAlertSet(true);
            setNotification();
        }

        mViewModel.addEvent(event);

        if (isExpiredEventBeingUpdated) {
            mViewModel.deleteExpiredEventById(eventExpiredId);
        }
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
        outState.putInt("chosenImageId", chosenImageId);

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
        outState.putBoolean("imageLoadedFromUserPhone", imageLoadedFromUserPhone);
        outState.putBoolean("imageLoadedFromUrl", imageLoadedFromUrl);
    }

    private void loadDestroyedData(final Bundle savedInstanceState) {
        mYear = savedInstanceState.getInt("mYear", mConstants.DEFAULT_VALUE);
        mMonth = savedInstanceState.getInt("mMonth", mConstants.DEFAULT_VALUE);
        mDay = savedInstanceState.getInt("mDay", mConstants.DEFAULT_VALUE);
        mHour = savedInstanceState.getInt("mHour", mConstants.DEFAULT_VALUE);
        mMinute = savedInstanceState.getInt("mMinute", mConstants.DEFAULT_VALUE);
        chosenImageId = savedInstanceState.getInt("chosenImageId", mConstants.DEFAULT_VALUE);
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
        imageLoadedFromUrl = savedInstanceState.getBoolean("imageLoadedFromUrl", imageLoadedFromUrl);
        imageLoadedFromUserPhone = savedInstanceState
                .getBoolean("imageLoadedFromUserPhone", imageLoadedFromUserPhone);
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
            imageLoadedFromUrl = false;
            imageLoadedFromUserPhone = false;

            mBinding.addEditCustomImage.setImageDrawable(null);
            randomImageButtonClickedTimes++;

            chosenImageId = randomImageIdArray[randomImageButtonClickedTimes];

            if (randomImageButtonClickedTimes == randomImageIdArray.length - 1) {
                if (imageUrl != null) {
                    Picasso.get().load(imageUrl).into(mBinding.addEditCustomImage);
                    imageLoadedFromUrl = true;
                } else {
                    mBinding.addEditCustomImage.setBackgroundResource(chosenImageId);
                }
                randomImageButtonClickedTimes = 0;

            } else {

                mBinding.addEditCustomImage.setBackgroundResource(chosenImageId);
            }

            if (AppHelperClass.checkIfUserBitmapImageExists(mContext)) {
                AppHelperClass.deleteUserBitmapImage(mContext);
                mBinding.addEditCustomImage.setImageBitmap(null);
            }

            YoYo.with(Techniques.Pulse)
                    .duration(100)
                    .playOn(mBinding.backgroundBtn);


        }

        public void pickColorButton(View view) {
            showColorPickerDialog();
            YoYo.with(Techniques.Pulse)
                    .duration(100)
                    .playOn(mBinding.colorBtn);
        }

        public void addImageClicked(View view) {
            getImageFromUserPhone();
        }

        //Complete new Countdown set up
        public void createCountdownClicked(View view) {

            if (validationProcessor()) {
                ActivityTransitionAnimation transitionAnimation = new ActivityTransitionAnimation();

                transitionAnimation.animateButtonWidth();
                transitionAnimation.fadeOutTextShowProgressDialog();
                transitionAnimation.nextAction();
            }
        }

        public void expandIconClicked(View view) {
            expanded++;

            if (expanded == 1) {
                mBinding.addEditExpandableLayout.toggle();
                mBinding.expandingIcon.setBackgroundResource(R.drawable.minus);

            } else {
                mBinding.addEditExpandableLayout.toggle();
                mBinding.expandingIcon.setBackgroundResource(R.drawable.plus);
                expanded = 0;
            }

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
                            mBinding.chosenColorTextView.setTextColor(colorDefault);
                        }
                    });

            ambilWarnaDialog.show();

        }

        public void setAlertClicked(View view) {
            YoYo.with(Techniques.Pulse).duration(100)
                    .playOn(mBinding.alertSwitch);
        }

        public void deleteEventIconClicked(View view){
            mViewModel.deleteExpiredEventById(eventExpiredId);
            unRevealActivity();
        }

        public void exitAddEditIconClicked(View view){
            YoYo.with(Techniques.Pulse).duration(100)
                    .playOn(mBinding.addEditExitIcon);
            unRevealActivity();
        }
    }



    private void getImageFromUserPhone() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), DEFAULT_IMG_ID);

    }

    private void updateDateUI() {
        mBinding.inputDateEtext.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
    }

    private void updateTimeUI() {
        mBinding.inputTimeEtext.setText(mHour + ":" + mMinute);
    }

    private void updateOtherUI() {
        mBinding.inputNameEtext.setText(charInput);
        mBinding.chosenColorTextView.setTextColor(colorDefault);
        Bitmap bitmap = AppHelperClass.getUserBitmapImage(AddEditActivity.this);

        if (bitmap != null) {
            mBinding.addEditCustomImage.setImageBitmap(bitmap);
        } else {
            mBinding.addEditCustomImage.setBackgroundResource(chosenImageId);
        }

        if (isAlertSetInActivity) {
            mBinding.alertSwitch.toggle();
            mBinding.alertSwitch.setChipBackgroundColorResource(R.color.tab_expired);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("cycle", "onDestroy: ");
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DEFAULT_IMG_ID && data != null) {

            try {
                Uri uri = null; //Need to save this URI into shared preferences in order to load the image back up.
                if (data != null) {
                    uri = data.getData();
                }
                Bitmap bitmap = null;

                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    // To handle deprication use
                    ImageDecoder.Source source = ImageDecoder
                            .createSource(AddEditActivity.this.getContentResolver(), uri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                } else {
                    // Use older version
                    bitmap = MediaStore.Images.Media.getBitmap(AddEditActivity.this.getContentResolver(), uri);

                }

                if (!AppHelperClass.saveUserImageIntoSharedPreferences(bitmap, AddEditActivity.this)) {
                    throw new IOException();
                } else {
                    mBinding.addEditCustomImage.setImageBitmap(bitmap);
                    imageLoadedFromUserPhone = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("error_tag", "Load image failed: " + e);
                Toast.makeText(this, "unable to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ActivityTransitionAnimation {

        private void animateButtonWidth() {
            ValueAnimator anim = ValueAnimator.ofInt(mBinding.addEditCreateCountdownBtn.getMeasuredWidth(),
                    AppHelperClass.getFabWidth(AddEditActivity.this));

            anim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mBinding.addEditCreateCountdownBtn.getLayoutParams();
                    layoutParams.width = val;
                    mBinding.addEditCreateCountdownBtn.requestLayout();
                }
            });
            anim.setDuration(250);
            anim.start();

        }

        private void fadeOutTextShowProgressDialog() {
            YoYo.with(Techniques.FadeOut)
                    .duration(250)
                    .withListener(new AnimatorListener() {
                        @Override
                        public void onAnimationStart(final Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(final Animator animation) {
                            mBinding.addEditProgressBar.setAlpha(1f);

                            Drawable drawable = mBinding.addEditProgressBar.getIndeterminateDrawable();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                drawable.setColorFilter(new BlendModeColorFilter(
                                        ContextCompat.getColor(AddEditActivity.this, R.color.white),
                                        BlendMode.SRC_ATOP));
                            } else {
                                drawable.setColorFilter(ContextCompat.getColor(AddEditActivity.this, R.color.white),
                                        PorterDuff.Mode.SRC_ATOP);
                            }

                            mBinding.addEditProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(final Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(final Animator animation) {

                        }
                    })
                    .playOn(mBinding.addEditBtnTextView);

        }

        private void nextAction() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    revealButton();
                    fadeOutProgressDialog();
                    fadeOutActivityToolbar();
                }
            }, 2000);
        }

        private void fadeOutActivityToolbar() {
            YoYo.with(Techniques.FadeOutRight).duration(500).playOn(mBinding.addEditCardViewToolbar);
        }

        private void revealButton() {
            mBinding.addEditCreateCountdownBtn.setElevation(0f);
            mBinding.rootLayout.setVisibility(View.VISIBLE);

            int cx = mBinding.rootLayout.getWidth();
            int cy = mBinding.rootLayout.getHeight();

            int x = (int) (AppHelperClass.getFabWidth(mContext) / 2 + mBinding.addEditCreateCountdownBtn.getX());
            int y = (int) (AppHelperClass.getFabWidth(mContext) / 2 + mBinding.addEditCreateCountdownBtn.getY());

            float finalRadius = Math.max(cx, cy) * 1.2f;

            Animator reveal = ViewAnimationUtils
                    .createCircularReveal(mBinding.rootLayout, x, y, AppHelperClass.getFabWidth(mContext),
                            finalRadius);
            reveal.setDuration(350);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    super.onAnimationEnd(animation);
                    unRevealActivity();
                }
            });
            reveal.start();


        }

        private void fadeOutProgressDialog() {
            YoYo.with(Techniques.FadeOut)
                    .duration(200)
                    .playOn(mBinding.addEditProgressBar);

        }
    }
}