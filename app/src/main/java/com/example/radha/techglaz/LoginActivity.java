package com.example.radha.techglaz;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    Button dBVerifyOTP;
    Button dBCancel;
    TextView SignUp;
    TextView set_number;

    EditText id_edt;
    EditText password_edt;
    EditText otp_1;
    EditText otp_2;
    EditText otp_3;
    EditText otp_4;

    ImageView emailSignIn;
    ImageView linkedInSignIn;
    ImageView githubSignIn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Dialog dialog;

    private static final String CLIENT_ID_LinkedIn = "86c27xdtjk2d6j";
    private static final String CLIENT_SECRET_LinkedIn = "8BApFR79UURyUboM";
    //private static final String REDIRECT_URI_LinkedIn = "YOUR_REDIRECT_URI";
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String PROFILE_URL = "https://api.linkedin.com/v2/me";

    private static final String CLIENT_ID_Github = "Ov23liVaO3k3KM28ebwW";
    private static  final String CLIENT_SECRET_Github= "99ca31a38ea32ef2d3ddaf4299fe19bb3e47cfbd";
    private static final String REDIRECT_URI_Github = "techglaz://callback";

    Random random;
    int otp;;

    String user_id,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        id_edt = findViewById(R.id.login_phone);
        btn_login = findViewById(R.id.button_login);
        SignUp  = findViewById(R.id.signup_tv);
        emailSignIn = findViewById(R.id.email_login);
        linkedInSignIn = findViewById(R.id.linkedin_login);
        githubSignIn = findViewById(R.id.github_login);
        password_edt = findViewById(R.id.login_password);

        set_Dialog();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCredentials()){
                    login_db();
                    //dialog.show();
                }
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

        emailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInEmail();
            }
        });

        githubSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOAuthFlow();
            }
        });

        linkedInSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authUrl = AUTHORIZATION_URL + "?response_type=code&client_id=" + CLIENT_ID_LinkedIn
                        + "&redirect_uri=" + REDIRECT_URI_Github + "&scope=r_liteprofile%20r_emailaddress";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(intent);
            }
        });


    }

    public void login_db(){
        MongoDB_Database mongoDBDatabase = new MongoDB_Database(getApplicationContext());
        mongoDBDatabase.setupDatabase();
        mongoDBDatabase.login(user_id, password, new MongoDB_Database.DBCallback() {
            @Override
            public void onSuccess() {
                openHome();
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void SignInEmail() {
        Log.d("Error","Inside signIn email");
        Intent signInintent = gsc.getSignInIntent();
        startActivityForResult(signInintent,100);
        Log.d("Error","Exit signIn email");
    }

    private boolean checkCredentials() {
        user_id = id_edt.getText().toString();
        password = password_edt.getText().toString();
        if(validate_Phoneno(user_id)){
            return true;

        }
        else if(Patterns.EMAIL_ADDRESS.matcher(user_id).matches()){
            send_otp_email();
            return true;
        }
        else if(user_id.isEmpty()){
            showError(id_edt,"Enter number or email to proceed");
            return false;
        }
        else{
            showError(id_edt,"Invalid number or email");
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                send_otp_phone();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void send_otp_phone(){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            random = new Random();
            otp  = 1000 + random.nextInt(9000);
            String sms = "OTP is : " + Integer.toString(otp);
            smsManager.sendTextMessage(user_id,null,sms,null,null);
            Toast.makeText(getApplicationContext(), "SMS Sent to " + user_id, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void send_otp_email(){
        random = new Random();
        otp  = 1000 + random.nextInt(9000);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: " + user_id)); // Only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ruparanjan612@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "OTP Verification");
        intent.putExtra(Intent.EXTRA_TEXT, "Your OTP verification code is: " + Integer.toString(otp));

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d("mail veify","Inside resolve");
            startActivity(intent);
        }
    }

    private void set_Dialog(){
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.custom_dialogue_send_otp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);

        dBVerifyOTP = dialog.findViewById(R.id.verify_otp);
        dBCancel = dialog.findViewById(R.id.send_otp_cancel);
        otp_1 = dialog.findViewById(R.id.send_otp_et1);
        otp_2 = dialog.findViewById(R.id.send_otp_et2);
        otp_3 = dialog.findViewById(R.id.send_otp_et3);
        otp_4 = dialog.findViewById(R.id.send_otp_et4);
        set_number = dialog.findViewById(R.id.otp_tv_number);

        dBCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dBVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp_received = otp_1.getText().toString() + otp_2.getText().toString() + otp_3.getText().toString() + otp_4.getText().toString();
                String otp_send = Integer.toString(otp);
                if(otp_send.equals(otp_received)){
                    openHome();
                }
            }
        });
    }

    private void showError(EditText view, String error) {
        view.setError(error);
        view.requestFocus();
    }

    private boolean validate_Phoneno(String number){
        Pattern p = Pattern.compile("[6-9][0-9]{9}");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    private void openSignUp() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Error","In activity result");
        if(requestCode == 100){
            Log.d("Error","Inside activity result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("Error","In activity result");
            handleSignInResult(task);
            Log.d("Error","Exit activity result");

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Toast.makeText(this,"Login sucessfull " + account.getDisplayName(),Toast.LENGTH_LONG).show();
            Log.d("Error","Login Successfull " + account);
            openHome();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            Log.d("Error","Login Failed");
            Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
        }
    }

    private void startOAuthFlow() {
        Log.d("Github"," Enter StartOAuth Flow");
        String url = "https://github.com/login/oauth/authorize"
                + "?client_id=" + CLIENT_ID_Github
                + "&redirect_uri=" + REDIRECT_URI_Github
                + "&scope=read:user,user:email";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /*protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("Github"," Enter On New Intent");
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI_Github)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // Exchange code for access token
                getAccessToken(code);
                Log.d("Github"," Exit On new Intent");
            }
        }
    }*/

    private void getAccessToken(String code) {
        Log.d("Github"," Enter Get access token");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("client_id", CLIENT_ID_Github)
                .add("client_secret", CLIENT_SECRET_Github)
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI_Github)
                .build();

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(requestBody)
                .header("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    AccessTokenResponse tokenResponse = gson.fromJson(responseBody, AccessTokenResponse.class);
                    String accessToken = tokenResponse.access_token;
                    // Use the access token to access the GitHub API
                }
            }
        });
        Log.d("Github"," Exit Get access token");
    }

    class AccessTokenResponse {
        String access_token;
        String scope;
        String token_type;


    }


    private void fetchGitHubUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Parse and use user data
                }
            }
        });

    }

    protected void onResume() {
        super.onResume();
        /*Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI_Github)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                getAccessToken_LinkeIn(code);
            }
        }*/

        Log.d("Github"," On resume called");
        Uri uri = getIntent().getData();
        if (uri != null ) {
            if (uri.toString().startsWith(REDIRECT_URI_Github)) {
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    // Exchange code for access token
                    getAccessToken(code);
                    Log.d("Github", " Code founded");
                } else {
                    Log.d("Github", " Code not found");
                }
            } else {
                Log.d("Github", " URI not found");
            }
        }
    }

    private void getAccessToken_LinkeIn(String code) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(TOKEN_URL + "?grant_type=authorization_code&code=" + code
                        + "&redirect_uri=" + REDIRECT_URI_Github
                        + "&client_id=" + CLIENT_ID_LinkedIn
                        + "&client_secret=" + CLIENT_SECRET_LinkedIn)
                .post(RequestBody.create(null, new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String accessToken = jsonObject.getString("access_token");
                        fetchUserProfile(accessToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchUserProfile(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(PROFILE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String firstName = jsonObject.getJSONObject("localizedFirstName").getString("en_US");
                        String lastName = jsonObject.getJSONObject("localizedLastName").getString("en_US");
                        String profilePicture = jsonObject.getJSONObject("profilePicture")
                                .getJSONObject("displayImage~")
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONArray("identifiers")
                                .getJSONObject(0)
                                .getString("identifier");

                        runOnUiThread(() -> {
                            // Update UI with profile data
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




}