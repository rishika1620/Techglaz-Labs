package com.example.radha.techglaz;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class OpenPDFFragment extends Fragment {

    private static final String ARG_PDF_URI = "pdf_uri";

    ImageView pdfImage;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page pdfPage;

    public static OpenPDFFragment newInstance(Uri pdfUri) {
        OpenPDFFragment fragment = new OpenPDFFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PDF_URI, pdfUri);
        fragment.setArguments(args);
        return fragment;
    }
    public OpenPDFFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_open_p_d_f, container, false);
        pdfImage= view.findViewById(R.id.pdfView);

        Uri pdfUri = getArguments() != null ? getArguments().getParcelable(ARG_PDF_URI) : null;
        if (pdfUri != null) {
            try {
                Log.d("OpenPDF","Opening PDF");
                openPdf(pdfUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    private void openPdf(Uri uri) throws IOException {
        ParcelFileDescriptor fileDescriptor = getContext().getContentResolver().openFileDescriptor(uri, "r");
        pdfRenderer = new PdfRenderer(fileDescriptor);

        Log.d("OpenPDF","Indode function Openpdf");
        if (pdfRenderer.getPageCount() > 0) {
            pdfPage = pdfRenderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(pdfPage.getWidth(), pdfPage.getHeight(), Bitmap.Config.ARGB_8888);
            pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdfImage.setImageBitmap(bitmap);
            pdfPage.close();
        }

        pdfRenderer.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdfPage != null) {
            pdfPage.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}

