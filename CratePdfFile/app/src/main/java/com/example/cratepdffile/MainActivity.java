package com.example.cratepdffile;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.barcodes.Barcode1D;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static com.itextpdf.kernel.pdf.PdfName.BaseFont;


public class MainActivity extends AppCompatActivity {


    Calendar calender;
    SimpleDateFormat simpleDateFormat;
    EditText nameEdt, numberplateEdt, explanationEdt, brandEdt;
    String nameStr, numberPlateStr, brandStr, listPdf, date;
    Button crateBtn, addBtn;
    ListView listView;
    String explanation;
    ArrayList<String> list;

    ArrayAdapter<String> arrayAdapter;

    private static final int PERMISSION_REQUEST_CODE = 200;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nameEdt = findViewById(R.id.nameEdt);
        numberplateEdt = findViewById(R.id.numberplateEdt);
        explanationEdt = findViewById(R.id.explanationEdt);
        crateBtn = findViewById(R.id.cratesBtn);
        brandEdt = findViewById(R.id.brandEdt);
        listView = findViewById(R.id.listView);
        addBtn = findViewById(R.id.addBtn);
        calender = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        date = simpleDateFormat.format(calender.getTime());


        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();

        }


        list = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explanation = explanationEdt.getText().toString();
                list.add(explanation);
                listView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                explanationEdt.setText(" ");
                listPdf = list.toString();

            }
        });

        crateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameStr = nameEdt.getText().toString();
                numberPlateStr = numberplateEdt.getText().toString();
                brandStr = brandEdt.getText().toString();

                try {
                    cratePdf();
                    //showPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                }


            }
        });
    }

    private void cratePdf() throws IOException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Document.pdf");
        OutputStream outputStream = new FileOutputStream(file);



        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);
        document.setMargins(0, 0, 0, 0);

        Drawable d = getDrawable(R.drawable.pdfrepair);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);


        Paragraph pdfTitle = new Paragraph("Ad Soyad:" + nameStr).setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
        Paragraph pdfNubmerPlate = new Paragraph("Plaka:" + " " + numberPlateStr).setTextAlignment(TextAlignment.CENTER).setFontSize(12);
        Paragraph pdfBrand = new Paragraph("Marka:" + " " + brandStr).setTextAlignment(TextAlignment.CENTER).setFontSize(12);
        Paragraph pdfDate = new Paragraph("Tarih:" + "  " + date).setTextAlignment(TextAlignment.RIGHT).setFontSize(10);
        Paragraph problem = new Paragraph("Araç Promblemleri").setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER);

        float[] width = {500f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        for (String list1 : list) {

            table.addCell(list1);


        }

        BarcodeQRCode qrCode = new BarcodeQRCode(nameStr + "\n" + numberPlateStr + "\n" + date);
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        Image qrCodeImg = new Image(qrCodeObject).setWidth(120).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.add(pdfDate);
        document.add(pdfTitle);
        document.add(pdfBrand);
        document.add(pdfNubmerPlate);
        document.add(problem);
        document.add(table);
        document.add(qrCodeImg);


        document.close();
        Toast.makeText(this, "Pdf Dosyası Başarıyla Oluşturuldu", Toast.LENGTH_LONG).show();
        nameEdt.setText(" ");
        numberplateEdt.setText(" ");
        brandEdt.setText(" ");
        list.clear();


    }


    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


}




