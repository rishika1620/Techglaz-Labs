package com.example.radha.techglaz;

import android.content.SharedPreferences;
import android.util.Log;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.result.UpdateResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import android.content.Context;
import android.widget.Toast;

public class MongoDB_Database {
    private static final String APP_ID = "techglaz-nhykatf";
    private static final String DATABASE_NAME = "TechglazDatabase";
    private static final String COLLECTION_NAME = "TechglazCollection";

    private App app;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private static MongoCollection<Document> collectionCertiticate,collectionRegisterCourse;
    Context context;

    public MongoDB_Database(Context context){
        app = new App(new AppConfiguration.Builder(APP_ID).build());
        this.context = context;
    }

    public void login(String email, String password,DBCallback callback) {
      //  Credentials credentials = Credentials.emailPassword(email,password);

        Document query = new Document("email",email);
        collection.findOne(query).getAsync(result -> {
            if (result.isSuccess()) {
                Document foundDocument = result.get();
                if (foundDocument != null) {
                    String dbPassword = foundDocument.getString("password");
                    if(dbPassword.equals(password)){
                        Document updateValue = new Document("$set", new Document("isLoggedIn", true));
                        collection.updateOne(query, updateValue).getAsync(LoggedIn -> {
                            if (LoggedIn.isSuccess()) {
                                long count = LoggedIn.get().getModifiedCount();
                                if (count > 0) {
                                    callback.onSuccess();
                                    Log.d("Login","Document updated successfully");
                                } else {
                                    Log.d("Login","No document found with the given email");
                                }
                            } else {
                                //   Log.d("Login","Failed to update document: " + result.getError().toString());
                            }
                        });
                    }
                    else{
                        callback.onError("Incorrect password");
                    }
                }else{
                    Log.d("Database", "Error in isLoggedIn" + result.getError().toString());
                    callback.onError("Error in isLoggedIn" + result.getError().toString());
                }
            } else {
                System.err.println("Failed to find document: " + result.getError().toString());
            }
        });
    }

    public void logout(String email,isLoggedCallback callback){
        Document query = new Document("email",email);
        Document updateValue = new Document("$set", new Document("isLoggedIn", false));
        if (collection == null) {
            Log.d("Login","MongoCollection is null. Check your collection name.");
        }
        collection.updateOne(query, updateValue).getAsync(LoggedIn -> {
            if (LoggedIn.isSuccess()) {
                long count = LoggedIn.get().getModifiedCount();
                if (count > 0) {
                    Log.d("Login","Document updated successfully");
                    callback.onSuccess(false);
                } else {
                    Log.d("Login","No document found with the given email");
                    callback.onError(LoggedIn.getError().toString());
                }
            }
        });
    }

    public void isLoggedIn(String email,isLoggedCallback callback){
        Document query = new Document("email", email);
        collection.findOne(query).getAsync(result -> {
            if (result.isSuccess()) {
                Document foundDocument = result.get();
                if (foundDocument != null) {
                    Boolean isLogged = foundDocument.getBoolean("isLoggedIn");
                    callback.onSuccess(isLogged);
                }else{
                    Log.d("Database", "Error in isLoggedIn" + result.getError().toString());
                    callback.onError("Error in isLoggedIn" + result.getError().toString());
                }
            } else {
                System.err.println("Failed to find document: " + result.getError().toString());
            }
        });
    }

    public void signup(User_Model userDetails,DBCallback callback) {
        Document newUser = new Document("username", userDetails.getName())
                .append("email", userDetails.getEmail())
                .append("phone", userDetails.getPhone_no())
                .append("isLoggedIn",userDetails.getLoggeIn())
                .append("password", userDetails.getPassword());

        // Credentials credentials = Credentials.emailPassword(userDetails.getEmail(), userDetails.getPassword());
        app.getEmailPassword().registerUserAsync(userDetails.getEmail(), userDetails.getPassword(), it -> {
            if (it.isSuccess()) {
                Log.d("Database", "Registration Sucessfull");
                setupDatabase();
                collection.insertOne(newUser).getAsync(result -> {
                    if (result.isSuccess()) {
                        Log.d("Database", "Data Inserted");
                        callback.onSuccess();
                    } else {
                        Log.d("Database", "Data not inserted"+result.getError().toString());
                        callback.onError("Data not inserted"+result.getError().toString());
                    }
                });
            } else {
                Toast.makeText(context,"Registration Failed " + it.getError(),Toast.LENGTH_LONG).show();
                Log.d("Database", "Registration Failed" + it.getError().toString());
            }
        });
    }

