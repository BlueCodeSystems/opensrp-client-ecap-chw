package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;

import java.util.HashMap;

public class HouseholdOverviewFragment extends Fragment {

    TextView txtProvince, txtDistrict, txtWard, txtVillage, txtPartners, txtHealth_facility, txtScreening_location, txtScreening_date,
            txtViral_load, txtViral_date, txtCaseworker_first_name, txtCaseworker_last_name,
            txtCaregiver_hiv_status, txtActive_on_treatment, txtCaregiver_art, txtSuppressed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hh_fragment_overview, container, false);
        txtProvince = view.findViewById(R.id.hh_province);
        txtDistrict = view.findViewById(R.id.hh_district);
        txtWard = view.findViewById(R.id.hh_ward);
        txtVillage = view.findViewById(R.id.hh_village);
        txtHealth_facility = view.findViewById(R.id.hh_health_facility);
        txtPartners = view.findViewById(R.id.hh_partners);
        txtScreening_date = view.findViewById(R.id.hh_screening_date);
        txtScreening_location = view.findViewById(R.id.hh_screening_location);


        txtCaregiver_hiv_status = view.findViewById(R.id.hh_caregiver_hiv_status);
        txtActive_on_treatment = view.findViewById(R.id.hh_active_on_treatment);
        txtCaregiver_art = view.findViewById(R.id.hh_caregiver_art);
        txtSuppressed = view.findViewById(R.id.hh_caregiver_virally_suppressed);
        txtViral_load = view.findViewById(R.id.hh_viral_load);
        txtViral_date = view.findViewById(R.id.hh_viral_date);

        txtCaseworker_first_name = view.findViewById(R.id.hh_caseworker_name1);
        txtCaseworker_last_name = view.findViewById(R.id.hh_caseworker_name2);


        HashMap<String, String> mymap = ((HouseholdDetails) requireActivity()).getData();

        txtProvince.setText(mymap.get("province"));
        txtDistrict.setText(mymap.get("district"));
        txtVillage.setText(mymap.get("ward"));
        txtWard.setText(mymap.get("adolescent_village"));
        txtHealth_facility.setText(mymap.get("health_facility"));
        txtPartners.setText(mymap.get("partners"));
        txtScreening_date.setText(mymap.get("screening_date"));
        txtScreening_location.setText(mymap.get("screening_location"));

        //Caregiver details
        txtCaregiver_hiv_status.setText(mymap.get("caregiver_hiv_status"));
        txtActive_on_treatment.setText(mymap.get("active_on_treatment"));
        txtCaregiver_art.setText(mymap.get("caregiver_art_number"));
        txtSuppressed.setText(mymap.get("is_caregiver_virally_suppressed"));
        txtViral_load.setText(mymap.get("viral_load_results"));
        txtViral_date.setText(mymap.get("date_of_last_viral_load"));

        //Case worker details
        txtCaseworker_first_name.setText(mymap.get("fname_community_case_worker"));
        txtCaseworker_last_name.setText(mymap.get("lname_community_case_worker"));


        return view;
    }
}
