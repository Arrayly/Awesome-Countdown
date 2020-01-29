package project.awesomecountdown;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import project.awesomecountdown.EventAdapter.ViewHolder;

public class EventAdapter extends ListAdapter<Event, ViewHolder> {

    private EventClickedListener mListener;

    private EventExpiredListener mExpiredListener;

    private long timeLeftInMillis, futureMillis, millisAtTimeOfCreation;

    private Context mContext;

    private View mView;


    public EventAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull final Event oldItem, @NonNull final Event newItem) {
            return oldItem.getID() == newItem.getID();


        }

        @Override
        public boolean areContentsTheSame(@NonNull final Event oldItem, @NonNull final Event newItem) {
            return oldItem.getEventTitle().equals(newItem.getEventTitle()) &&
                    oldItem.getMillisLeft() == newItem.getMillisLeft() &&
                    oldItem.getEventOrderId() == newItem.getEventOrderId();

        }

    };


    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        Log.i("ADAPTER", "onCreateViewHolder: ");
        return viewHolder;
    }

    @Override
    public void onViewRecycled(@NonNull final ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.ViewHolder holder, final int position) {
        Log.i("ADAPTER", "onBindViewHolder: ");
        final Event event = getItem(position);

        holder.cardBackgroundImgView.setImageDrawable(null);

        if (event.isImageLoadedFromUserPhone() && AppHelperClass.checkIfUserBitmapImageExists(mContext)) {
            Bitmap bitmap = AppHelperClass.getUserBitmapImage(mContext);
            holder.cardBackgroundImgView.setImageBitmap(bitmap);
        } else if (event.isImageLoadedFromUrl()) {
            Picasso.get().load(event.getImgUrl()).into(holder.cardBackgroundImgView);

        } else {
            holder.cardBackgroundImgView.setBackgroundResource(event.getImageId());
        }

        if(event.isLocationSet()){
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(event.getEventLocation());
        }

        int textColor = event.getTextColorId();
        holder.title.setText(event.getEventTitle());
        holder.endDate.setText(event.getDateRawString());
        if (event.isAlertSet()) {
            holder.reminderImgView.setImageDrawable(mContext.getDrawable(R.drawable.reminder_set_icon));

        }

        holder.title.setTextColor(textColor);
        holder.endDate.setTextColor(textColor);
        holder.location.setTextColor(textColor);

        futureMillis = event.getMillisLeft();
        timeLeftInMillis = futureMillis - System.currentTimeMillis();
        millisAtTimeOfCreation = event.getMillisAtTimeOfCreation();

        //Nullify countdown to prevent new objects using the same references
        if (holder.mCountDownTimer != null) {
            holder.mCountDownTimer.cancel();
            holder.mCountDownTimer = null;

        }

        holder.mCountDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateView(timeLeftInMillis, holder, millisAtTimeOfCreation);
            }

            @Override
            public void onFinish() {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION && mExpiredListener != null) {
                    long id = getItem(holder.getAdapterPosition()).getID();
                    mExpiredListener.onExpired(id);
                }
            }
        }.start();
    }

    @Override
    public int getItemViewType(final int position) {

        long id = getItem(position).getID();

        Log.i("TAG", "getItemViewType: " + position + " ID: " + id);
        return super.getItemViewType(position);

    }


    private void updateView(long timeLeft, ViewHolder holder, long millisAtCreation) {

        final long day = TimeUnit.MILLISECONDS.toDays(timeLeft);

        final long hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeLeft));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft));

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));

        if (day >= 1) {
            holder.time.setText(String.format(Locale.getDefault(), "%d d", day));
        } else if (hours >= 1) {
            holder.time.setText(String.format(Locale.getDefault(), "%d h", hours));
        } else if (minutes >= 1) {
            holder.time.setText(String.format(Locale.getDefault(), "%d m", minutes));
        } else if (seconds >= 1) {
            holder.time.setText(String.format(Locale.getDefault(), "%d s", seconds));
        } else {
            holder.time.setText("");
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CountDownTimer mCountDownTimer;

        private TextView title, endDate, time, location;

        private ImageView reminderImgView, cardBackgroundImgView;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mView = itemView;

            title = itemView.findViewById(R.id.event_rv_title_txv);
            endDate = itemView.findViewById(R.id.event_end_date);
            time = itemView.findViewById(R.id.rv_event_display_time);
            reminderImgView = itemView.findViewById(R.id.rv_event_reminderSet_imgView);
            cardBackgroundImgView = itemView.findViewById(R.id.rv_event_background_imgVIew);
            location = itemView.findViewById(R.id.event_location);
            time.bringToFront();

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onClick(position);
                    }
                }
            });
        }
    }


    public interface EventClickedListener {

        void onClick(int position);
    }

    public interface EventExpiredListener {

        void onExpired(long id);
    }

    public void setEventClickedListener(EventClickedListener listener) {
        mListener = listener;
    }

    public void setEventExpiredListener(EventExpiredListener listener) {
        mExpiredListener = listener;
    }
}