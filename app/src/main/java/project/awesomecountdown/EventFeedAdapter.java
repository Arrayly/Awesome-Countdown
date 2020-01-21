package project.awesomecountdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
import project.awesomecountdown.ExpiredEventsAdapter.ViewHolder2;

public class EventFeedAdapter extends RecyclerView.Adapter<EventFeedAdapter.ViewHolder> {

    private EventFeedClickListener mListener;

    private Context mContext;

    private List<EventFeedDetails> mEventFeedDetails;

    public EventFeedAdapter(final Context context, final List<EventFeedDetails> eventFeedDetails, EventFeedClickListener listener) {
        mContext = context;
        mEventFeedDetails = eventFeedDetails;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mEventFeedDetails.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_event_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(mEventFeedDetails.get(position).getEventName());
        holder.date.setText(mEventFeedDetails.get(position).getEventLocalDate());

        String eventId = mEventFeedDetails.get(position).getEventId();
        String eventUrl = mEventFeedDetails.get(position).getEventUrl();
        String eventLocalTime = mEventFeedDetails.get(position).getEventLocalTime();
        String eventImage16_9 = mEventFeedDetails.get(position).getEventImage16_9();
        String eventLocationName = mEventFeedDetails.get(position).getEventLocationName();
        String eventPostalCode = mEventFeedDetails.get(position).getEventPostalCode();

        String description = "Event Id = " + eventId + " eventUrl = " + eventUrl + "event local time" + eventLocalTime;

        holder.description.setText(description);

        Picasso.get()
                .load(eventImage16_9)
                .into(holder.image);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, description;
        ImageView image;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventfeed_title_txtview);
            date = itemView.findViewById(R.id.eventfeed_rv_startdate_txv);
            description = itemView.findViewById(R.id.eventfeed_rv_description_txv);
            image = itemView.findViewById(R.id.event_rv_imageview);

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

    public void refreshEventList(List<EventFeedDetails> eventDetails){
        mEventFeedDetails.clear();
        mEventFeedDetails.addAll(eventDetails);
    }

    public interface EventFeedClickListener {

        void onClick(int position);
    }
}
