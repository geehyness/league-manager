package com.yukicide.leaguemanager.UI.leagueCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.Adapters.LeagueAdapter;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.Home.HomeActivity;
import com.yukicide.leaguemanager.UI.userManagement.LoginActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetFavouriteActivity extends AppCompatActivity {
    private LeagueAdapter leagueAdapter;
    private ArrayList<LeagueModel> leagueList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_favourite);

        initRecycler();
        getLeagues();

        FloatingActionButton btnAddLeague = findViewById(R.id.btnAddLeague);
        btnAddLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SetFavouriteActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("New League")
                        .setMessage("Are you sure you want to create a new League?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(SetFavouriteActivity.this, AddLeagueActivity.class));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void getLeagues() {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<List<LeagueModel>> call = leagueManagerAPI.getLeague();
        call.enqueue(new Callback<List<LeagueModel>>() {
            @Override
            public void onResponse(Call<List<LeagueModel>> call, Response<List<LeagueModel>> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    
                    new AlertDialog.Builder(getBaseContext(), R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to get Leagues")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                for (LeagueModel l : response.body()) {
                    leagueList.add(l);
                }
                leagueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<LeagueModel>> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(SetFavouriteActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void initRecycler() {
        RecyclerView leagueRecyclerView = findViewById(R.id.leagueRecycler);
        leagueRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(getBaseContext());
        leagueAdapter = new LeagueAdapter(leagueList);

        leagueRecyclerView.setLayoutManager(itemLayoutManager);
        leagueRecyclerView.setAdapter(leagueAdapter);

        leagueAdapter.setOnItemClickListener(new LeagueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                LeagueModel leagueModel = leagueList.get(position);

                String textToSave = (new Gson()).toJson(leagueModel);

                try {
                    FileOutputStream fileOutputStream = openFileOutput("fav.txt", MODE_PRIVATE);
                    fileOutputStream.write(textToSave.getBytes());
                    fileOutputStream.close();

                    startActivity(new Intent(SetFavouriteActivity.this, HomeActivity.class)
                            .putExtra(StringExtras.LEAGUE, textToSave));
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("fav.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }

            if (ret == "") {
                finish();
            } else {
                startActivity(new Intent(this, HomeActivity.class)
                        .putExtra(StringExtras.LEAGUE, ret));
                finish();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}