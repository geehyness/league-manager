package com.yukicide.leaguemanager.UI.leagueCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.LogAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogTableActivity extends AppCompatActivity {
    ArrayList<TeamModel> teams = new ArrayList<>();
    private LeagueModel leagueModel;
    private LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_table);

        Intent i = getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        initRecycler();
        getTeams();
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
        Call<List<TeamModel>> call = leagueManagerAPI.getLogTable(leagueModel.get_id());
        call.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(LogTableActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Leagues")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (TeamModel t : response.body()) {
                    teams.add(t);
                }
                logAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(LogTableActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void initRecycler() {
        RecyclerView leagueRecyclerView = findViewById(R.id.teamsRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        logAdapter = new LogAdapter(teams);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(logAdapter);

        logAdapter.setOnItemClickListener(new LogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {

            }
        });
    }
}