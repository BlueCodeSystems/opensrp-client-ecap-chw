package com.bluecodeltd.ecap.chw.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.issue.DiagnosticsCollector;
import com.bluecodeltd.ecap.chw.issue.IssuePhotoProvider;
import com.bluecodeltd.ecap.chw.issue.IssuePhotoUploadWorker;
import com.bluecodeltd.ecap.chw.issue.IssueReportStore;
import com.bluecodeltd.ecap.chw.util.StatusBarUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Lets a user report a problem with the app or in the field. The report becomes a document in the
 * Firestore {@code issue_reports} collection, with an optional photo in Firebase Storage.
 *
 * <p>Works offline: the document is written to Firestore's local cache, which sends it when the
 * phone is back online, and the photo waits in app storage for {@link IssuePhotoUploadWorker}.
 * The screen never waits on the network.</p>
 */
public class ReportProblemActivity extends AppCompatActivity {
    private static final String EXTRA_SCREEN = "screen";
    private static final String STATE_PHOTO = "photo";
    private static final String COLLECTION = "issue_reports";
    private static final int MIN_DESCRIPTION_CHARS = 10;

    private ChipGroup chipGroupCategory;
    private TextView textCategoryError;
    private MaterialButtonToggleGroup toggleSeverity;
    private TextInputLayout inputDescription;
    private TextInputEditText editDescription;
    private View photoContainer;
    private View photoButtons;
    private ImageView imagePreview;
    private SwitchMaterial switchDiagnostics;
    private MaterialButton btnSubmit;
    private ProgressBar progressSubmitting;

    private String facilityName;
    private String caseworkerName;
    private String screen;

