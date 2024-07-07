package com.example.radha.techglaz;

import android.app.Application;
import android.util.Log;

import org.bson.Document;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class TechglazApplication extends Application {

    public void onCreate() {
        super.onCreate();
        // Initialize Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        Log.d("TechglazApp","Initializing Database");
    }
}
