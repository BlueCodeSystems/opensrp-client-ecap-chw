package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.rey.material.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;


public class HouseholdOverviewFragment extends Fragment {


    TextView housetitle, txtIncome, txtIncomeSource, txtBeds, txtMalaria, txtMales,
            txtFemales, txtNumber, txtName;
    LinearLayout linearLayout;
    Button screenBtn;
    FloatingActionButton fab;
    Household house;
    CaregiverAssessmentModel caregiverAssessmentModel;


    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_household_overview, container, false);

        housetitle = view.findViewById(R.id.overview_section_header);
        txtIncome = view.findViewById(R.id.income);
        txtIncomeSource = view.findViewById(R.id.income_source);
        txtBeds = view.findViewById(R.id.beds);
        txtMalaria = view.findViewById(R.id.malaria);
        txtMales = view.findViewById(R.id.males);
        txtFemales = view.findViewById(R.id.females);
        txtName = view.findViewById(R.id.emergency_name);
        txtNumber = view.findViewById(R.id.emergency_number);

        linearLayout = view.findViewById(R.id.llayout);
        screenBtn = view.findViewById(R.id.screenBtn);

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

    }

    @SuppressLint("RestrictedApi")
    public void setViews(){

        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();
        HashMap<String, CaregiverAssessmentModel> vmap = ( (HouseholdDetails) requireActivity()).getVulnerabilities();

        String females = ( (HouseholdDetails) requireActivity()).countFemales;
        String males = ( (HouseholdDetails) requireActivity()).countMales;

        house = mymap.get("house");
        caregiverAssessmentModel = vmap.get("vulnerabilities");

        String is_screened = house.getScreened();
        String incomeSource = house.getFam_source_income();

        String income = "Not Set";

        if(caregiverAssessmentModel != null){
            income = caregiverAssessmentModel.getMonthly_expenses();
        }

        String beds = house.getBeds();
        String household_member_had_malaria = house.getHousehold_member_had_malaria();
        String emergency_name = house.getEmergency_name();
        String contact_number = house.getContact_number();

        txtIncome.setText(income);
        txtBeds.setText(beds);
        txtIncomeSource.setText(incomeSource);
        txtMalaria.setText(household_member_had_malaria);
        txtMales.setText(males);
        txtFemales.setText(females);
        txtMales.setText(males);
        if(emergency_name != null)
        {
            txtName.setText(emergency_name);
        }
        if(contact_number != null)
        {
            txtNumber.setText(contact_number);
        }


        if(is_screened != null && is_screened.equals("true")){

            screenBtn.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            housetitle.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        } else if (is_screened == null) {

            screenBtn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            housetitle.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

        }
    }
}
