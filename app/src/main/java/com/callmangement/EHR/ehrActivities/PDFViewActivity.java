package com.callmangement.EHR.ehrActivities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.callmangement.R;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.IOException;

public class PDFViewActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        imageView = findViewById(R.id.pdfImageView);
        progressBar = findViewById(R.id.progressBar);

        String pdfPath = getIntent().getStringExtra("pdfPath");
        File file = new File(pdfPath);

        if (file.exists()) {
            renderPdf(file);
        }
    }

    private void renderPdf(File file) {
        new Thread(() -> {
            try {
                PDDocument document = PDDocument.load(file);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                // Render the first page as a Bitmap
                Bitmap bitmap = pdfRenderer.renderImageWithDPI(0, 300); // Adjust DPI as needed

                runOnUiThread(() -> {
                    imageView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                });

                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }
}