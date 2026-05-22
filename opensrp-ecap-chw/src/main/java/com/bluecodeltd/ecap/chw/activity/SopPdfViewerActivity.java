package com.bluecodeltd.ecap.chw.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import timber.log.Timber;

public class SopPdfViewerActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_TITLE = "title";

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sop_pdf_viewer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = getIntent() != null ? getIntent().getStringExtra(EXTRA_TITLE) : null;
            getSupportActionBar().setTitle(title != null ? title : "Document");
        }

        pdfView = findViewById(R.id.pdfView);

        String uriStr = getIntent() != null ? getIntent().getStringExtra(EXTRA_URI) : null;
        if (uriStr == null) {
            finish();
            return;
        }

        try {
            Uri uri = Uri.parse(uriStr);
            pdfView.fromUri(uri)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .spacing(0)
                    .load();
        } catch (Exception e) {
            Timber.e(e);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        try {
            if (pdfView != null) pdfView.recycle();
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }
}

