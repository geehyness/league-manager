package com.yukicide.leaguemanager.UI.teamCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTeamActivity extends AppCompatActivity {
    TextInputLayout name, logo;
    TeamModel team, editTeam;
    LeagueModel leagueModel;
    boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        Intent i = getIntent();
        if (i.getStringExtra(StringExtras.EDIT) != null)
            edit = (new Gson()).fromJson(i.getStringExtra(StringExtras.EDIT), boolean.class);
        else
            edit = false;

        editTeam = (new Gson()).fromJson(i.getStringExtra(StringExtras.TEAM), TeamModel.class);
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        name = findViewById(R.id.txtTeamName);
        //logo = findViewById(R.id.txtLogoLink);
        TextView title = findViewById(R.id.txtTitle);
        title.setText(leagueModel.getName() + " team");

        if (editTeam!=null) {
            name.getEditText().setText(editTeam.getName());
        }

        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getEditText().getText().toString())) {
                    name.getEditText().setError("Name cannot be empty!");
                    return;
                }

                /*if (TextUtils.isEmpty(logo.getEditText().getText().toString())) {
                    name.getEditText().setError("Put a link to the league logo!");
                    return;
                }*/

                team = new TeamModel();
                team.setName(name.getEditText().getText().toString());
                team.setLeagueId(leagueModel.get_id());

                if (editTeam != null) {
                    updateTeam();
                } else {
                    addTeam();
                }

            }
        });
    }

    private void addTeam() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<TeamModel> call = leagueManagerAPI.postTeam(team);
        call.enqueue(new Callback<TeamModel>() {
            @Override
            public void onResponse(Call<TeamModel> call, Response<TeamModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to add Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                name.getEditText().setText("");

                TeamModel teamModel = response.body();
                new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(teamModel.getName() + " added!\nWould you like to add another team?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refresh();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<TeamModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void updateTeam() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<TeamModel> call = leagueManagerAPI.patchTeam(editTeam.get_id(), team);
        call.enqueue(new Callback<TeamModel>() {
            @Override
            public void onResponse(Call<TeamModel> call, Response<TeamModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to rename Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                TeamModel teamModel = response.body();
                new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(teamModel.getName() + " updated!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refresh();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<TeamModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddTeamActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void refresh() {
        SharedPreferences sp = getSharedPreferences("DataState", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshTeam", true);
        editor.commit();

        finish();
    }
}