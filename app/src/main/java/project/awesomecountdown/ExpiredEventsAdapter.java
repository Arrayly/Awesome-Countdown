package project.awesomecountdown;


import android.content.Context;
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

        holder.title.setText("ID: "+ expiredEvents.getEventTitle());
        holder.id.setText("ID: "+ expiredEvents.getID());
        holder.time.setText("ID: "+ expiredEvents.getMillisLeft());


    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView title, id, time;

        public ViewHolder2(@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.expired_event_rv_title_txv);
            id = itemView.findViewById(R.id.expired_event_rv_id_txv);
            time = itemView.findViewById(R.id.expired_display_time_txtview);

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

    public interface ExpiredEventClickListener {

        void OnClick(int position);
    }

    public void setOnClickListener(ExpiredEventClickListener clickListener) {
        this.mListener = clickListener;
    }


}
