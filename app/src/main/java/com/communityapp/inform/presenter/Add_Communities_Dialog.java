package com.communityapp.inform.presenter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inform.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Dialog displaying the communities to add.
 */
public class Add_Communities_Dialog extends DialogFragment {

    public interface MultiChoiceListener {
        void onPositiveButtonClicked (String[] list, ArrayList<String> selectedList);
        void onNegativeButtonClicked ();
        void onNeutralButtonClicked ();
    }

    private MultiChoiceListener listener;

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        final String[] communities_list = getActivity().getResources().getStringArray(R.array.communities_options);
        final ArrayList<String> selected_communities = new ArrayList<>();

        builder.setTitle("Select communities");
        builder.setMultiChoiceItems(communities_list, null, (dialogInterface, position, isChecked) -> {
            if(isChecked){
               selected_communities.add(communities_list[position]);
            }
        });

        builder.setPositiveButton("Ok", (dialogInterface, position) -> listener.onPositiveButtonClicked(communities_list, selected_communities))
                .setNegativeButton("Cancel", (dialogInterface, i) -> listener.onNegativeButtonClicked())
                .setNeutralButton("Clear all", (dialogInterface, pos) -> listener.onNeutralButtonClicked());

        return builder.create();
    }
}
