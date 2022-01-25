package com.yukicide.leaguemanager.UI.leagueCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.Home.HomeActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddLeagueActivity extends AppCompatActivity {
    TextInputLayout name, logo;
    LeagueModel league, editLeague;
    boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_league);

        Intent i = getIntent();
        if (i.getStringExtra(StringExtras.EDIT) != null)
            edit = (new Gson()).fromJson(i.getStringExtra(StringExtras.EDIT), boolean.class);
        else
            edit = false;

        editLeague = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        name = findViewById(R.id.txtLeagueName);
        //logo = findViewById(R.id.txtLogoLink);

        if (editLeague != null) {
            name.getEditText().setText(editLeague.getName());
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

                league = new LeagueModel(name.getEditText().getText().toString());

                if (editLeague!=null) {
                    updateLeague();
                    //Toast.makeText(AddLeagueActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                } else {
                    addLeague();
                    //Toast.makeText(AddLeagueActivity.this, "New", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addLeague() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<LeagueModel> call = leagueManagerAPI.postLeague(league);
        call.enqueue(new Callback<LeagueModel>() {
            @Override
            public void onResponse(Call<LeagueModel> call, Response<LeagueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to add League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                LeagueModel leagueModel = response.body();
                new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(league.getName() + " added!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String textToSave = (new Gson()).toJson(leagueModel);

                                try {
                                    FileOutputStream fileOutputStream = openFileOutput("fav.txt", MODE_PRIVATE);
                                    fileOutputStream.write(textToSave.getBytes());
                                    fileOutputStream.close();

                                    startActivity(new Intent(AddLeagueActivity.this, HomeActivity.class)
                                            .putExtra(StringExtras.LEAGUE, textToSave));
                                    finish();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<LeagueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void updateLeague() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<LeagueModel> call = leagueManagerAPI.patchLeague(editLeague.get_id(), league);
        call.enqueue(new Callback<LeagueModel>() {
            @Override
            public void onResponse(Call<LeagueModel> call, Response<LeagueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage(response.code()+" - Unable to update League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                LeagueModel leagueModel = response.body();

                String textToSave = (new Gson()).toJson(leagueModel);

                try {
                    FileOutputStream fileOutputStream = openFileOutput("fav.txt", MODE_PRIVATE);
                    fileOutputStream.write(textToSave.getBytes());
                    fileOutputStream.close();

                    new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ball)
                            .setTitle("Success")
                            .setMessage(league.getName() + " updated!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    refresh();
                                }
                            })
                            .show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LeagueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddLeagueActivity.this, R.style.MyAlertDialogStyle)
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
        editor.putBoolean("refreshLeague", true);
        editor.commit();

        finish();
    }
}