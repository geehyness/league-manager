package com.yukicide.leaguemanager.UI.leagueCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.fixtureCRUD.ViewFixturesActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.ViewTeamsActivity;
import com.yukicide.leaguemanager.UI.venuesCRUD.ViewVenuesActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeagueDashboardActivity extends AppCompatActivity implements LeagueMenuBottomSheet.LeagueBottomSheetListener {
    LeagueModel leagueModel;
    private LeagueManagerAPI leagueManagerAPI;
    private ArrayList<TeamModel> teamList = new ArrayList<>();
    private ArrayList<FixtureModel> fixturesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_dashboard);

        Intent i = getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        TextView txtLeague = findViewById(R.id.txtTitle);
        txtLeague.setText(leagueModel.getName());

        CardView teams = findViewById(R.id.teams);
        CardView fixtures = findViewById(R.id.fixtures);
        CardView venues = findViewById(R.id.venues);
        CardView log = findViewById(R.id.log);

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamFixtures teamFixtures = new TeamFixtures();
                teamFixtures.setFixturesList(fixturesList);
                teamFixtures.setTeamList(teamList);

                startActivity(new Intent(LeagueDashboardActivity.this, ViewTeamsActivity.class)
                        .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures))
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        fixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamFixtures teamFixtures = new TeamFixtures();
                teamFixtures.setFixturesList(fixturesList);
                teamFixtures.setTeamList(teamList);

                startActivity(new Intent(LeagueDashboardActivity.this, ViewFixturesActivity.class)
                        .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures))
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        venues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeagueDashboardActivity.this, ViewVenuesActivity.class)
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeagueDashboardActivity.this, LogTableActivity.class)
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        ImageView btnManage = findViewById(R.id.btnManage);
        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeagueMenuBottomSheet menu = new LeagueMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "leagueMenu");
            }
        });
    }

    @Override
    public void onButtonClicked(int option) {
        if (option == 1) {
            startActivity(new Intent(this, AddLeagueActivity.class)
                    .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel))
                    .putExtra(StringExtras.EDIT, true));

        } else if (option == 2) {
            new AlertDialog.Builder(LeagueDashboardActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete " + leagueModel.getName())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteLeague();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void deleteLeague() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<LeagueModel> call = leagueManagerAPI.deleteLeague(leagueModel.get_id());
        call.enqueue(new Callback<LeagueModel>() {
            @Override
            public void onResponse(Call<LeagueModel> call, Response<LeagueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(LeagueDashboardActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to add League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                LeagueModel leagueModel = response.body();
                new AlertDialog.Builder(LeagueDashboardActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(leagueModel.getName() + " deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sp = getSharedPreferences("DataState", 0);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("refreshLeague", true);
                                    editor.commit();

                                    finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<LeagueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(LeagueDashboardActivity.this, R.style.MyAlertDialogStyle)
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
        boolean refresh = sp.getBoolean("refreshLeague", false);

        if (refresh) {
            getLeague();
        }
    }

    private void getLeague() {
        // TODO: 2020/09/04 Refresh League data
    }
}