package com.example.radha.techglaz;

import android.content.ContentValues;
import android.content.Context;
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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

public class PDF_Certificate {
    Context context;

   public PDF_Certificate(Context context){
        this.context = context;
   }

    public void Create_PDF(FileOutputStream outputStream) throws IOException {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_certificate_layout,null);
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            context.getDisplay().getRealMetrics(displayMetrics);
        }
       // else context.getgetDefaultDisplay().getMetrics(displayMetrics);

        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels,View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels,View.MeasureSpec.EXACTLY));

        view.layout(0,0,displayMetrics.widthPixels,displayMetrics.heightPixels);

        int newwidth = view.getMeasuredWidth();
        int newheight = view.getMeasuredHeight();

        Log.d("PDF","Inside Creating PDF");

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(newwidth, newheight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

       /* Log.d("PDF","Converting to Byte Stream");
        // Write the PDF to a byte array output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            pdfDocument.writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the document
        pdfDocument.close();
        Log.d("PDF","Closing Byte Stream");
        byteArrayOutputStream.close();*/

        Log.d("PDF","Saving pdf");
         //Save PDF to file system (optional)
       // File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "example.pdf");
      //  outputStream.write(byteArrayOutputStream.toByteArray());
        pdfDocument.writeTo(outputStream);
        pdfDocument.close();
        outputStream.close();

        Log.d("PDF","Uploading PDF");
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

        Log.d("PDF","Inside savePdfToStorage");
        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
        if (uri != null) {
            try (FileOutputStream outputStream = (FileOutputStream) context.getContentResolver().openOutputStream(uri)) {
                Create_PDF(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static void createPdf(OutputStream outputStream, byte[] data) {
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.add(new Paragraph(new String(data))); // Assuming the binary data is text
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}