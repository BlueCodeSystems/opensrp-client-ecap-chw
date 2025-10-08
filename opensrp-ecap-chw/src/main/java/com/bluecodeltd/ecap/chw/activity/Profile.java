package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

public class Profile extends AppCompatActivity {

    private TextView txtName, txtCode, txtProvince, txtDistrict, txtFacility, txtPartner, txtNrc, txtPhone, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
                upArrow.setColorFilter(getResources().getColor(org.smartregister.R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
            TextView tvTitle = findViewById(R.id.tvTitle);
            if (tvTitle != null) tvTitle.setText("ECAP II Caseworker Profile");
        }

        applyLightStatusBar();

        txtName = findViewById(R.id.name);
        txtCode = findViewById(R.id.code);
        txtProvince = findViewById(R.id.province);
        txtDistrict = findViewById(R.id.district);
        txtFacility = findViewById(R.id.facility);
        txtPartner = findViewById(R.id.partner);
        txtNrc = findViewById(R.id.nrc);
        txtPhone = findViewById(R.id.phone);
        txtEmail = findViewById(R.id.email);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Profile.this);
        String code = sp.getString("code", "anonymous");
        String name = sp.getString("caseworker_name", "anonymous");
        String province = sp.getString("province", "anonymous");
        String partner = sp.getString("partner", "anonymous");
        String phone = sp.getString("phone", "anonymous");
        String district = sp.getString("district", "anonymous");
        String facility = sp.getString("facility", "anonymous");
        String email = sp.getString("email", "anonymous");
        String nrc = sp.getString("nrc", "anonymous");

        txtName.setText(name);
        txtCode.setText(code);
        txtProvince.setText(province);
        txtDistrict.setText(district);
        txtFacility.setText(facility);
        txtPartner.setText(partner);
        txtNrc.setText(nrc);
        txtPhone.setText(phone);
        txtEmail.setText(email);

    }

    // Status bar padding no longer required now that toolbar occupies the top.

    private void applyLightStatusBar() {
        Window window = getWindow();
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
}
