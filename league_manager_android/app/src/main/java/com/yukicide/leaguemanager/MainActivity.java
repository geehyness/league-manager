package com.yukicide.leaguemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.UI.Home.HomeActivity;
import com.yukicide.leaguemanager.UI.fixtureCRUD.AddFixtureActivity;
import com.yukicide.leaguemanager.UI.leagueCRUD.SetFavouriteActivity;
import com.yukicide.leaguemanager.UI.userManagement.LoginActivity;
import com.yukicide.leaguemanager.UI.userManagement.RegisterActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("user.txt");

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
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                getFavouriteLeague();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFavouriteLeague() {
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
                startActivity(new Intent(this, HomeActivity.class)
                    .putExtra(StringExtras.LEAGUE, ret));
                finish();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(this, SetFavouriteActivity.class));
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}