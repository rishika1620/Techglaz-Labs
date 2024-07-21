package com.example.radha.techglaz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

public class PDF_Certificate {
    Context context;

    public PDF_Certificate(Context context) {
        this.context = context;
    }

    public void Create_PDF(FileOutputStream outputStream) throws IOException {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_certificate_layout, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(displayMetrics);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        int newwidth = view.getMeasuredWidth();
        int newheight = view.getMeasuredHeight();

        Log.d("PDF", "Inside Creating PDF");

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(newwidth, newheight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        Log.d("PDF", "Saving pdf");
        pdfDocument.writeTo(outputStream);
        pdfDocument.close();
        outputStream.close();

        Log.d("PDF", "Uploading PDF");
        // Upload the PDF to MongoDB
        MongoDB_Database database = new MongoDB_Database(context.getApplicationContext());
        database.setupDatabase();
        // database.uploadPDF(byteArrayOutputStream.toByteArray());
    }

    public void savePdfToStorage(Context context) {
        String pdfName = "example.pdf";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, pdfName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/MyApp");

        Log.d("PDF", "Inside savePdfToStorage");
        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
        showPdfCreationNotification(uri);
        if (uri != null) {
            try (FileOutputStream outputStream = (FileOutputStream) context.getContentResolver().openOutputStream(uri)) {
                Create_PDF(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PDF Creation Channel";
            String description = "Channel for PDF creation notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("PDF_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showPdfCreationNotification(Uri pdfUri) {
        createNotificationChannel();

        /*Intent intent = new Intent(context, MainActivity.class);
        intent.setData(pdfUri);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "PDF_CHANNEL_ID")
                .setSmallIcon(R.drawable.techglaz_logo) // Replace with your app icon
                .setContentTitle("Certificate Download")
                .setContentText("Downloading Certificate")
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Show the notification
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }

        // Simulate PDF creation process
        new Thread(() -> {
            try {
                // Simulate time-consuming task
                Thread.sleep(5000);

                // Update the notification
                builder.setContentText("Tap to view Certificate ")
                        .setOngoing(false)
                        .setAutoCancel(true)
                        //.setContentIntent(pendingIntent)
                        .setProgress(0,0,false);
                notificationManager.notify(1, builder.build());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


}