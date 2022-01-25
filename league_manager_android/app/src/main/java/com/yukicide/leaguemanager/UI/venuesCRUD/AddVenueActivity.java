package com.yukicide.leaguemanager.UI.venuesCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddVenueActivity extends AppCompatActivity {
    TextInputLayout name, logo;
    VenueModel venueModel, editVenue;
    boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        Intent i = getIntent();
        if (i.getStringExtra(StringExtras.EDIT) != null)
            edit = (new Gson()).fromJson(i.getStringExtra(StringExtras.EDIT), boolean.class);
        else
            edit = false;

        editVenue = (new Gson()).fromJson(i.getStringExtra(StringExtras.VENUE), VenueModel.class);

        name = findViewById(R.id.txtVenueName);
        //logo = findViewById(R.id.txtLogoLink);

        if (editVenue != null) {
            name.getEditText().setText(editVenue.getName());
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

                venueModel = new VenueModel(name.getEditText().getText().toString());

                if (editVenue !=null) {
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
        Call<VenueModel> call = leagueManagerAPI.postVenue(venueModel);
        call.enqueue(new Callback<VenueModel>() {
            @Override
            public void onResponse(Call<VenueModel> call, Response<VenueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to add League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                VenueModel venueModel = response.body();
                new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(venueModel.getName() + " added!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refresh();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<VenueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
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
        Call<VenueModel> call = leagueManagerAPI.patchVenue(editVenue.get_id(), venueModel);
        call.enqueue(new Callback<VenueModel>() {
            @Override
            public void onResponse(Call<VenueModel> call, Response<VenueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage(response.code()+" - Unable to update League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                VenueModel venueModel = response.body();
                new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(venueModel.getName() + " updated!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refresh();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<VenueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddVenueActivity.this, R.style.MyAlertDialogStyle)
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
        editor.putBoolean("refreshVenues", true);
        editor.commit();

        finish();
    }
}