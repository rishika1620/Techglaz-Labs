package com.example.radha.techglaz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private MongoDB_Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",MODE_PRIVATE);

        String email = sharedPreferences.getString("email",null);
        //String passwpord =sharedPreferences.getString("password",null);

        if(email == null){
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        }
        else{
            database = new MongoDB_Database(getApplicationContext());
            database.setupDatabase();
            database.isLoggedIn(email, new MongoDB_Database.isLoggedCallback() {
                @Override
                public void onSuccess(Boolean isLogged) {
                    if(isLogged){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                        finish();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("Register",error);
                }
            });
        }
    }

}