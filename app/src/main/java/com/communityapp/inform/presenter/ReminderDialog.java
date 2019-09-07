package com.communityapp.inform.presenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.inform.R;

import java.util.Objects;

/**
 * Dialog to display the reminder options.
 */
public class ReminderDialog extends DialogFragment {

    private int position = 0;// default selection

    public interface SingleChoiceListener {
        void onPositiveReminderButtonClicked (String[] list, int pos );
        void onNegativeReminderButtonClicked ();
    }

    private SingleChoiceListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SingleChoiceListener) context;
        } catch (Exception e){
            throw new ClassCastException(getActivity().toString()+"SingleChoiceListener must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] reminder_list = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.reminder_options);

        builder.setTitle("Select reminder")
                .setSingleChoiceItems(reminder_list, 0, (dialogInterface, i) -> position = i)
                .setPositiveButton("OK", (dialogInterface, i) -> mListener.onPositiveReminderButtonClicked(reminder_list, position))
                .setNegativeButton("CANCEL", (dialogInterface, i) -> mListener.onNegativeReminderButtonClicked());


        return builder.create();

    }
}