    public void isAlreadyExists(String email,DBCallback callback){
        Document query = new Document("email", email);
        collection.find(query).iterator().getAsync(task -> {
            if (task.isSuccess()) {
                if (task.get().hasNext()) {
                    callback.onSuccess();
                    // User exists, proceed to log in
                } else {
                    // User does not exist, register new user
                   callback.onError("Not registered");
                }
            } else {
                // Handle query failure
                callback.onError("No data found");
            }
        });
    }

    public void changePassword(String email,String newPassword,DBCallback callback){
        Document query = new Document("email",email);
        Document updateValue = new Document("$set", new Document("password", newPassword));

        collection.updateOne(query,updateValue).getAsync(result -> {
            if(result.isSuccess()){

                callback.onSuccess();
            }
            else{
                callback.onError("Unable to change Password");
            }
        });
    }

    public void setupDatabase() {
        /*SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email",null);
        String password = sharedPreferences.getString("password",null);*/

       /* String email = "rishika@gmail.com";
        String password = "12345678";*/
       // if(email == null){
            app.loginAsync(Credentials.anonymous(),result -> {
                if(result.isSuccess()){
                    MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
                    database = mongoClient.getDatabase(DATABASE_NAME);
                    collection = database.getCollection(COLLECTION_NAME);
                    collectionCertiticate = database.getCollection("Certificates");
                    collectionRegisterCourse =database.getCollection("CourseRegistration");
                }
            });
       /*}
       else{
            Credentials credentials  = Credentials.emailPassword(email,password);
            app.loginAsync(credentials,result -> {
                if(result.isSuccess()){
                    MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
                    database = mongoClient.getDatabase(DATABASE_NAME);
                    collection = database.getCollection(COLLECTION_NAME);
                    collectionCertiticate = database.getCollection("Certificates");
                    collectionRegisterCourse =database.getCollection("CourseRegistration");
                }
            });
        }*/

        if (app.currentUser() != null) {
            MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
            database = mongoClient.getDatabase(DATABASE_NAME);
            collection = database.getCollection(COLLECTION_NAME);
            collectionCertiticate = database.getCollection("Certificates");
            Log.d("Database","Connected to Database");

            if(database == null){
                Log.d("Login","Database is null.");
            }
            else{
                Log.d("Login","Database is connected.");

            }
            if (collection == null) {
                Log.d("Login","MongoCollection is null. Check your collection name.");
            }
            else{
                Log.d("Login","Collection is not null.");

            }
        }
        else{
            Log.d("Database","app user is null");
        }

    }

    public interface DBCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface isLoggedCallback{
        void onSuccess(Boolean isLogged);
        void onError(String error);
    }


    public void uploadPDF(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email",null);

        Document document = new Document("filename", "example.pdf")
                .append("certificate_id","123456")
                .append("email",email);

        collectionCertiticate.insertOne(document).getAsync(result -> {
            if(result.isSuccess()){
                Log.d("PDF_upload","Certificte uploaded");
                Toast.makeText(context,"Certificate uploaded",Toast.LENGTH_LONG).show();
            }
            else{
                Log.d("PDF_upload","Certificte not uploaded" + result.getError());
            }
        });
    }

    public void verifyPDF(String id, DBCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email",null);
        Document query = new Document("email", email);
        collectionCertiticate.findOne(query).getAsync(result -> {
            if (result.isSuccess()) {
                Document foundDocument = result.get();
                if (foundDocument != null) {
                    if(id.equals(foundDocument.getString("certificate_id"))){
                        callback.onSuccess();
                        Log.d("Database","Certificate Found");
                    }
                    else{
                        callback.onError("Incorrect Certificate Id");
                    }
                }else{
                   // Log.d("Database", "No Certificate Found");
                    callback.onError("No Certificate Found");
                }
            } else {
                System.err.println("Failed to find document: " + result.getError().toString());
            }
        });
    }


    public void registerCourseDB(RegisterCourse_Model rc_user,DBCallback  callback){
        Document newRegistration = new Document("username", rc_user.getUserName())
                .append("email", rc_user.getEmail())
                .append("phone", rc_user.getPhone_no())
                .append("address",rc_user.getAddress())
                .append("collegeName",rc_user.getCollegeName())
                .append("branch",rc_user.getBranch())
                .append("yearOfGraduation",rc_user.getYearOfGraduation())
                .append("courseName",rc_user.getCourseName())
                .append("mode",rc_user.getMode())
                .append("StartDate",rc_user.getStartDate())
                .append("EndDate",rc_user.getEndDate());

        collectionRegisterCourse.insertOne(newRegistration).getAsync(result -> {
            if(result.isSuccess()){
                Log.d("Registartion","Registration Successfull to DB");
                callback.onSuccess();
            }
            else{
                callback.onError(result.getError().toString());
                Log.d("Registartion","Registration failed to DB");
            }
        });

    }


}
