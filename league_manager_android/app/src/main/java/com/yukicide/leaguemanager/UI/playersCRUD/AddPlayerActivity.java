package com.yukicide.leaguemanager.UI.playersCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.teamCRUD.AddTeamActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddPlayerActivity extends AppCompatActivity {

    private TeamModel teamModel;
    private TextInputLayout name;
    private PlayerModel editPlayer;
    private PlayerModel player;
    private TextInputLayout num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        Intent i = getIntent();
        teamModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.TEAM), TeamModel.class);
        editPlayer = (new Gson()).fromJson(i.getStringExtra(StringExtras.PLAYER), PlayerModel.class);

        name = findViewById(R.id.txtPlayerName);
        num = findViewById(R.id.txtPlayerNum);
        //logo = findViewById(R.id.txtLogoLink);
        TextView title = findViewById(R.id.txtTitle);
        title.setText(teamModel.getName() + " player");

        if (editPlayer!=null) {
            name.getEditText().setText(editPlayer.getName());
            num.getEditText().setText(editPlayer.getNumber()+"");
        }

        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getEditText().getText().toString())) {
                    name.getEditText().setError("Name cannot be empty!");
                    return;
                }

                if (TextUtils.isEmpty(num.getEditText().getText().toString())) {
                    num.getEditText().setError("Number cannot be empty!");
                    return;
                }

                /*if (TextUtils.isEmpty(logo.getEditText().getText().toString())) {
                    name.getEditText().setError("Put a link to the league logo!");
                    return;
                }*/

                player = new PlayerModel();
                player.setName(name.getEditText().getText().toString());
                player.setNumber(Integer.parseInt(num.getEditText().getText().toString()));
                player.setTeamId(teamModel.get_id());

                if (editPlayer != null) {
                    updatePlayer();
                } else {
                    addPlayer();
                }

            }
        });
    }

    private void addPlayer() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<PlayerModel> call = leagueManagerAPI.postPlayer(player);
        call.enqueue(new Callback<PlayerModel>() {
            @Override
            public void onResponse(Call<PlayerModel> call, Response<PlayerModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage(response.code() + " Unable to add Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                name.getEditText().setText("");
                num.getEditText().setText("");

                PlayerModel playerModel = response.body();
                new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(playerModel.getName() + " added!\nWould you like to add another player?")
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
            public void onFailure(Call<PlayerModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void updatePlayer() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<PlayerModel> call = leagueManagerAPI.patchPlayer(editPlayer.get_id(), player);
        call.enqueue(new Callback<PlayerModel>() {
            @Override
            public void onResponse(Call<PlayerModel> call, Response<PlayerModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to rename Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                PlayerModel teamModel = response.body();
                new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
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
            public void onFailure(Call<PlayerModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddPlayerActivity.this, R.style.MyAlertDialogStyle)
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
        editor.putBoolean("refreshPlayer", true);
        editor.commit();

        finish();
    }
}