package project.awesomecountdown;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import project.awesomecountdown.EventAdapter.ViewHolder;

public class EventAdapter extends ListAdapter<Event, ViewHolder> {

    private EventClickedListener mListener;

    private EventExpiredListener mExpiredListener;

    private long timeLeftInMillis, futureMillis,millisAtTimeOfCreation;

    private Context mContext;


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

        holder.title.setText(event.getEventTitle());
        holder.id.setText("ID = " + event.getID());

        futureMillis = event.getMillisLeft();
        timeLeftInMillis = futureMillis - System.currentTimeMillis();
        millisAtTimeOfCreation = event.getMillisAtTimeOfCreation();



        if (holder.mCountDownTimer != null) {
            holder.mCountDownTimer.cancel();
        }

        holder.mCountDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(final long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateView(timeLeftInMillis, holder,millisAtTimeOfCreation);
            }

            @Override
            public void onFinish() {
                long id = getItem(holder.getAdapterPosition()).getID();

                Log.i("TAG", "onFinish: " + id);

                if (mExpiredListener != null && position != RecyclerView.NO_POSITION) {
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

        long seconds_countdown = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
        long seconds_at_time_of_creation = TimeUnit.MILLISECONDS.toSeconds(millisAtCreation);

        final long day = TimeUnit.MILLISECONDS.toDays(timeLeft);

        final long hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeLeft));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft));

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));

        if (day >= 1) {
            holder.progress_countdown.setStepCountText(String.format(Locale.getDefault(), "%d days", day));
        } else if (hours >= 1) {
            holder.progress_countdown.setStepCountText(String.format(Locale.getDefault(), "%d hrs", hours));
        } else if (minutes >= 1) {
            holder.progress_countdown.setStepCountText(String.format(Locale.getDefault(), "%d min", minutes));
        } else if (seconds >= 1) {
            holder.progress_countdown.setStepCountText(String.format(Locale.getDefault(), "%d sec", seconds));
        } else {
            holder.progress_countdown.setStepCountText("DONE!");
        }

        double diff1 = seconds_at_time_of_creation - seconds_countdown;
        double diff2 = diff1 / seconds_at_time_of_creation;
        int percentage = (int) (diff2 * 100);


        holder.progress_countdown.setPercentage((int) (percentage * 3.6));


    }




    class ViewHolder extends RecyclerView.ViewHolder {

        CountDownTimer mCountDownTimer;

        private TextView title, id;

        com.app.progresviews.ProgressWheel progress_countdown;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);


            title = itemView.findViewById(R.id.event_rv_title_txv);
            id = itemView.findViewById(R.id.event_rv_id_txv);
            progress_countdown = itemView.findViewById(R.id.progress_countdown);

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
