package com.communityapp.inform.Presenter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inform.R;

import java.util.ArrayList;

/**
 * Dialog displaying the communities to add.
 */
public class Add_Communities_Dialog extends DialogFragment {

    public interface MultiChoiceListener {
        void onPositiveButtonClicked (String[] list, ArrayList<String> selectedList);
        void onNegativeButtonClicked ();
        void onNeutralButtonClicked ();
    }

    MultiChoiceListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (MultiChoiceListener) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString()+" MultiChoiceListener must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] communities_list = getActivity().getResources().getStringArray(R.array.communities_options);
        final ArrayList<String> selected_communities = new ArrayList<>();

        builder.setTitle("Select communities");
        builder.setMultiChoiceItems(communities_list, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                   selected_communities.add(communities_list[position]);
                }
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        listener.onPositiveButtonClicked(communities_list, selected_communities);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onNegativeButtonClicked();
                    }
                })
                .setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        listener.onNeutralButtonClicked();
                    }
        });

        return builder.create();
    }
}
