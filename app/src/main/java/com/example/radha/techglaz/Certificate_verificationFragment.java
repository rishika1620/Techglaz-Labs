package com.example.radha.techglaz;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileOutputStream;
import java.io.IOException;

public class Certificate_verificationFragment extends Fragment {

    TextInputEditText certificate_id;
    Button verify_certificate;
    Button download_certifiacte;
    Button cancel_dialog;
    Button upload_certificate;

    TextView verification_status;
    TextView status_msg;

    Dialog dialog;

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_CODE_READ = 100000;
    private static final int CHOOSE_PDF = 1000;
    private MongoDB_Database mongoDBDatabase;
    private String PDF_PATH;

    public Certificate_verificationFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_certificate_verification, container, false);
        certificate_id = view.findViewById(R.id.certificate_id_edt);
        verify_certificate = view.findViewById(R.id.certificate_verify_btn);
        upload_certificate = view.findViewById(R.id.certificate_upload_btn);

        verify_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    verify_method();
                    dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialogue_download_certificate);
                    dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.dialog_background));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    download_certifiacte = dialog.findViewById(R.id.certificate_download_btn);
                    cancel_dialog =dialog.findViewById(R.id.certificate_cancel_dialog);
                    status_msg = dialog.findViewById(R.id.certificate_message);
                    verification_status = dialog.findViewById(R.id.certificate_status);

                    if(verify_method()){
                        dialog.show();
                    }
                    else{
                        status_msg.setText("We can't find any certificate from the given ID");
                        verification_status.setText("Failed!");
                        download_certifiacte.setVisibility(View.INVISIBLE);
                        cancel_dialog.setText("back");
                        dialog.show();
                    }

                    cancel_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                   download_certifiacte.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                PDF_Certificate pdfCertificate = new PDF_Certificate(getContext());
                                pdfCertificate.savePdfToStorage(getContext());
                                Toast.makeText(getContext(),"Certificate Downloaded",Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });

        upload_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Certificate","upload button clicked");
              /*  try {
                    //FileOutputStream outputStream = new FileOutputStream();
                    //pdfCertificate.Create_PDF(outputStream);
                    Log.d("CV","PDF created");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }*/
            }
        });

        return view;
    }

    private boolean verify_method() {
        MongoDB_Database database = new MongoDB_Database(getContext());
        database.setupDatabase();
        database.verifyPDF("example.pdf",getContext());
        Log.d("PDF","PDF verified");
        return true;
    }

  /*  private void downloadPDF() {
        String fileName = "downloaded_file.pdf";
        mongoDBDatabase.downloadPDFById(certificate_id.getText().toString(), fileName);
    }*/

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permission","INside on requestpermission");
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //downloadPDF();
            } else {
                Log.e("Permission", "Write external storage permission denied");
            }
        }
        if (requestCode == REQUEST_CODE_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Certificate","Permission sucess to read file");
                callChooseFileFromDevice();
            } else {
                Log.e("Permission", "Read external storage permission denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_PDF && resultCode == Activity.RESULT_OK){
            if(data != null){
                Log.d("Error","onActivityResult: " + data.getData());
                PDF_PATH = data.getData().getPath().toString();
                Log.d("Path",PDF_PATH);
            }
        }
    }

    /*private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContext().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (path == null) {
            path = uri.getPath();
        }
        return path;
    }*/

    private void callChooseFileFromDevice(){
        Log.d("Certificate","Preparing to choose file");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent,"Select a file"),CHOOSE_PDF);
    }

}