package com.yukicide.leaguemanager.UI.Home.Fragments;

import android.annotation.SuppressLint;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.FixtureAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.ResultModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.fixtureCRUD.AddFixtureActivity;
import com.yukicide.leaguemanager.UI.fixtureCRUD.AddFixtureResultsActivity;
import com.yukicide.leaguemanager.UI.fixtureCRUD.FixtureBottomMenuSheet;
import com.yukicide.leaguemanager.UI.fixtureCRUD.ViewFixturesActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.AddTeamActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FixturesFragment extends Fragment {
    LeagueModel leagueModel;
    ArrayList<FixtureModel> fixturesList = new ArrayList<>();
    ArrayList<FixtureModel> displayFixturesList = new ArrayList<>();
    ArrayList<ResultModel> results = new ArrayList<>();
    TeamFixtures teamFixtures;
    FixtureAdapter fixtureAdapter;
    CardView btnUpcoming, btnAll;
    boolean all = false;

    LeagueManagerAPI leagueManagerAPI;
    private FixtureModel selectedFixture;
    private ArrayList<VenueModel> venuesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fixtures, container, false);

        Intent i = getActivity().getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        teamFixtures = new TeamFixtures();
        teamFixtures.setFixturesList(fixturesList);

        displayFixturesList.addAll(teamFixtures.getFixturesList());

        teamFixtures = new TeamFixtures();
        initRetrofit();
        getTeams();
        getResults();
        getVenues(v);

        final TextView txtTitle = v.findViewById(R.id.txtTitle);
        //txtTitle.setText(i.getStringExtra(StringExtras.LEAGUE));

        btnUpcoming = v.findViewById(R.id.btnUpcoming);
        btnAll = v.findViewById(R.id.btnAll);

        btnUpcoming.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                txtTitle.setText("Upcoming Fixtures");
                displayFixturesList.clear();
                displayFixturesList.addAll(teamFixtures.getFixturesList());
                fixtureAdapter.notifyDataSetChanged();
                all = false;
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                txtTitle.setText("All Fixtures");
                displayFixturesList.clear();
                displayFixturesList.addAll(fixturesList);
                fixtureAdapter.notifyDataSetChanged();
                all = true;
            }
        });

        Button btnAddFixture = v.findViewById(R.id.btnManage);
        btnAddFixture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddFixtureActivity.class)
                        .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures))
                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel)));
            }
        });

        return v;
    }

    private void getResults() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<ResultModel>> call = leagueManagerAPI.getAllResults();
        call.enqueue(new Callback<List<ResultModel>>() {
            @Override
            public void onResponse(Call<List<ResultModel>> call, Response<List<ResultModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Fixtures")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (ResultModel t : response.body()) {
                    results.add(t);
                }
            }

            @Override
            public void onFailure(Call<List<ResultModel>> call, Throwable t) {
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

    private void initFixtureRecycler(View v) {
        RecyclerView leagueRecyclerView = v.findViewById(R.id.fixtureRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(getContext());
        fixtureAdapter = new FixtureAdapter(displayFixturesList, teamFixtures, venuesList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(fixtureAdapter);

        fixtureAdapter.setOnItemClickListener(new FixtureAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedFixture = displayFixturesList.get(position);

                /* FixtureBottomMenuSheet menu = new FixtureBottomMenuSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "fixtureMenu"); */

                PopupMenu popupMenu = new PopupMenu(leagueRecyclerView.findViewHolderForAdapterPosition(position).itemView.getContext(),
                        leagueRecyclerView.findViewHolderForAdapterPosition(position).itemView);
                popupMenu.inflate(R.menu.fixture_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.fixture_results:
                                menuChoice(1);
                                return true;

                            case R.id.fixture_add_results:
                                menuChoice(2);
                                return true;

                            case R.id.fixture_delete:
                                menuChoice(3);
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

    private void getVenues(View view) {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<VenueModel>> call = leagueManagerAPI.getAllVenues();
        call.enqueue(new Callback<List<VenueModel>>() {
            @Override
            public void onResponse(Call<List<VenueModel>> call, Response<List<VenueModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {

                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get venues!")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (VenueModel v : response.body()) {
                    venuesList.add(v);
                }

                initFixtureRecycler(view);
            }

            @Override
            public void onFailure(Call<List<VenueModel>> call, Throwable t) {
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

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
    }

    private void getFixtures() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<FixtureModel>> call = leagueManagerAPI.getAllFixtures(leagueModel.get_id());
        call.enqueue(new Callback<List<FixtureModel>>() {
            @Override
            public void onResponse(Call<List<FixtureModel>> call, Response<List<FixtureModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Fixtures")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (FixtureModel t : response.body()) {
                    fixturesList.add(t);
                }

                if (all) {
                    displayFixturesList.clear();
                    displayFixturesList.addAll(fixturesList);
                    fixtureAdapter.notifyDataSetChanged();
                } else {
                    displayFixturesList.clear();
                    displayFixturesList.addAll(teamFixtures.getFixturesList());
                    fixtureAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<FixtureModel>> call, Throwable t) {
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
                    teamFixtures.getTeamList().add(t);
                }

                getUpcoming();
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

    private void getUpcoming() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<FixtureModel>> call = leagueManagerAPI.getUpcomingFixtures(leagueModel.get_id());
        call.enqueue(new Callback<List<FixtureModel>>() {
            @Override
            public void onResponse(Call<List<FixtureModel>> call, Response<List<FixtureModel>> response) {
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

                for (FixtureModel f : response.body()) {
                    teamFixtures.getFixturesList().add(f);
                }

                getFixtures();
            }

            @Override
            public void onFailure(Call<List<FixtureModel>> call, Throwable t) {
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

    public void menuChoice(int option) {
        if (option == 1) {
            TeamModel t1 = null, t2 = null;

            for (TeamModel t : teamFixtures.getTeamList()) {
                if (selectedFixture.getTeam1Id().equals(t.get_id())) {
                    t1 = t;
                }
                if (selectedFixture.getTeam2Id().equals(t.get_id())) {
                    t2 = t;
                }
            }

            if (teamFixtures.getFixturesList().contains(selectedFixture)) {
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle(t1.getName() + " vs " + t2.getName())
                        .setMessage("Match results unavailable!")
                        .setPositiveButton("Ok", null)
                        .show();
            } else {
                String report = "";
                for (ResultModel r : results) {
                    if (r.getFixtureId().equals(selectedFixture.get_id())) {

                        if (r.getOutcome() == 1)
                            report += "Winner - " + t1.getName();
                        else if (r.getOutcome() == 2)
                            report += "Winner - " + t1.getName();
                        else
                            report += "Draw";

                        break;
                    }
                }
                if (report != "") {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle(t1.getName() + " vs " + t2.getName())
                            .setMessage(report)
                            .setPositiveButton("Ok", null)
                            .show();
                } else {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle(t1.getName() + " vs " + t2.getName())
                            .setMessage("No results have been entered for this match!")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }
        } else if (option == 2) {
            startActivity(new Intent(getContext(), AddFixtureResultsActivity.class)
                    .putExtra(StringExtras.FIXTURE, (new Gson()).toJson(selectedFixture))
                    .putExtra(StringExtras.TEAM_FIXTURES, (new Gson()).toJson(teamFixtures)));
        } else if (option == 3) {
            new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete this fixture?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFixture();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void deleteFixture() {
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<FixtureModel> call = leagueManagerAPI.deleteFixture(selectedFixture.get_id());
        call.enqueue(new Callback<FixtureModel>() {
            @Override
            public void onResponse(Call<FixtureModel> call, Response<FixtureModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Fixture")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                FixtureModel fixtureModel = response.body();
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage("Fixture deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (teamFixtures.getFixturesList().contains(selectedFixture)) {
                                    fixturesList.clear();
                                    teamFixtures.getFixturesList().clear();
                                    getUpcoming();
                                } else {
                                    fixturesList.clear();
                                    getFixtures();
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<FixtureModel> call, Throwable t) {
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
        boolean refresh = sp.getBoolean("refreshFixture", false);


        if (refresh) {
            fixturesList.clear();
            teamFixtures.getFixturesList().clear();

            getUpcoming();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshFixture", false);

        boolean refresh2 = sp.getBoolean("refreshResults", false);

        if (refresh2) {
            results.clear();

            getResults();
        }

        editor.putBoolean("refreshResults", false);
        editor.commit();
    }
}