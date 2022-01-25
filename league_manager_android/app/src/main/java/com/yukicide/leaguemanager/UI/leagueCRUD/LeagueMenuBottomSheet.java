package com.yukicide.leaguemanager.UI.leagueCRUD;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.teamCRUD.TeamMenuBottomSheet;

public class LeagueMenuBottomSheet extends BottomSheetDialogFragment {
    private LeagueMenuBottomSheet.LeagueBottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_league_options, container, false);

        Button viewPlayers = v.findViewById(R.id.btnLeagueRename);
        viewPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(1);
                dismiss();
            }
        });

        Button editName = v.findViewById(R.id.btnLeagueDelete);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(2);
                dismiss();
            }
        });

        return v;
    }

    public interface LeagueBottomSheetListener {
        void onButtonClicked(int option);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (LeagueMenuBottomSheet.LeagueBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LeagueBottomSheetListener");
        }
    }
}
