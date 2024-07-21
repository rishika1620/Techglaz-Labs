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

public class Certificate_verificationFragment extends Fragment {

    TextInputEditText certificate_id;
    Button verify_certificate;
    Button download_certifiacte;
    Button cancel_dialog;
    Button upload_certificate;

    TextView verification_status;
    TextView status_msg;

    Dialog dialog;

    MongoDB_Database databse;

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
      //  upload_certificate = view.findViewById(R.id.certificate_upload_btn);

        databse = new MongoDB_Database(getContext());
        databse.setupDatabase();

        verify_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certificate_Id = certificate_id.getText().toString();

                if(certificate_Id.isEmpty()){
                    certificate_id.setError("Please enter Certificate Id");
                    certificate_id.requestFocus();
                }
                else{
                    verify_method(certificate_Id);
                }
            }
        });

       /* Code to upload Certificate by admin
       upload_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databse.uploadPDF();
                Log.d("Certificate","upload button clicked");
            }
        });*/

        return view;
    }

    private void setUpDialog(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_download_certificate);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        download_certifiacte = dialog.findViewById(R.id.certificate_download_btn);
        cancel_dialog =dialog.findViewById(R.id.certificate_cancel_dialog);
        status_msg = dialog.findViewById(R.id.certificate_message);
        verification_status = dialog.findViewById(R.id.certificate_status);


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
                dialog.dismiss();
            }
        });
    }

    public void setFailedDialog(){

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_download_certificate);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        download_certifiacte = dialog.findViewById(R.id.certificate_download_btn);
        cancel_dialog =dialog.findViewById(R.id.certificate_cancel_dialog);
        status_msg = dialog.findViewById(R.id.certificate_message);
        verification_status = dialog.findViewById(R.id.certificate_status);

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

        status_msg.setText("We can't find any certificate from the given ID");
        verification_status.setText("Failed!");
        download_certifiacte.setVisibility(View.INVISIBLE);
        cancel_dialog.setText("back");
    }

    public void verify_method(String id) {
        databse.verifyPDF(id, new MongoDB_Database.DBCallback() {
            @Override
            public void onSuccess() {
                setUpDialog();
                dialog.show();
                Log.d("PDF","PDF verified");
            }

            @Override
            public void onError(String errorMessage) {
                setFailedDialog();
                dialog.show();
                Log.d("PDF","PDF not verified" + errorMessage);
            }
        });
    }

}