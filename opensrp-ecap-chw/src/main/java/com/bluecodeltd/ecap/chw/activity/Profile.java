package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

public class Profile extends AppCompatActivity {

    private TextView txtName, txtCode, txtProvince, txtDistrict, txtFacility, txtPartner, txtNrc, txtPhone, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
}