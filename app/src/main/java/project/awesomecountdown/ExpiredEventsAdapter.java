package project.awesomecountdown;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.concurrent.TimeUnit;
import project.awesomecountdown.EventAdapter.ViewHolder;
import project.awesomecountdown.ExpiredEventsAdapter.ViewHolder2;

public class ExpiredEventsAdapter extends ListAdapter<ExpiredEvents, ViewHolder2> {

    private Context mContext;

    private ExpiredEventClickListener mListener;

    public ExpiredEventsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    private static final DiffUtil.ItemCallback<ExpiredEvents> DIFF_CALLBACK = new ItemCallback<ExpiredEvents>() {
        @Override
        public boolean areItemsTheSame(@NonNull final ExpiredEvents oldItem, @NonNull final ExpiredEvents newItem) {
            return oldItem.getID() == newItem.getID();
        }

        @Override
        public boolean areContentsTheSame(@NonNull final ExpiredEvents oldItem,
                @NonNull final ExpiredEvents newItem) {
            return oldItem.getEventTitle().equals(newItem.getEventTitle()) &&
                    oldItem.getMillisLeft() == newItem.getMillisLeft();
        }
    };

    @NonNull
    @Override
    public ExpiredEventsAdapter.ViewHolder2 onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_expiredevent, parent, false);
        ViewHolder2 viewHolder2 = new ViewHolder2(view);
        return viewHolder2;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder2 holder, final int position) {
        ExpiredEvents expiredEvents = getItem(position);
        holder.cardRootBackground.setImageDrawable(null);

        if (expiredEvents.isImageLoadedFromUserPhone() && AppHelperClass.checkIfUserBitmapImageExists(mContext)) {
            Bitmap bitmap = AppHelperClass.getUserBitmapImage(mContext);
            holder.cardRootBackground.setImageBitmap(bitmap);
        } else if (expiredEvents.isImageLoadedFromUrl()) {
            Picasso.get().load(expiredEvents.getImageUrl()).into(holder.cardRootBackground);

        } else {
            holder.cardRootBackground.setBackgroundResource(expiredEvents.getImageId());
        }

        holder.timeSinceExpiry.setText(timeSinceExpiry(expiredEvents.getMillisRecordedAtExpiry()));
        holder.title.setText(expiredEvents.getEventTitle());
        holder.title.setTextColor(expiredEvents.getTextColorId());


    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView title, timeSinceExpiry;

        private ImageView cardRootBackground, cardFrontFade;

        public ViewHolder2(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.expired_event_rv_title_txv);
            timeSinceExpiry = itemView.findViewById(R.id.expired_event_rv_timeSinceExpiry_txv);
            cardRootBackground = itemView.findViewById(R.id.expired_event_imageView);

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.OnClick(position);
                    }
                }
            });
        }
    }

    //Calculate the time sinse the event has expired and display into the card
    private String timeSinceExpiry(long millisRecordedAtExpiry) {
        long currentMillis = System.currentTimeMillis();
        long timeSinceExpiry = currentMillis - millisRecordedAtExpiry;

        final long day = TimeUnit.MILLISECONDS.toDays(timeSinceExpiry);

        final long hours = TimeUnit.MILLISECONDS.toHours(timeSinceExpiry)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeSinceExpiry));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeSinceExpiry)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeSinceExpiry));

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeSinceExpiry)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSinceExpiry));

        if (day >= 1) {
            if (day < 2) {
                return day + " day ago";
            }
            return day + " days ago";
        } else if (hours >= 1 && hours < 24) {
            if (hours < 2) {
                return hours + " hr ago";
            } else {
                return hours + " hrs ago";
            }
        } else if (minutes >= 1 && minutes < 60) {
            return minutes + " min ago";
        } else {
            return "Just now";
        }

    }

    public interface ExpiredEventClickListener {

        void OnClick(int position);
    }

    public void setOnClickListener(ExpiredEventClickListener clickListener) {
        this.mListener = clickListener;
    }


}
