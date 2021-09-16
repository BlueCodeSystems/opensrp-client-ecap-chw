package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;

import java.util.HashMap;

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

        HashMap<String, String> mymap = ( (IndexDetailsActivity) requireActivity()).getData();

        /* map.put("caregiver_sex", client.getColumnmaps().get("caregiver_sex"));
        map.put("caregiver_hiv_status", client.getColumnmaps().get("caregiver_hiv_status"));
        map.put("relation", client.getColumnmaps().get("relation"));
        map.put("caregiver_phone", client.getColumnmaps().get("caregiver_phone"));*/

        txtCaregiverName.setText(mymap.get("caregiver_firstname"));
        txtGender.setText(mymap.get("caregiver_sex"));
        txtDob.setText(mymap.get("caregiver_birth_date"));
        txtHiv.setText(mymap.get("caregiver_hiv_status"));
        txtRelation.setText(mymap.get("relation"));
        txtPhone.setText(mymap.get("caregiver_phone"));


        return view;

    }
}
