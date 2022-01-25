package com.yukicide.leaguemanager.UI.fixtureCRUD;

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

public class FixtureBottomMenuSheet extends BottomSheetDialogFragment {
    private FixtureBottomMenuSheet.FixtureBottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fixture_options, container, false);

        Button view = v.findViewById(R.id.btnFixtureOutcome);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(1);
                dismiss();
            }
        });

        Button add = v.findViewById(R.id.btnAddOutcome);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(2);
                dismiss();
            }
        });

        Button del = v.findViewById(R.id.btnFixtureDelete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(3);
                dismiss();
            }
        });

        return v;
    }

    public interface FixtureBottomSheetListener {
        void onButtonClicked(int option);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (FixtureBottomMenuSheet.FixtureBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FixtureBottomSheetListener");
        }
    }
}
