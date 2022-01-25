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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    LeagueManagerAPI leagueManagerAPI;
    UserModel regUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button register = findViewById(R.id.btnRegister);
        TextView reg = findViewById(R.id.lblLogin);

        register.setOnClickListener(this);
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
        if (v == findViewById(R.id.btnRegister)) {
            login();
        }
        if (v == findViewById(R.id.lblLogin)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void login() {
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

        Call<UserModel> call = leagueManagerAPI.regUser(userModel);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        new AlertDialog.Builder(RegisterActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage("Account already exists!")
                                .setPositiveButton("Ok", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(RegisterActivity.this, R.style.MyAlertDialogStyle)
                                .setIcon(R.drawable.ic_baseline_warning)
                                .setTitle("Error")
                                .setMessage(response.code() + " - Try again")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                    return;
                }

                regUser = response.body();

                String textToSave = (new Gson()).toJson(regUser);

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

                new AlertDialog.Builder(RegisterActivity.this, R.style.MyAlertDialogStyle)
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
                startActivity(new Intent(RegisterActivity.this, HomeActivity.class)
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