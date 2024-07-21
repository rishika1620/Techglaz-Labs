package com.example.radha.techglaz;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPasswordActivity extends AppCompatActivity {


    Random random;
    int otp;
    EditText forgot_password_id;
    Button VerifyAccount;
    EditText password_edt;
    EditText confirmPassword_edt;
    Button changePassword;
    Button Back;
    TextView set_number;
    String id;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgot_password_id = findViewById(R.id.forgot_id);
        VerifyAccount = findViewById(R.id.Verify_account_btn);

        VerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_Dialog();
                dialog.show();
               // validateUser();
            }
        });
        /*sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = forgot_password_id.getText().toString();

                if (id.isEmpty()) {
                    forgot_password_id.setError("Field cannot be empty");
                } else if (id.contains("@gmail.com")) {
                    sendEmail("id","Otp Verification Code","Here is your otp to Verify your email\nYour Otp is: " + getOtp());
                    dialog.show();
                } else if (validate_Phoneno(id)) {
                    send_otp_phone();
                    dialog.show();
                }
            }
        });*/





    }

    public void validateUser(){
        MongoDB_Database database = new MongoDB_Database(getApplicationContext());
        database.setupDatabase();

        database.isAlreadyExists(id, new MongoDB_Database.DBCallback() {
            @Override
            public void onSuccess() {
                set_Dialog();
                dialog.show();
            }
            @Override
            public void onError(String errorMessage) {
                forgot_password_id.setError("No user exists with the account");
            }
        });
    }

    /*private boolean validate_Phoneno(String number) {
        Pattern p = Pattern.compile("[6-9][0-9]{9}");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    private void send_otp_phone() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String sms = "OTP is : " + getOtp();
            smsManager.sendTextMessage(id, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent to " + id, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String getOtp(){
        random = new Random();
        otp = 1000 + random.nextInt(9000);
        return Integer.toString(otp);
    }*/
    private void set_Dialog() {
        dialog = new Dialog(ForgotPasswordActivity.this);
        dialog.setContentView(R.layout.change_password);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);

        password_edt = dialog.findViewById(R.id.forgotpass_password);
        confirmPassword_edt = dialog.findViewById(R.id.forgotpass_confm_password);
        changePassword = dialog.findViewById(R.id.changePass_btn);
        Back = dialog.findViewById(R.id.back_btn);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String password = password_edt.getText().toString();
               String confirmPassword = confirmPassword_edt.getText().toString();
               id = forgot_password_id.getText().toString();

               if(password.isEmpty()){
                   password_edt.setError("Fill password");
               }
               else if(confirmPassword.isEmpty()){
                   confirmPassword_edt.setError("Can't be empty");
               }
               else if(confirmPassword.equals(password)){
                   MongoDB_Database database = new MongoDB_Database(getApplicationContext());
                   database.setupDatabase();
                   database.changePassword(id, password, new MongoDB_Database.DBCallback() {
                       @Override
                       public void onSuccess() {
                           Toast.makeText(getApplicationContext(),"Password Changed sucessfully",Toast.LENGTH_LONG).show();
                           dialog.dismiss();
                           Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                           startActivity(intent);
                       }

                       @Override
                       public void onError(String errorMessage) {
                           Toast.makeText(getApplicationContext(),"Failed to Change Password",Toast.LENGTH_LONG).show();
                       }
                   });
               }
               else{
                   confirmPassword_edt.setError("Password not matched");
               }
            }
        });
    }

    /*private void sendEmail(String recipientEmail, String subject, String messageBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String senderEmail = "your_email@gmail.com"; // your email
                String senderPassword = "your_password"; // your email password

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host
                props.put("mail.smtp.port", "587"); // SMTP port
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                               return new PasswordAuthentication(senderEmail,senderPassword);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(senderEmail));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    message.setSubject(subject);
                    message.setText(messageBody);

                    Transport.send(message);
                    Log.d("SendEmail", "Email sent successfully");

                } catch (MessagingException e) {
                    Log.e("SendEmail", "Error sending email", e);
                }

                return null;
            }
        }.execute();
    }*/
}