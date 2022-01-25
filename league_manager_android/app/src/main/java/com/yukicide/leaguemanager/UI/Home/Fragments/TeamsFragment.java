package com.yukicide.leaguemanager.UI.Home.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.TeamAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.leagueCRUD.LogTableActivity;
import com.yukicide.leaguemanager.UI.playersCRUD.ViewPlayersActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.AddTeamActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.TeamMenuBottomSheet;
import com.yukicide.leaguemanager.UI.teamCRUD.ViewTeamsActivity;
import com.yukicide.leaguemanager.UI.venuesCRUD.AddVenueActivity;
import com.yukicide.leaguemanager.UI.venuesCRUD.ViewVenuesActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeamsFragment extends Fragment implements TeamMenuBottomSheet.BottomSheetListener {

    private TeamAdapter teamAdapter;
    private ArrayList<TeamModel> teamList = new ArrayList<>();

    LeagueModel leagueModel;
    LeagueManagerAPI leagueManagerAPI;
    private TeamModel selectedTeam;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teams, container, false);

        Intent i = getActivity().getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        TextView txtLeague = v.findViewById(R.id.txtTitle);
        //txtLeague.setText(leagueModel.getName() + " teams");
        txtLeague.setText("Teams");
        initRecycler(v);
        getTeams();

        TextView txtLogTable = v.findViewById(R.id.txtLogTable);
        txtLogTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LogTableActivity.class)
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        Button btnAddLeague = v.findViewById(R.id.btnAdd);
        btnAddLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("New League")
                        .setMessage("Are you sure you want to create a new Team?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getContext(), AddTeamActivity.class)
                                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return v;
    }

    private void initRecycler(View v){
        RecyclerView leagueRecyclerView = v.findViewById(R.id.teamsRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(getContext());
        teamAdapter = new TeamAdapter(teamList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(teamAdapter);

        teamAdapter.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedTeam = teamList.get(position);

                //startActivity(new Intent(getContext(), ViewTeamActivity.class)
                //        .putExtra(StringExtras.TEAM, (new Gson()).toJson(teamModel)));

                /* TeamMenuBottomSheet menu = new TeamMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getActivity().getSupportFragmentManager(), "teamMenu"); */

                startActivity(new Intent(getContext(), ViewPlayersActivity.class)
                        .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam)));
            }

            @Override
            public void onMoreClick(int position) {
                selectedTeam = teamList.get(position);

                PopupMenu popupMenu = new PopupMenu(leagueRecyclerView.findViewHolderForAdapterPosition(position).itemView.getContext(),
                        leagueRecyclerView.findViewHolderForAdapterPosition(position).itemView);
                popupMenu.inflate(R.menu.team_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.team_rename:
                                startActivity(new Intent(getContext(), AddTeamActivity.class)
                                        .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam))
                                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel))
                                        .putExtra(StringExtras.EDIT, true));
                                return true;

                            case R.id.team_delete:
                                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                                        .setIcon(R.drawable.ic_baseline_warning)
                                        .setTitle("Warning")
                                        .setMessage("Are you sure you want to delete " + selectedTeam.getName())
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteTeam();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public void onButtonClicked(int text) {
        if (text == 1) {
            startActivity(new Intent(getContext(), ViewPlayersActivity.class)
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam)));

        } else if (text == 2) {
            startActivity(new Intent(getContext(), AddTeamActivity.class)
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam))
                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel))
                    .putExtra(StringExtras.EDIT, true));

        } else if (text == 3) {
            new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete " + selectedTeam.getName())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTeam();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void deleteTeam() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<TeamModel> call = leagueManagerAPI.deleteTeam(selectedTeam.get_id());
        call.enqueue(new Callback<TeamModel>() {
            @Override
            public void onResponse(Call<TeamModel> call, Response<TeamModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                TeamModel teamModel = response.body();
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(teamModel.getName() + " deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                teamList.clear();
                                getTeams();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<TeamModel> call, Throwable t) {
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

    private void getTeams() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<List<TeamModel>> call = leagueManagerAPI.getTeamsByLeague(leagueModel.get_id());
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
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

                for (TeamModel t : response.body()) {
                    teamList.add(t);
                }
                teamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {
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

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = getActivity().getSharedPreferences("DataState", 0);
        boolean refresh = sp.getBoolean("refreshTeam", false);

        if (refresh) {
            teamList.clear();
            getTeams();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshTeam", false);
        editor.commit();
    }
}