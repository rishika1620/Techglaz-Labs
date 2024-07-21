package com.example.radha.techglaz;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermisssionGranted = false;
    private  boolean isWritePermissionGranted = false;
    private boolean ispostNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> o) {
                Log.d("MainActivity","Inside permission laucher");
                if(o.get(Manifest.permission.READ_MEDIA_IMAGES) != null){
                    isReadPermisssionGranted = o.get(Manifest.permission.READ_MEDIA_IMAGES);
                }
                if(o.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null){
                    isReadPermisssionGranted = o.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if(o.get(Manifest.permission.POST_NOTIFICATIONS) != null){
                    ispostNotification = o.get(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        });

        requestPermission();

        loadFragment(new HmeFragment(),1);
        navigationView.setClickable(true);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.nav_home){
                    loadFragment(new HmeFragment(),0);
                    //Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_certificate){
                    loadFragment(new Certificate_verificationFragment(),0);
                   // Toast.makeText(MainActivity.this, "Certificate selected", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_contact_us){
                    loadFragment(new ContactFragment(),0);
                    //Toast.makeText(MainActivity.this, "Contact selected", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_about){
                    loadFragment(new AboutFragment(),0);
                   // Toast.makeText(MainActivity.this, "About selected", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_policy){
                    loadFragment(new PrivacyPolicyFragment(),0);
                   // Toast.makeText(MainActivity.this, "Privacy selected", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_logout){
                    MongoDB_Database database = new MongoDB_Database(getApplicationContext());
                    database.setupDatabase();

                    SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",MODE_PRIVATE);
                    String email = sharedPreferences.getString("email",null);
                    database.logout(email, new MongoDB_Database.isLoggedCallback() {
                        @Override
                        public void onSuccess(Boolean isLogged) {
                            if(!isLogged){
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("Main Activity",error);
                        }
                    });
                }
                else{
                    loadFragment(new HmeFragment(),0);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });

        if (getIntent().hasExtra("pdfUri")) {
            String pdfUriString = getIntent().getStringExtra("pdfUri");
            Uri pdfUri = Uri.parse(pdfUriString);

            OpenPDFFragment fragment = OpenPDFFragment.newInstance(pdfUri);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment, int falg) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(falg == 1){
            ft.add(R.id.frame,fragment);
        }
        else{
            ft.replace(R.id.frame,fragment);
        }
        ft.commit();
    }

    private void requestPermission(){
        Log.d("MainActivity","Inside requestPermission");
        isReadPermisssionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        ispostNotification = ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        List<String> permissionRequest = new ArrayList<String>();

        if(!isReadPermisssionGranted){
            permissionRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
        }

        if(!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!ispostNotification){
            permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        if(!permissionRequest.isEmpty()){
            Log.d("MainActivity","Request initiated");
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }
}