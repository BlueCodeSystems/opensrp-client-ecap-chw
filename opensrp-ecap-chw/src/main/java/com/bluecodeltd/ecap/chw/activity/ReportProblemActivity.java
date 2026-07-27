package com.bluecodeltd.ecap.chw.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.smartregister.util.PermissionUtils;

import com.bluecodeltd.ecap.chw.util.StatusBarUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ReportProblemActivity extends AppCompatActivity {

    private static final String[] CATEGORIES = {
            "Device / App issue",
            "Stockout",
            "Data / Sync issue",
            "Client safety concern",
            "Other"
    };

    private TextView textFacility;
    private TextView textCaseworker;
    private Spinner spinnerCategory;
    private EditText editDescription;
    private ImageView imagePreview;
    private View btnSubmit;
    private ProgressBar progressSubmitting;

    private String facilityName;
    private String caseworkerName;

    private Uri photoUri;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> choosePhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        StatusBarUtils.applyLightStatusBar(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Report a Problem");
        }

        textFacility = findViewById(R.id.text_facility);
        textCaseworker = findViewById(R.id.text_caseworker);
        spinnerCategory = findViewById(R.id.spinner_category);
        editDescription = findViewById(R.id.edit_description);
        imagePreview = findViewById(R.id.image_preview);
        btnSubmit = findViewById(R.id.btn_submit);
        progressSubmitting = findViewById(R.id.progress_submitting);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        facilityName = sp.getString("facility", "Unknown");
        caseworkerName = sp.getString("caseworker_name", "Unknown");
        textFacility.setText(facilityName);
        textCaseworker.setText(caseworkerName);

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES));

        takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success && photoUri != null) {
                showPhotoPreview(photoUri);
            }
        });

        choosePhotoLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                photoUri = uri;
                showPhotoPreview(uri);
            }
        });

        findViewById(R.id.btn_take_photo).setOnClickListener(v -> launchCamera());
        findViewById(R.id.btn_choose_photo).setOnClickListener(v -> choosePhotoLauncher.launch("image/*"));
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void launchCamera() {
        if (!PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "report_problem_photo");
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (photoUri != null) {
            takePhotoLauncher.launch(photoUri);
        }
    }

    private void showPhotoPreview(Uri uri) {
        imagePreview.setVisibility(View.VISIBLE);
        imagePreview.setImageURI(uri);
    }

    private void submitReport() {
        String description = editDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            editDescription.setError("Please describe the problem");
            return;
        }

        String category = (String) spinnerCategory.getSelectedItem();

        setSubmitting(true);

        Map<String, Object> report = new HashMap<>();
        report.put("category", category);
        report.put("description", description);
        report.put("timestamp", FieldValue.serverTimestamp());
        report.put("reportedBy", caseworkerName);
        report.put("facility", facilityName);
        report.put("appVersionName", BuildConfig.VERSION_NAME);
        report.put("appVersionCode", getAppVersionCode());
        report.put("deviceModel", Build.MODEL);
        report.put("androidSdkInt", Build.VERSION.SDK_INT);

        FirebaseFirestore.getInstance()
                .collection("issue_reports")
                .add(report)
                .addOnSuccessListener(documentReference -> {
                    if (photoUri != null) {
                        uploadPhoto(documentReference.getId(), photoUri);
                    } else {
                        onSubmitSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Timber.e(e);
                    setSubmitting(false);
                    Toast.makeText(this, "Could not submit report. It will retry once you're online.", Toast.LENGTH_LONG).show();
                });
    }

    private void uploadPhoto(String documentId, Uri uri) {
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference()
                .child("issue_reports")
                .child(documentId)
                .child("photo.jpg");

        photoRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUrl -> {
                            FirebaseFirestore.getInstance()
                                    .collection("issue_reports")
                                    .document(documentId)
                                    .update("photoUrl", downloadUrl.toString());
                            onSubmitSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Timber.e(e);
                            onSubmitSuccess();
                        }))
                .addOnFailureListener(e -> {
                    Timber.e(e);
                    onSubmitSuccess();
                });
    }

    private void onSubmitSuccess() {
        setSubmitting(false);
        Toast.makeText(this, "Thank you, your report has been submitted.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void setSubmitting(boolean submitting) {
        btnSubmit.setEnabled(!submitting);
        btnSubmit.setAlpha(submitting ? 0.5f : 1f);
        progressSubmitting.setVisibility(submitting ? View.VISIBLE : View.GONE);
    }

    private long getAppVersionCode() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            return -1;
        }
    }
}
