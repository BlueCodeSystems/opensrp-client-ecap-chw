package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.SopDocumentsAdapter;
import com.bluecodeltd.ecap.chw.model.SopDocumentModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class SopDocumentsActivity extends AppCompatActivity {

    public static final String EXTRA_RELATIVE_PATH = "relativePath";
    public static final String EXTRA_TITLE = "title";
    public static final String DEFAULT_SOPS_DIR = "ECAP II SOPs";

    private RecyclerView recyclerView;
    private View emptyState;
    private View progress;
    private SopDocumentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sop_documents);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String title = getIntent() != null ? getIntent().getStringExtra(EXTRA_TITLE) : null;
            getSupportActionBar().setTitle(title != null ? title : "ECAP II SOPs");
        }

        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.empty_state);
        progress = findViewById(R.id.progress_loading);

        adapter = new SopDocumentsAdapter(this, doc -> {
            Intent i = new Intent(SopDocumentsActivity.this, SopPdfViewerActivity.class);
            i.putExtra(SopPdfViewerActivity.EXTRA_URI, doc.getUri().toString());
            i.putExtra(SopPdfViewerActivity.EXTRA_TITLE, doc.getDisplayName());
            startActivity(i);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        loadDocs();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadDocs() {
        if (progress != null) progress.setVisibility(View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);

        String relativePath = getIntent() != null ? getIntent().getStringExtra(EXTRA_RELATIVE_PATH) : null;
        if (relativePath == null) {
            relativePath = Environment.DIRECTORY_DOWNLOADS + "/" + DEFAULT_SOPS_DIR + "/";
        }

        final String finalRelativePath = relativePath;
        Threading.ioBestEffort(() -> {
            List<SopDocumentModel> docs = queryDownloads(finalRelativePath);
            Threading.main(() -> {
                if (progress != null) progress.setVisibility(View.GONE);
                adapter.setItems(docs);
                boolean empty = docs == null || docs.isEmpty();
                if (emptyState != null) emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
            });
        });
    }

    private List<SopDocumentModel> queryDownloads(String relativePath) {
        List<SopDocumentModel> res = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] projection = new String[]{
                    MediaStore.Downloads._ID,
                    MediaStore.Downloads.DISPLAY_NAME,
                    MediaStore.Downloads.SIZE,
                    MediaStore.Downloads.DATE_MODIFIED,
                    MediaStore.Downloads.RELATIVE_PATH
            };
            String selection = MediaStore.Downloads.RELATIVE_PATH + "=? AND " + MediaStore.Downloads.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = new String[]{relativePath, "%.pdf"};
            cursor = getContentResolver().query(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    MediaStore.Downloads.DISPLAY_NAME + " COLLATE NOCASE ASC"
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    long size = cursor.isNull(2) ? 0 : cursor.getLong(2);
                    long dateModified = cursor.isNull(3) ? 0 : cursor.getLong(3);
                    Uri uri = Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, String.valueOf(id));
                    res.add(new SopDocumentModel(uri, name, size, dateModified));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            try {
                if (cursor != null) cursor.close();
            } catch (Exception ignored) {
            }
        }
        return res;
    }
}

