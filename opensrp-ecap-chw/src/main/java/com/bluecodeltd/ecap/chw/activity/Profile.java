package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        applyStatusBarPadding();
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

    private void applyStatusBarPadding() {
        View root = findViewById(R.id.profile_scroll);
        if (root == null) {
            return;
        }

        int statusBarHeight = getStatusBarHeight();
        if (statusBarHeight > 0) {
            root.setPadding(root.getPaddingLeft(), root.getPaddingTop() + statusBarHeight,
                    root.getPaddingRight(), root.getPaddingBottom());
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

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
