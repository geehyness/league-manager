package com.yukicide.leaguemanager.UI.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.LeagueAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.leagueCRUD.AddLeagueActivity;
import com.yukicide.leaguemanager.UI.leagueCRUD.LeagueDashboardActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeaguesFragment extends Fragment {
    private LeagueAdapter leagueAdapter;
    private ArrayList<LeagueModel> leagueList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leagues, container, false);

        initRecycler(v);
        getLeagues();

        FloatingActionButton btnAddLeague = v.findViewById(R.id.btnAddLeague);
        btnAddLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("New League")
                        .setMessage("Are you sure you want to create a new League?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getActivity(), AddLeagueActivity.class));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return v;
    }

    private void getLeagues() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<List<LeagueModel>> call = leagueManagerAPI.getLeague();
        call.enqueue(new Callback<List<LeagueModel>>() {
            @Override
            public void onResponse(Call<List<LeagueModel>> call, Response<List<LeagueModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Leagues")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (LeagueModel l : response.body()) {
                    leagueList.add(l);
                }
                leagueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LeagueModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void initRecycler(View v){
        RecyclerView leagueRecyclerView = v.findViewById(R.id.leagueRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(getContext());
        leagueAdapter = new LeagueAdapter(leagueList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(leagueAdapter);

        leagueAdapter.setOnItemClickListener(new LeagueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                LeagueModel leagueModel = leagueList.get(position);

                startActivity(new Intent(getActivity(), LeagueDashboardActivity.class)
                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = getActivity().getSharedPreferences("DataState", 0);
        boolean refresh = sp.getBoolean("refreshLeague", false);


        if (refresh) {
            leagueList.clear();

            getLeagues();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshLeague", false);
        editor.commit();
    }
}
