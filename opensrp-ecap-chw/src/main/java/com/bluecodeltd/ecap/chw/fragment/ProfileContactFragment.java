package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.widget.Toast;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.HashMap;

import timber.log.Timber;

public class ProfileContactFragment extends Fragment {

    TextView txtCaregiverName, txtGender, txtDob, txtHiv, txtRelation, txtPhone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        txtCaregiverName= view.findViewById(R.id.caregiver_name);
        txtGender = view.findViewById(R.id.caregiver_gender);
        txtDob= view.findViewById(R.id.caregiver_dob);
        txtHiv = view.findViewById(R.id.hiv_status);
        txtRelation = view.findViewById(R.id.child_relation);
        txtPhone = view.findViewById(R.id.caregiver_phone);

        HashMap<String, Child> childHashMap = ((IndexDetailsActivity) requireActivity()).getData();
        Child child = childHashMap != null ? childHashMap.get("Child") : null;

        if (child == null) {
            Timber.w("ProfileContactFragment: missing child data, using placeholder values");
            Toast.makeText(requireContext(), "Member data incomplete", Toast.LENGTH_LONG).show();
            applyFallbackValues();
            return view;
        }

        txtCaregiverName.setText(valueOrDefault(child.getCaregiver_name()));
        txtGender.setText(valueOrDefault(child.getCaregiver_sex()));
        txtDob.setText(valueOrDefault(child.getCaregiver_birth_date()));
        txtHiv.setText(valueOrDefault(child.getCaregiver_hiv_status()));
        txtRelation.setText(valueOrDefault(child.getRelation()));
        txtPhone.setText(valueOrDefault(child.getCaregiver_phone()));


        return view;

    }

    private String valueOrDefault(String value) {
        return TextUtils.isEmpty(value) ? "Not Set" : value;
    }

    private void applyFallbackValues() {
        txtCaregiverName.setText("Not Set");
        txtGender.setText("Not Set");
        txtDob.setText("Not Set");
        txtHiv.setText("Not Set");
        txtRelation.setText("Not Set");
        txtPhone.setText("Not Set");
    }
}
