package com.yukicide.leaguemanager.UI.venuesCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.VenueAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
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

public class ViewVenuesActivity extends AppCompatActivity implements VenueMenuBottomSheet.BottomSheetListener{

    private LeagueManagerAPI leagueManagerAPI;
    private ArrayList<VenueModel> venueList = new ArrayList<>();
    private VenueAdapter venueAdapter;
    private VenueModel selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_venues);

        initStadiumRecycler();
        initRetrofit();
        getVenues();

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewVenuesActivity.this, AddVenueActivity.class));
            }
        });
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
                    new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Fixtures")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (VenueModel v : response.body()) {
                    venueList.add(v);
                }
                venueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<VenueModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
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

    private void initStadiumRecycler() {
        RecyclerView leagueRecyclerView = findViewById(R.id.stadiumRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        venueAdapter = new VenueAdapter(venueList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(venueAdapter);

        venueAdapter.setOnItemClickListener(new VenueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedItem = venueList.get(position);

                VenueMenuBottomSheet menu = new VenueMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "venueMenu");
            }
        });
    }

    @Override
    public void onButtonClicked(int text) {
        if (text == 1) {
            startActivity(new Intent(this, AddVenueActivity.class)
                    .putExtra(StringExtras.VENUE, (new Gson()).toJson(selectedItem)));

        } else if (text == 2) {
            new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete " + selectedItem.getName())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteVenue();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void deleteVenue() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<VenueModel> call = leagueManagerAPI.deleteVenue(selectedItem.get_id());
        call.enqueue(new Callback<VenueModel>() {
            @Override
            public void onResponse(Call<VenueModel> call, Response<VenueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                VenueModel teamModel = response.body();
                new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(teamModel.getName() + " deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                venueList.clear();
                                getVenues();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<VenueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(ViewVenuesActivity.this, R.style.MyAlertDialogStyle)
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
        boolean refresh = sp.getBoolean("refreshVenues", false);


        if (refresh) {
            venueList.clear();

            getVenues();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshVenues", false);
    }
}