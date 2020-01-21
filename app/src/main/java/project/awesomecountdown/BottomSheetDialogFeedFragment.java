package project.awesomecountdown;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialogFeedFragment extends BottomSheetDialogFragment {

    private FeedFragmentBottomSheetListener mListener;

    public static BottomSheetDialogFeedFragment newInstance(){
        BottomSheetDialogFeedFragment frag = new BottomSheetDialogFeedFragment();
        frag.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_sheet_feedfragment, container,false);

        TextView add,share,browse;

        add = view.findViewById(R.id.modalSheet_feedFragment_addTextView);
        share = view.findViewById(R.id.modalSheet_feedFragment_shareTextView);
        browse = view.findViewById(R.id.modalSheet_feedFragment_browse);

        if (mListener!=null){
            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mListener.onBottomSheetItemSelected(1);
                    dismiss();
                }
            });
        }

        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onBottomSheetItemSelected(2);
                dismiss();

            }
        });

        browse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onBottomSheetItemSelected(3);
                dismiss();

            }
        });

        return view;
    }

    public interface FeedFragmentBottomSheetListener{
        void onBottomSheetItemSelected(int position);
    }

    public void setFeedFragmentBottomSheetListener(FeedFragmentBottomSheetListener listener){
        mListener = listener;
    }
}
