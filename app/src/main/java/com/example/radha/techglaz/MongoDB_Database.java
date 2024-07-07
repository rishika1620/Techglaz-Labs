package com.example.radha.techglaz;

import static com.example.radha.techglaz.PDF_Certificate.createPdf;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.iterable.MongoCursor;

import org.bson.Document;
import org.bson.types.Binary;

import android.content.Context;
import android.widget.Toast;

import java.io.OutputStream;

public class MongoDB_Database {
    private static final String APP_ID = "techglaz-nhykatf";
    private static final String DATABASE_NAME = "TechglazDatabase";
    private static final String COLLECTION_NAME = "TechglazCollection";

    private App app;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private static MongoCollection<Document> collectionCertiticate;
    Context context;

    public MongoDB_Database(Context context){
        app = new App(new AppConfiguration.Builder(APP_ID).build());
        Log.d("App","App is here");
        this.context = context;
    }

    public void login(String email, String password,DBCallback callback) {
        Credentials credentials = Credentials.emailPassword(email,password);

        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.d("Database","Login Sucessfull");
                    Document query = new Document("email",email);
                    Document updateValue = new Document("$set", new Document("isLoggedIn", true));
                    if (collection == null) {
                        Log.d("Login","MongoCollection is null. Check your collection name.");
                    }
                    collection.updateOne(query, updateValue).getAsync(LoggedIn -> {
                        if (LoggedIn.isSuccess()) {
                            long count = LoggedIn.get().getModifiedCount();
                            if (count > 0) {
                                Log.d("Login","Document updated successfully");
                            } else {
                                Log.d("Login","No document found with the given email");
                            }
                        } else {
                            Log.d("Login","Failed to update document: " + result.getError().toString());
                        }
                    });
                    callback.onSuccess();
                } else {
                    Log.d("Databse","Login Failed" + result.getError().toString());
                    callback.onError("Failed to login " + result.getError().toString());
                }
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
                Log.d("Database", "Registration Failed" + it.getError().toString());
            }
        });
    }

    public void setupDatabase() {
        Credentials credentials  = Credentials.emailPassword("raj@gmail.com","11114444");
        app.loginAsync(credentials,result -> {
            if(result.isSuccess()){
                MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
                database = mongoClient.getDatabase(DATABASE_NAME);
                collection = database.getCollection(COLLECTION_NAME);
                collectionCertiticate = database.getCollection("Certificates");
            }
        });
        if (app.currentUser() != null) {
            MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
            database = mongoClient.getDatabase(DATABASE_NAME);
            collection = database.getCollection(COLLECTION_NAME);
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


    public void uploadPDF(byte[] pdfData){
        Document document = new Document("filename", "example.pdf")
                .append("id","123456")
                .append("file", new Binary(pdfData));

        collection.insertOne(document).getAsync(result -> {
            if(result.isSuccess()){
                Log.d("PDF_upload","Certificte uploaded");
                Toast.makeText(context,"Certificate uploaded",Toast.LENGTH_LONG).show();
            }
            else{
                Log.d("PDF_upload","Certificte not uploaded" + result.getError());
            }
        });
    }

    public void verifyPDF(String filename, Context context) {
        org.bson.Document query = new org.bson.Document("filename", filename);
        Log.d("PDF","Searching PDF");
        collection.find(query).iterator().getAsync(task -> {
            if (task.isSuccess()) {
                Log.d("PDF","Verified PDF from database");
                /*MongoCursor<org.bson.Document> results = task.get();
                if (results.hasNext()) {
                    Log.d("PDF","Converting to byte");
                    org.bson.Document doc = results.next();
                    Binary binary = doc.get("file", Binary.class);
                    byte[] data = binary.getData();
                    Toast.makeText(context,"Verified Data",Toast.LENGTH_LONG).show();
                    // Proceed to save the PDF
                   // savePdfToStorage(context, filename, data);
                }
                else{
                    Log.d("PDF","PDF is not converting");
                }*/
            } else {
                Log.e("MongoDB Realm", "Failed to retrieve data: " + task.getError());
            }
        });
    }

    public static void savePdfToStorage(Context context, String filename, byte[] data) {
        Log.d("PDF","Saving PDF");
        String pdfName = filename + ".pdf";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, pdfName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/MyApp");

        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                createPdf(outputStream, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
