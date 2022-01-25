package com.yukicide.leaguemanager.UI.teamCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewTeamsActivity extends AppCompatActivity implements TeamMenuBottomSheet.BottomSheetListener {
    private TeamAdapter teamAdapter;
    private ArrayList<TeamModel> teamList = new ArrayList<>();

    LeagueModel leagueModel;
    LeagueManagerAPI leagueManagerAPI;
    private TeamModel selectedTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teams);

        Intent i = getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        TextView txtLeague = findViewById(R.id.txtTitle);
        txtLeague.setText(leagueModel.getName() + " teams");

        initRecycler();
        getTeams();

        TextView txtLogTable = findViewById(R.id.txtLogTable);
        txtLogTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewTeamsActivity.this, LogTableActivity.class)
                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        FloatingActionButton btnAddLeague = findViewById(R.id.btnAdd);
        btnAddLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("New League")
                        .setMessage("Are you sure you want to create a new Team?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(ViewTeamsActivity.this, AddTeamActivity.class)
                                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void initRecycler(){
        RecyclerView leagueRecyclerView = findViewById(R.id.teamsRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        teamAdapter = new TeamAdapter(teamList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(teamAdapter);

        teamAdapter.setOnItemClickListener(new TeamAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedTeam = teamList.get(position);

                //startActivity(new Intent(getContext(), ViewTeamActivity.class)
                //        .putExtra(StringExtras.TEAM, (new Gson()).toJson(teamModel)));

                TeamMenuBottomSheet menu = new TeamMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "teamMenu");
            }

            @Override
            public void onMoreClick(int position) {

            }
        });
    }

    @Override
    public void onButtonClicked(int text) {
        if (text == 1) {
            startActivity(new Intent(this, ViewPlayersActivity.class)
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam)));

        } else if (text == 2) {
            startActivity(new Intent(this, AddTeamActivity.class)
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(selectedTeam))
                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel))
                    .putExtra(StringExtras.EDIT, true));

        } else if (text == 3) {
            new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
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
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
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
                    new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                TeamModel teamModel = response.body();
                new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
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

                new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void getTeams() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
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
                    new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
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

                new AlertDialog.Builder(ViewTeamsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("DataState", 0);
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