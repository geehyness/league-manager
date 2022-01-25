package com.yukicide.leaguemanager.UI.Home.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.VenueAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.VenueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.venuesCRUD.AddVenueActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VenuesFragment extends Fragment {

    private LeagueManagerAPI leagueManagerAPI;
    private ArrayList<VenueModel> venueList = new ArrayList<>();
    private VenueAdapter venueAdapter;
    private VenueModel selectedItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_venues, container, false);

        initStadiumRecycler(v);
        initRetrofit();
        getVenues();

        Button btnAdd = v.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddVenueActivity.class));
            }
        });

        return v;
    }

    private void getVenues() {
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
                            .setMessage("Unable to get Fixtures")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                venueList.clear();

                for (VenueModel v : response.body()) {
                    venueList.add(v);
                }
                venueAdapter.notifyDataSetChanged();
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

    private void initStadiumRecycler(View v) {
        RecyclerView venueRecyclerView = v.findViewById(R.id.stadiumRecycler);
        venueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(getContext());
        venueAdapter = new VenueAdapter(venueList);

        venueRecyclerView.setLayoutManager(itemLayoutManager);
        venueRecyclerView.setAdapter(venueAdapter);

        venueAdapter.setOnItemClickListener(new VenueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedItem = venueList.get(position);

                /* VenueMenuBottomSheet menu = new VenueMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "venueMenu"); */

                PopupMenu popupMenu = new PopupMenu(venueRecyclerView.findViewHolderForAdapterPosition(position).itemView.getContext(),
                        venueRecyclerView.findViewHolderForAdapterPosition(position).itemView);
                popupMenu.inflate(R.menu.venue_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.stadium_rename:
                                menuChoice(1);
                                return true;

                            case R.id.stadium_remove:
                                menuChoice(2);
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

    public void menuChoice(int text) {
        if (text == 1) {
            startActivity(new Intent(getContext(), AddVenueActivity.class)
                    .putExtra(StringExtras.VENUE, (new Gson()).toJson(selectedItem)));

        } else if (text == 2) {
            new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
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
        final MyProgressDialog progressDialog = new MyProgressDialog(getContext());
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
                    new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                VenueModel teamModel = response.body();
                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
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
        boolean refresh = sp.getBoolean("refreshVenues", false);


        if (refresh) {
            venueList.clear();

            getVenues();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshVenues", false);
    }
}