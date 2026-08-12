package com.bluecodeltd.ecap.chw.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.json.JSONArray;
import org.json.JSONException;

public class VcaServiceReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";

    private String baseEntityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vca_service_report_view);

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        applyLightStatusBar();
        findViewById(R.id.report_view_back_button).setOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();
    }

    private void applyLightStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);
        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    private void loadReport() {
        if (baseEntityId == null || baseEntityId.trim().isEmpty()) {
            return;
        }
        Threading.io(() -> {
            VCAServiceModel model = null;
            try { model = VCAServiceReportDao.getServiceReportByEntityId(baseEntityId); } catch (Exception ignored) {}
            VCAServiceModel finalModel = model;
            Threading.main(() -> {
                if (finalModel != null) {
                    populateData(finalModel);
                }
            });
        });
    }

    private void populateData(VCAServiceModel model) {
        setText(R.id.txt_date, model.getDate());
        setText(R.id.txt_location, model.getVca_service_location());

        setText(R.id.txt_services, formatMultiSelect(model.getServices()));
        setText(R.id.txt_other_service, model.getOther_service());

        setText(R.id.txt_school_services, formatMultiSelect(model.getSchooled_services()));
        setText(R.id.txt_other_school_services, model.getOther_schooled_services());

        setText(R.id.txt_safe_services, formatMultiSelect(model.getSafe_services()));
        setText(R.id.txt_other_safe_services, model.getOther_safe_services());

        setText(R.id.txt_stable_services, formatMultiSelect(model.getStable_services()));
        setText(R.id.txt_other_stable_services, model.getOther_stable_services());

        boolean isHivPositive = model.getIs_hiv_positive() != null && !model.getIs_hiv_positive().trim().isEmpty();
        View hivCard = findViewById(R.id.hiv_card);
        if (hivCard != null) {
            hivCard.setVisibility(isHivPositive ? View.VISIBLE : View.GONE);
        }
        setText(R.id.txt_hiv_status, capitalize(model.getIs_hiv_positive()));
        setText(R.id.txt_pregnant_breastfeeding, capitalize(model.getPregnant_breastfeeding()));
        setText(R.id.txt_art_clinic, model.getArt_clinic());
        setText(R.id.txt_date_last_vl, model.getDate_last_vl());
        setText(R.id.txt_vl_last_result, model.getVl_last_result());
        setText(R.id.txt_date_next_vl, model.getDate_next_vl());
        setText(R.id.txt_child_mmd, capitalize(model.getChild_mmd()));
        setText(R.id.txt_level_mmd, model.getLevel_mmd());

        showSignature(model.getSignature());
    }

    private void showSignature(String encodedSignature) {
        ImageView signatureView = findViewById(R.id.img_signature);
        TextView noSignature = findViewById(R.id.txt_no_signature);
        if (encodedSignature == null || encodedSignature.trim().isEmpty()) {
            signatureView.setVisibility(View.GONE);
            noSignature.setVisibility(View.VISIBLE);
            return;
        }
        try {
            byte[] decodedBytes = Base64.decode(encodedSignature, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (bitmap != null) {
                signatureView.setImageBitmap(bitmap);
                signatureView.setVisibility(View.VISIBLE);
                noSignature.setVisibility(View.GONE);
            } else {
                signatureView.setVisibility(View.GONE);
                noSignature.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            signatureView.setVisibility(View.GONE);
            noSignature.setVisibility(View.VISIBLE);
        }
    }

    private void setText(int viewId, String value) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(value == null || value.trim().isEmpty() ? getString(R.string.not_available) : value.trim());
        }
    }

    private String capitalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }

    private String formatMultiSelect(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            JSONArray array = new JSONArray(raw);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < array.length(); i++) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(array.optString(i));
            }
            return builder.length() == 0 ? null : builder.toString();
        } catch (JSONException e) {
            return raw.trim();
        }
    }
}
