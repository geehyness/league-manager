package com.yukicide.leaguemanager.UI.userManagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yukicide.leaguemanager.JavaRepositories.LeagueManagerAPI;
import com.yukicide.leaguemanager.JavaRepositories.Models.UserModel;
import com.yukicide.leaguemanager.JavaRepositories.MyProgressDialog;
import com.yukicide.leaguemanager.JavaRepositories.StringExtras;
import com.yukicide.leaguemanager.R;
import com.yukicide.leaguemanager.UI.Home.HomeActivity;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private UserModel loginUser;
    private LeagueManagerAPI leagueManagerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button login = findViewById(R.id.btnLogin);
        TextView reg = findViewById(R.id.tvRegister);

        login.setOnClickListener(this);
        reg.setOnClickListener(this);

        /*final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setMessage("Checking user status");
        progressBar.show();
        if(fbAuth.getCurrentUser() != null){
            startActivity(new Intent(this, HomeActivity.class));
        }
        progressBar.hide();*/
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.btnLogin)) {
            login();
        }
        if (v == findViewById(R.id.tvRegister)) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    private void login() {
        final UserModel[] currUser = {null};
        EditText email = findViewById(R.id.txtEmail),
                password = findViewById(R.id.txtPassword);

        final String uemail = email.getText().toString().trim();
        String upass = password.getText().toString().trim();

        // Validating input
        if (TextUtils.isEmpty(uemail)) {
            email.setError("Student number cannot be empty!");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
            email.setError("Email is invalid!");
            return;
        }

        if (TextUtils.isEmpty(upass)) {
            password.setError("Password cannot be empty!");
            password.requestFocus();
            return;
        }

        if (upass.length() < 8) {
            password.setError("Password should be atleast 8 characters long!");
            password.requestFocus();
            return;
        }

        initRetrofit();
        register(new UserModel(uemail, upass));
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://league-manager.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leagueManagerAPI = retrofit.create(LeagueManagerAPI.class);
    }

    private void register(UserModel userModel) {
        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<UserModel> call = leagueManagerAPI.loginUser(userModel);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage("The email or password you have entered is incorrect. Please try again!")
                                .setPositiveButton("Ok", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage(response.code() + " - Try again")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                    return;
                }

                loginUser = response.body();

                String textToSave = (new Gson()).toJson(loginUser);

                try {
                    FileOutputStream fileOutputStream = openFileOutput("user.txt", MODE_PRIVATE);
                    fileOutputStream.write(textToSave.getBytes());
                    fileOutputStream.close();

                    getFavouriteLeague();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                progressDialog.dismiss();

                new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void getFavouriteLeague() {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("fav.txt");

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
                startActivity(new Intent(LoginActivity.this, HomeActivity.class)
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