    /** The photo to attach, already scaled down, in the private cache. */
    @Nullable
    private File photo;
    private File cameraOutput;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> choosePhotoLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    /** Opens the form, recording which screen the user reported from. */
    public static void start(Activity from) {
        from.startActivity(new Intent(from, ReportProblemActivity.class)
                .putExtra(EXTRA_SCREEN, from.getClass().getSimpleName()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        StatusBarUtils.applyLightStatusBar(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.report_problem);
        }

        chipGroupCategory = findViewById(R.id.chip_group_category);
        textCategoryError = findViewById(R.id.text_category_error);
        toggleSeverity = findViewById(R.id.toggle_severity);
        inputDescription = findViewById(R.id.input_description);
        editDescription = findViewById(R.id.edit_description);
        photoContainer = findViewById(R.id.photo_container);
        photoButtons = findViewById(R.id.photo_buttons);
        imagePreview = findViewById(R.id.image_preview);
        switchDiagnostics = findViewById(R.id.switch_diagnostics);
        btnSubmit = findViewById(R.id.btn_submit);
        progressSubmitting = findViewById(R.id.progress_submitting);

        screen = getIntent().getStringExtra(EXTRA_SCREEN);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String unknown = getString(R.string.report_problem_unknown);
        facilityName = sp.getString("facility", unknown);
        caseworkerName = sp.getString("caseworker_name", unknown);
        ((TextView) findViewById(R.id.text_facility)).setText(facilityName);
        ((TextView) findViewById(R.id.text_caseworker)).setText(caseworkerName);

        cameraOutput = new File(new File(getCacheDir(), "issue_photos"), "camera.jpg");
        takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), taken -> {
            if (taken) {
                usePhoto(Uri.fromFile(cameraOutput));
            }
        });
        choosePhotoLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                usePhoto(uri);
            }
        });
        cameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                launchCamera();
            } else {
                Toast.makeText(this, R.string.report_problem_camera_denied, Toast.LENGTH_LONG).show();
            }
        });

        chipGroupCategory.setOnCheckedStateChangeListener((group, ids) -> textCategoryError.setVisibility(View.GONE));
        findViewById(R.id.btn_take_photo).setOnClickListener(v -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA));
        findViewById(R.id.btn_choose_photo).setOnClickListener(v -> choosePhotoLauncher.launch("image/*"));
        findViewById(R.id.btn_remove_photo).setOnClickListener(v -> showPhoto(null));
        findViewById(R.id.btn_see_details).setOnClickListener(v -> showDiagnostics());
        btnSubmit.setOnClickListener(v -> submitReport());

        if (savedInstanceState != null && savedInstanceState.getString(STATE_PHOTO) != null) {
            File saved = new File(savedInstanceState.getString(STATE_PHOTO));
            showPhoto(saved.isFile() ? saved : null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photo != null) {
            outState.putString(STATE_PHOTO, photo.getPath());
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void launchCamera() {
        File dir = cameraOutput.getParentFile();
        if (dir != null && !dir.isDirectory() && !dir.mkdirs()) {
            Timber.w("ReportProblemActivity: could not create %s", dir);
        }
        takePhotoLauncher.launch(FileProvider.getUriForFile(this, getPackageName() + IssuePhotoProvider.AUTHORITY_SUFFIX, cameraOutput));
    }

    /** Scales the picked or captured image into a private file off the main thread, then shows it. */
    private void usePhoto(Uri source) {
        File out = new File(cameraOutput.getParentFile(), UUID.randomUUID() + ".jpg");
        executor.execute(() -> {
            File prepared = null;
            try {
                IssueReportStore.preparePhoto(getContentResolver(), source, out);
                prepared = out;
            } catch (Exception | OutOfMemoryError e) {
                Timber.e(e, "ReportProblemActivity: could not prepare photo");
            }
            //noinspection ResultOfMethodCallIgnored
            cameraOutput.delete();
            File result = prepared;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (result == null) {
                    Toast.makeText(this, R.string.report_problem_photo_failed, Toast.LENGTH_LONG).show();
                } else {
                    showPhoto(result);
                }
            });
        });
    }

    private void showPhoto(@Nullable File file) {
        if (photo != null && !photo.equals(file)) {
            //noinspection ResultOfMethodCallIgnored
            photo.delete();
        }
        photo = file;
        photoContainer.setVisibility(file != null ? View.VISIBLE : View.GONE);
        photoButtons.setVisibility(file != null ? View.GONE : View.VISIBLE);
        imagePreview.setImageURI(null);
        if (file != null) {
            imagePreview.setImageURI(Uri.fromFile(file));
        }
    }

    private void showDiagnostics() {
        executor.execute(() -> {
            Map<String, Object> diagnostics = DiagnosticsCollector.collect(getApplicationContext(), screen);
            String logs = DiagnosticsCollector.recentLogs();
            StringBuilder text = new StringBuilder();
            for (Map.Entry<String, Object> e : diagnostics.entrySet()) {
                text.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
            }
            int logLines = logs.isEmpty() ? 0 : logs.split("\n").length;
            text.append('\n').append(getString(R.string.report_problem_details_logs, logLines));
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.report_problem_details_title)
                        .setMessage(text.toString())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            });
        });
    }

    private void submitReport() {
        int categoryId = chipGroupCategory.getCheckedChipId();
        String description = editDescription.getText() == null ? "" : editDescription.getText().toString().trim();

        boolean valid = true;
        if (categoryId == View.NO_ID) {
            textCategoryError.setVisibility(View.VISIBLE);
            valid = false;
        }
        if (description.length() < MIN_DESCRIPTION_CHARS) {
            inputDescription.setError(getString(R.string.report_problem_description_required));
            valid = false;
        } else {
            inputDescription.setError(null);
        }
        if (!valid) {
            return;
        }

        String category = ((TextView) findViewById(categoryId)).getText().toString();
        String severity = severity(toggleSeverity.getCheckedButtonId());
        boolean includeDiagnostics = switchDiagnostics.isChecked();
        File attachedPhoto = photo;
        setSubmitting(true);

        executor.execute(() -> {
            String reportId = FirebaseFirestore.getInstance().collection(COLLECTION).document().getId();

            Map<String, Object> report = new HashMap<>();
            report.put("reportId", reportId);
            report.put("status", "new");
            report.put("category", category);
            report.put("severity", severity);
            report.put("description", description);
            report.put("reportedBy", caseworkerName);
            report.put("facility", facilityName);
            report.put("timestamp", FieldValue.serverTimestamp());
            report.put("clientCreatedAt", System.currentTimeMillis());
            // Always sent: support needs these to reproduce anything.
            Map<String, Object> diagnostics = DiagnosticsCollector.collect(getApplicationContext(), screen);
            for (String key : new String[]{"appVersionName", "appVersionCode", "deviceModel", "androidSdkInt", "screen", "providerId"}) {
                report.put(key, diagnostics.get(key));
            }
            if (includeDiagnostics) {
                report.putAll(diagnostics);
                report.put("logs", DiagnosticsCollector.recentLogs());
            }

            boolean saved = true;
            if (attachedPhoto != null) {
                try {
                    IssueReportStore.queuePhoto(getApplicationContext(), reportId, attachedPhoto);
                    report.put("photoPath", IssueReportStore.storagePath(reportId));
                } catch (Exception e) {
                    Timber.e(e, "ReportProblemActivity: could not queue photo");
                    saved = false;
                }
            }

            if (saved) {
                // The write lands in Firestore's local cache now and syncs when online; its task
                // only completes on server ack, so don't hold the screen open waiting for it.
                FirebaseFirestore.getInstance().collection(COLLECTION).document(reportId).set(report)
                        .addOnFailureListener(e -> Timber.e(e, "ReportProblemActivity: report %s rejected", reportId));
                if (report.containsKey("photoPath")) {
                    IssuePhotoUploadWorker.enqueue(getApplicationContext());
                }
            }

            boolean ok = saved;
            boolean online = DiagnosticsCollector.isOnline(getApplicationContext());
            runOnUiThread(() -> {
                if (ok) {
                    photo = null; // now owned by the upload queue
                    Toast.makeText(getApplicationContext(), online
                            ? R.string.report_problem_sent_online
                            : R.string.report_problem_sent_offline, Toast.LENGTH_LONG).show();
                    finish();
                } else if (!isFinishing() && !isDestroyed()) {
                    setSubmitting(false);
                    Toast.makeText(this, R.string.report_problem_save_failed, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private static String severity(int checkedButtonId) {
        if (checkedButtonId == R.id.btn_severity_blocking) {
            return "blocking";
        }
        if (checkedButtonId == R.id.btn_severity_minor) {
            return "minor";
        }
        return "major";
    }

    private void setSubmitting(boolean submitting) {
        btnSubmit.setEnabled(!submitting);
        btnSubmit.setText(submitting ? R.string.report_problem_sending : R.string.report_problem_send);
        progressSubmitting.setVisibility(submitting ? View.VISIBLE : View.GONE);
    }
}
