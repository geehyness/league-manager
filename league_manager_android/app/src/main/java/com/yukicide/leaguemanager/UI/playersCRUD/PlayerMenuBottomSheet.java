package com.yukicide.leaguemanager.UI.playersCRUD;

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
import com.yukicide.leaguemanager.UI.leagueCRUD.LeagueMenuBottomSheet;

public class PlayerMenuBottomSheet extends BottomSheetDialogFragment {
    private PlayerMenuBottomSheet.PlayerBottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_player_options, container, false);

        Button editName = v.findViewById(R.id.btnPlayerRename);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(1);
                dismiss();
            }
        });

        Button delete = v.findViewById(R.id.btnPlayerDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(2);
                dismiss();
            }
        });

        return v;
    }

    public interface PlayerBottomSheetListener {
        void onButtonClicked(int option);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (PlayerMenuBottomSheet.PlayerBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PlayerBottomSheetListener");
        }
    }
}
