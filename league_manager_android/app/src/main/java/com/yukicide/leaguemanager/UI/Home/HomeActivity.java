package com.yukicide.leaguemanager.UI.Home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.FixtureModel;
import com.yukicide.leaguemanager.JavaRepositories.Models.LeagueModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.Home.Fragments.FixturesFragment;
import com.yukicide.leaguemanager.UI.Home.Fragments.TeamsFragment;
import com.yukicide.leaguemanager.UI.Home.Fragments.VenuesFragment;
import com.yukicide.leaguemanager.UI.leagueCRUD.AddLeagueActivity;
import com.yukicide.leaguemanager.UI.leagueCRUD.SetFavouriteActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    LeagueModel leagueModel;
    TextView txtLeague;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        leagueModel = (new Gson()).fromJson(i.getStringExtra(StringExtras.LEAGUE), LeagueModel.class);

        txtLeague = findViewById(R.id.txtLeagueName);
        txtLeague.setText(leagueModel.getName());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                new TeamsFragment()).commit();

        ImageView leagueOptions = findViewById(R.id.league_options);
        leagueOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, leagueOptions);
                popupMenu.inflate(R.menu.league_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.league_rename:
                                startActivity(new Intent(HomeActivity.this, AddLeagueActivity.class)
                                        .putExtra(StringExtras.LEAGUE, (new Gson()).toJson(leagueModel))
                                        .putExtra(StringExtras.EDIT, true));
                                return true;

                            case R.id.league_delete:
                                new AlertDialog.Builder(HomeActivity.this, R.style.MyAlertDialogStyle)
                                        .setIcon(R.drawable.ic_baseline_warning)
                                        .setTitle("Warning")
                                        .setMessage("Are you sure you want to delete " + leagueModel.getName())
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteLeague(leagueModel);
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        ImageView leagueChange = findViewById(R.id.league_change);
        leagueChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SetFavouriteActivity.class));
                finish();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        Fragment selectedFrag = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_teams:
                selectedFrag = new TeamsFragment();
                break;
            case R.id.nav_fixtures:
                selectedFrag = new FixturesFragment();
                break;
            case R.id.nav_venues:
                selectedFrag = new VenuesFragment();
                break;
        }

        assert selectedFrag != null;
        HomeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                selectedFrag).commit();
        return true;
    };

    private void deleteLeague(LeagueModel leagueModel) {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LeagueManagerAPI leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
        Call<LeagueModel> call = leagueManagerAPI.deleteLeague(leagueModel.get_id());
        call.enqueue(new Callback<LeagueModel>() {
            @Override
            public void onResponse(Call<LeagueModel> call, Response<LeagueModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(HomeActivity.this, R.style.MyAlertDialogStyle)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle("Error")
                            .setMessage("Unable to delete League")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

                LeagueModel leagueModelResponse = response.body();
                new AlertDialog.Builder(HomeActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ball)
                        .setTitle("Success")
                        .setMessage(leagueModelResponse.getName() + " deleted!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String textToSave = ("");

                                try {
                                    FileOutputStream fileOutputStream = openFileOutput("fav.txt", MODE_PRIVATE);
                                    fileOutputStream.write(textToSave.getBytes());
                                    fileOutputStream.close();

                                    startActivity(new Intent(HomeActivity.this, SetFavouriteActivity.class));
                                    finish();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<LeagueModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(HomeActivity.this, R.style.MyAlertDialogStyle)
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
        super.onResume();
        SharedPreferences sp = getSharedPreferences("DataState", 0);
        boolean refresh = sp.getBoolean("refreshLeague", false);


        if (refresh) {
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
                    startActivity(new Intent(this, SetFavouriteActivity.class));
                    finish();
                } else {
                    leagueModel = (new Gson()).fromJson(ret, LeagueModel.class);
                    txtLeague.setText(leagueModel.getName());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                startActivity(new Intent(this, SetFavouriteActivity.class));
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("refreshLeague", false);
        editor.commit();
    }
}
