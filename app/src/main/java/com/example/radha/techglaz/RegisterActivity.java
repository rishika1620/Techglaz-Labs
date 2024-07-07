package com.example.radha.techglaz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class RegisterActivity extends AppCompatActivity {

    TextView login_tv;
    Button btn_signup;


    EditText usename_edt;
    EditText email_edt;
    EditText phone_edt;
    EditText password_edt;
    EditText cnf_passwrd_edt;
    String username,email,phone,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register);


        usename_edt = findViewById(R.id.register_username);
        email_edt = findViewById(R.id.register_email);
        phone_edt = findViewById(R.id.register_phone);
        password_edt = findViewById(R.id.register_password);
        cnf_passwrd_edt = findViewById(R.id.register_conf_password);

        login_tv = findViewById(R.id.login_text);
        btn_signup= findViewById(R.id.btn_Signup);

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCredentials()){
                    Log.d("Register", "Credentials are alright");
                    register_db();
                }

            }
        });
    }

    private boolean checkCredentials() {
        username = usename_edt.getText().toString();
        email = email_edt.getText().toString();
        phone = phone_edt.getText().toString();
        password = password_edt.getText().toString();
        String cnf_password = cnf_passwrd_edt.getText().toString();

        if(username.isEmpty()){
            showError(usename_edt,"Your username is not valid");
            return false;
        }
        else if(email.isEmpty() || !email.contains("@gmail.com")){
            showError(email_edt,"Email is not valid");
            return false;
        }
        else if(phone.length() != 10){
            showError(phone_edt,"Number is not valid");
            return false;
        }
        else if(password.isEmpty() || password.length()<8){
            showError(password_edt,"Password must contain at least 8 charcters");
            return false;
        }
        else if(cnf_password.isEmpty() || !cnf_password.equals(password)){
            showError(cnf_passwrd_edt,"Password not matched");
            return false;
        }
        else{
            return true;
        }
    }

    private void register_db() {
        MongoDB_Database database = new MongoDB_Database(getApplicationContext());
        Log.d("Database", "Connecting database");
        database.setupDatabase();
        User_Model userModel = new User_Model(username,email,phone,password,false);
        database.signup(userModel, new MongoDB_Database.DBCallback() {
            @Override
            public void onSuccess() {
                login();
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void showError(EditText view, String error) {
        view.setError(error);
        view.requestFocus();
    }

    public void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}