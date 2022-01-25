package com.yukicide.leaguemanager.UI.fixtureCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.FixtureAdapter;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.GoalAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.GoalModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.ResultModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFixtureResultsActivity extends AppCompatActivity {
    private FixtureModel fixtureModel;
    private TeamFixtures teamFixtures;

    ArrayList<PlayerModel> team1Players = new ArrayList<>();
    ArrayList<PlayerModel> team2Players = new ArrayList<>();

    ArrayList<GoalModel> t1Results = new ArrayList<>();
    ArrayList<GoalModel> t2Results = new ArrayList<>();

    private LeagueManagerAPI leagueManagerAPI;
    private Spinner spPlayers1, spPlayers2;
    private GoalAdapter goals1Adapter, goals2Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fixture_results);

        Intent i = getIntent();
        fixtureModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.FIXTURE), FixtureModel.class);
        teamFixtures = (new Gson()).fromJson(i.getStringExtra(StringExtras.TEAM_FIXTURES), TeamFixtures.class);

        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtTeam1Name = findViewById(R.id.txtTeam1Name);
        TextView txtTeam2Name = findViewById(R.id.txtTeam2Name);

        TeamModel t1 = null, t2 = null;

        for (TeamModel t : teamFixtures.getTeamList()) {
            if (t.get_id().equals(fixtureModel.getTeam1Id())) {
                t1 = t;
            } else if (t.get_id().equals(fixtureModel.getTeam2Id())) {
                t2 = t;
            }
        }

        if (t1 == null || t2 == null) {
            new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Error")
                    .setMessage("Unable to get team details!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

        //txtTitle.setText((new Gson()).toJson(t1) + " vs " + (new Gson()).toJson(t2));
        txtTitle.setText(String.format("%s vs %s", Objects.requireNonNull(t1).getName(), Objects.requireNonNull(t2).getName()));
        txtTeam1Name.setText(t1.getName());
        txtTeam2Name.setText(t2.getName());

        initRetrofit();
        getTeam1Players();
        getTeam2Players();

        Button add1 = findViewById(R.id.btnAdd1);
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoals1();
            }
        });

        Button add2 = findViewById(R.id.btnAdd2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoals2();
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GoalModel> allGoals = new ArrayList<>();

                allGoals.addAll(t1Results);
                allGoals.addAll(t2Results);

                if (allGoals.isEmpty()) {
                    new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("No goals have been logged. Please use the '0:0 Draw' button if that was the outcome.")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                ResultModel resultModel = new ResultModel(fixtureModel.get_id(), allGoals);

                saveResults(resultModel);
            }
        });
    }

    private void saveResults(ResultModel resultModel) {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<ResultModel> call = leagueManagerAPI.addResults(resultModel);
        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable post results")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                ResultModel t = response.body();

                new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage("Match outcome recorded!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refresh();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void addGoals1() {
        if (spPlayers1.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a player!", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView txtGoals = findViewById(R.id.txtGoals1);
        if (TextUtils.isEmpty(txtGoals.getText().toString())) {
            Toast.makeText(this, "Goals amount cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        PlayerModel p = team1Players.get(spPlayers1.getSelectedItemPosition() - 1);
        GoalModel goalModel = new GoalModel(p.get_id(), Integer.parseInt(txtGoals.getText().toString()));
        t1Results.add(goalModel);
        goals1Adapter.notifyDataSetChanged();

        spPlayers1.setSelection(0);
        txtGoals.setText("");
    }

    private void addGoals2() {
        if (spPlayers2.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a player!", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView txtGoals = findViewById(R.id.txtGoals2);
        if (TextUtils.isEmpty(txtGoals.getText().toString())) {
            Toast.makeText(this, "Goals amount cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        PlayerModel p = team2Players.get(spPlayers2.getSelectedItemPosition() - 1);
        GoalModel goalModel = new GoalModel(p.get_id(), Integer.parseInt(txtGoals.getText().toString()));
        t2Results.add(goalModel);
        goals2Adapter.notifyDataSetChanged();

        spPlayers2.setSelection(0);
        txtGoals.setText("");
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
    }

    private void getTeam1Players() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<PlayerModel>> call = leagueManagerAPI.getTeamPlayers(fixtureModel.getTeam1Id());
        call.enqueue(new Callback<List<PlayerModel>>() {
            @Override
            public void onResponse(Call<List<PlayerModel>> call, Response<List<PlayerModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Team 1 players")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (PlayerModel t : response.body()) {
                    team1Players.add(t);
                }
                initT1Spinner();
                initT1Recycler();
            }

            @Override
            public void onFailure(Call<List<PlayerModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void getTeam2Players() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<PlayerModel>> call = leagueManagerAPI.getTeamPlayers(fixtureModel.getTeam2Id());
        call.enqueue(new Callback<List<PlayerModel>>() {
            @Override
            public void onResponse(Call<List<PlayerModel>> call, Response<List<PlayerModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Team 2 players")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (PlayerModel t : response.body()) {
                    team2Players.add(t);
                }
                initT2Spinner();
                initT2Recycler();
            }

            @Override
            public void onFailure(Call<List<PlayerModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddFixtureResultsActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void initT1Spinner(){
        spPlayers1 = findViewById(R.id.spPlayers1);

        ArrayList<String> strList = new ArrayList<>();
        strList.add("Select a player");
        for (PlayerModel p : team1Players) {
            strList.add(p.getName());
        }

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(AddFixtureResultsActivity.this, android.R.layout.simple_spinner_item, strList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlayers1.setAdapter(catAdapter);
    }

    private void initT2Spinner(){
        spPlayers2 = findViewById(R.id.spPlayers2);

        ArrayList<String> strList = new ArrayList<>();
        strList.add("Select a player");
        for (PlayerModel p : team2Players) {
            strList.add(p.getName());
        }

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(AddFixtureResultsActivity.this, android.R.layout.simple_spinner_item, strList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlayers2.setAdapter(catAdapter);
    }

    private void initT1Recycler() {
        RecyclerView leagueRecyclerView = findViewById(R.id.goals1Recycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        goals1Adapter = new GoalAdapter(t1Results, team1Players);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(goals1Adapter);

        goals1Adapter.setOnItemClickListener(new GoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {

            }

            @Override
            public void onMoreClick(int position) {
                t1Results.remove(position);
                goals1Adapter.notifyItemRemoved(position);
            }
        });
    }

    private void initT2Recycler() {
        RecyclerView leagueRecyclerView = findViewById(R.id.goals2Recycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        goals2Adapter = new GoalAdapter(t2Results, team2Players);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(goals2Adapter);

        goals2Adapter.setOnItemClickListener(new GoalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {

            }

            @Override
            public void onMoreClick(int position) {
                t2Results.remove(position);
                goals2Adapter.notifyItemRemoved(position);
            }
        });
    }

    private void refresh() {
        SharedPreferences sp = getSharedPreferences("DataState", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshResults", true);
        editor.commit();

        finish();
    }
}