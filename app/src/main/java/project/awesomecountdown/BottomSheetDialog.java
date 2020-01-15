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

public class BottomSheetDialog extends BottomSheetDialogFragment {

    //Set the interface via our underlying fragment/activity
    private BottomSheetListener mListener;

    public static BottomSheetDialog newInstance() {
        BottomSheetDialog frag = new BottomSheetDialog();
        frag.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_sheet, container,false);

        TextView textView_edit = view.findViewById(R.id.edit_item_txtview);
        TextView textView_delete = view.findViewById(R.id.remove_item_txtview);

        textView_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.OnBottomSheetSelected(0);
                dismiss();
            }
        });


        textView_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.OnBottomSheetSelected(1);
                dismiss();
            }
        });

        return view;
    }

    //implement the interface to listen for view clicks

    public interface BottomSheetListener{
        void OnBottomSheetSelected(int position);
    }

    public void setListener(BottomSheetListener bottomSheetListener){
        mListener = bottomSheetListener;
    }
}
