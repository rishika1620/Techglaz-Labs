package com.example.radha.techglaz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.bson.Document;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class SplashActivity extends AppCompatActivity {
    ;
    private MongoDB_Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        database = new MongoDB_Database(getApplicationContext());
        database.setupDatabase();
        database.isLoggedIn("raj@gmail.com", new MongoDB_Database.isLoggedCallback() {
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