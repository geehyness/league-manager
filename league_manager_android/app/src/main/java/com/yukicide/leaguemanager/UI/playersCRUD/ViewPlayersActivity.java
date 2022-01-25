package com.yukicide.leaguemanager.UI.playersCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.PlayerAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.PlayerModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.TeamModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.teamCRUD.AddTeamActivity;
import com.yukicide.leaguemanager.UI.teamCRUD.TeamMenuBottomSheet;
import com.yukicide.leaguemanager.UI.teamCRUD.ViewTeamsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewPlayersActivity extends AppCompatActivity {
    private ArrayList<PlayerModel> playerList = new ArrayList<>();
    private PlayerAdapter playerAdapter;
    TeamModel teamModel;
    LeagueManagerAPI leagueManagerAPI;
    private PlayerModel selectedPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_players);

        Intent i = getIntent();
        teamModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.TEAM), TeamModel.class);

        final TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(teamModel.getName() + " Players");

        initRecycler();
        initRetrofit();
        getPlayers();

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewPlayersActivity.this, AddPlayerActivity.class)
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(teamModel)));
            }
        });
    }

    private void initRecycler(){
        RecyclerView playerRecyclerView = findViewById(R.id.playerRecycler);
        playerRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        playerAdapter = new PlayerAdapter(playerList);

        playerRecyclerView.setLayoutManager(itemLayoutManager);
        playerRecyclerView.setAdapter(playerAdapter);

        playerAdapter.setOnItemClickListener(new PlayerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                selectedPlayer = playerList.get(position);

                /* PlayerMenuBottomSheet menu = new PlayerMenuBottomSheet();
                assert getFragmentManager() != null;
                menu.show(getSupportFragmentManager(), "playerMenu"); */

                PopupMenu popupMenu = new PopupMenu(playerRecyclerView.findViewHolderForAdapterPosition(position).itemView.getContext(),
                        playerRecyclerView.findViewHolderForAdapterPosition(position).itemView);
                popupMenu.inflate(R.menu.player_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.player_rename:
                                menuChoice(1);
                                return true;

                            case R.id.player_delete:
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

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
    }

    private void getPlayers() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<List<PlayerModel>> call = leagueManagerAPI.getTeamPlayers(teamModel.get_id());
        call.enqueue(new Callback<List<PlayerModel>>() {
            @Override
            public void onResponse(Call<List<PlayerModel>> call, Response<List<PlayerModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Fixtures")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (PlayerModel t : response.body()) {
                    playerList.add(t);
                }
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<PlayerModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    public void menuChoice(int text) {
        if (text == 1) {
            startActivity(new Intent(this, AddPlayerActivity.class)
                    .putExtra(StringExtras.PLAYER, (new Gson()).toJson(selectedPlayer))
                    .putExtra(StringExtras.TEAM, (new Gson()).toJson(teamModel))
                    .putExtra(StringExtras.EDIT, true));

        } else if (text == 2) {
            new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
                    .setIcon(R.drawable.ic_baseline_warning)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete " + selectedPlayer.getName())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePlayer();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void deletePlayer() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<PlayerModel> call = leagueManagerAPI.deletePlayer(selectedPlayer.get_id());
        call.enqueue(new Callback<PlayerModel>() {
            @Override
            public void onResponse(Call<PlayerModel> call, Response<PlayerModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete Team")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                PlayerModel playerModel = response.body();
                new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(playerModel.getName() + " deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                playerList.clear();
                                getPlayers();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<PlayerModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(ViewPlayersActivity.this, R.style.MyAlertDialogStyle)
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
        boolean refresh = sp.getBoolean("refreshPlayer", false);

        if (refresh) {
            playerList.clear();
            getPlayers();
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshPlayer", false);
        editor.commit();
    }
}