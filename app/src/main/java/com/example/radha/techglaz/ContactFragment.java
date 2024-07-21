package com.example.radha.techglaz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ContactFragment extends Fragment {

    private static final String Linked_package = "com.linkedin.android";
    private static final String Linked_profile_url = "linkedin.com://techglaz";
    private static final String Linked_web_url = "https://www.linkedin.com/company/techglaz/?originalSubdomain=in";

    private static final String Facebook_package = "com.facebook";
    private static final String Facebook_profile_url = "fb://techglaz";
    private static final String Facebook_web_url = "https://www.facebook.com/techglaz";

    private static final String Instagram_package = "com.instagram.android";
    private static final String Instagram_profile_url = "https://instagram.com/_u/techglaz";
    private static final String Instagram_web_url = "https://www.instagram.com/techglaz";

    private static final String Website_url = "https://techglaz.com/";
    private static final String location_url = "https://www.google.com/maps/place/Techglaz+Labs+private+limited/@25.2504429,87.0386817,15z/data=!4m14!1m7!3m6!1s0x39f04770364cfedd:0x1e07ef5eb62a831a!2sTechglaz+Labs+private+limited!8m2!3d25.2505593!4d87.0370938!16s%2Fg%2F11j_0n5ny_!3m5!1s0x39f04770364cfedd:0x1e07ef5eb62a831a!8m2!3d25.2505593!4d87.0370938!16s%2Fg%2F11j_0n5ny_?entry=ttu";

    EditText senderName,senderEmail,senderMessage;
    Button sendMsg;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ImageView linkedIn_img = view.findViewById(R.id.contact_linkedIN);
        ImageView instagram_img =view.findViewById(R.id.contact_instagram);
        ImageView facebook_img = view.findViewById(R.id.contact_facebook);
        ImageView website_img = view.findViewById(R.id.contact_website);
        senderName = view.findViewById(R.id.sender_name);
        senderEmail = view.findViewById(R.id.sender_email);
        senderMessage = view.findViewById(R.id.sender_msg);
        sendMsg = view.findViewById(R.id.send_msg_btn);

        ImageView location_img = view.findViewById(R.id.contactUs_location);

        linkedIn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkedIn(Linked_profile_url,Linked_web_url);
            }
        });

        instagram_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkedIn(Instagram_profile_url,Instagram_web_url);
            }
        });

        facebook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkedIn(Facebook_profile_url,Facebook_web_url);
            }
        });

        website_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Website_url));
                startActivity(intent);
            }
        });

        location_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        return view;
    }

    private void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(location_url));
        startActivity(intent);
    }

    private void openLinkedIn(String profile_url, String web_url) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(profile_url));
            startActivity(intent);
        } catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(web_url));
            startActivity(intent);
        }
    }

    private void sendEmail() {
        String name = senderName.getText().toString().trim();
        String email = senderEmail.getText().toString().trim();
        String content = senderMessage.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || content.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = "Message from " + name;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"techglazlabs@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
    }


}