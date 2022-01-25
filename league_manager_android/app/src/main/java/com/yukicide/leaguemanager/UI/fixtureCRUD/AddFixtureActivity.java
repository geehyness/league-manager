package com.yukicide.leaguemanager.UI.fixtureCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamFixtures;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.DatePickerFragment;
import com.yukicide.leaguemanager.UI.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFixtureActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Calendar date;
    LeagueModel leagueModel;
    TeamFixtures teamFixtures;
    Spinner spTeam1, spTeam2, spVenues;
    ArrayList<VenueModel> venuesList = new ArrayList<>();

    LeagueManagerAPI leagueManagerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fixture);

        Intent i = getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);
        teamFixtures = (new Gson()).fromJson(i.getStringExtra(StringExtras.TEAM_FIXTURES), TeamFixtures.class);

        createTeamSpinners();
        initRetrofit();
        getVenues();

        Button button = (Button) findViewById(R.id.btnTime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFixture();
            }
        });
    }

    private void addFixture() {
        if (spTeam1.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Missing details")
                    .setMessage("Select the first team in the fixture!")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        if (spTeam2.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Missing details")
                    .setMessage("Select the second team in the fixture!")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        if (date == null) {
            new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Missing details")
                    .setMessage("Set the fixture time!")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        if (spVenues.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Missing details")
                    .setMessage("Select a venue!")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        FixtureModel fixtureModel = new FixtureModel();
        fixtureModel.setLeagueId(leagueModel.get_id());
        fixtureModel.setTeam1Id(teamFixtures.getTeamList().get(spTeam1.getSelectedItemPosition() - 1).get_id());
        fixtureModel.setTeam2Id(teamFixtures.getTeamList().get(spTeam2.getSelectedItemPosition() - 1).get_id());
        fixtureModel.setTime(date.getTimeInMillis());
        fixtureModel.setVenueId(venuesList.get(spVenues.getSelectedItemPosition() - 1).get_id());

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<FixtureModel> call = leagueManagerAPI.addFixture(fixtureModel);
        call.enqueue(new Callback<FixtureModel>() {
            @Override
            public void onResponse(Call<FixtureModel> call, Response<FixtureModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    if (response.code() == 403) {
                        new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage("Venue is Occupied! Try schedule the fixture for another time.")
                                .setPositiveButton("Ok", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage("An error occurred! Please check the inputted data and try again.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                    return;
                }

                if (response.code() == 200) {
                    FixtureModel fixture = response.body();

                    new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_done)
                            .setTitle("Success")
                            .setMessage("Fixture Added")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    refresh();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<FixtureModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        date = c;

        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
        date.set(Calendar.MINUTE, minute);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        TextView txtTime = findViewById(R.id.txtTime);
        txtTime.setText("Date & Time: " + formatter.format(date.getTime()));
    }

    private void createTeamSpinners(){
        spTeam1 = findViewById(R.id.spTeam1);
        spTeam2 = findViewById(R.id.spTeam2);

        ArrayList<String> strList = new ArrayList<>();
        strList.add("Select a team");
        for (TeamModel t : teamFixtures.getTeamList()) {
            strList.add(t.getName());
        }

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(Objects.requireNonNull(AddFixtureActivity.this), android.R.layout.simple_spinner_item, strList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTeam1.setAdapter(catAdapter);
        spTeam2.setAdapter(catAdapter);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
    }

    private void getVenues() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<VenueModel>> call = leagueManagerAPI.getAllVenues();
        call.enqueue(new Callback<List<VenueModel>>() {
            @Override
            public void onResponse(Call<List<VenueModel>> call, Response<List<VenueModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {

                    new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
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

                createVenueSpinner();
            }

            @Override
            public void onFailure(Call<List<VenueModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(AddFixtureActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void createVenueSpinner() {
        spVenues = findViewById(R.id.spVenues);

        ArrayList<String> strList = new ArrayList<>();
        strList.add("Select a venue");
        for (VenueModel v : venuesList) {
            strList.add(v.getName());
        }

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(Objects.requireNonNull(AddFixtureActivity.this), android.R.layout.simple_spinner_item, strList);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVenues.setAdapter(catAdapter);
    }

    private void refresh() {
        SharedPreferences sp = getSharedPreferences("DataState", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshFixture", true);
        editor.commit();

        finish();
    }
}