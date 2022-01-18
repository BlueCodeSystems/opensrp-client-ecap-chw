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
import com.bluecodeltd.ecap.chw.model.Household;
import com.rey.material.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;


public class HouseholdOverviewFragment extends Fragment {


    TextView housetitle, txtIncome, txtIncomeSource, txtBeds, txtMalaria, txtMales5,
            txtFemales5, txtMales10, txtFemales10, txtNumber, txtName;
    LinearLayout linearLayout;
    Button screenBtn;
    FloatingActionButton fab;
    Household house;


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
        txtMales5 = view.findViewById(R.id.males_less_5);
        txtFemales5 = view.findViewById(R.id.females_less_5);
        txtMales10 = view.findViewById(R.id.males_10);
        txtFemales10 = view.findViewById(R.id.females_10);
        txtName = view.findViewById(R.id.emergency_name);
        txtNumber = view.findViewById(R.id.emergency_number);

        linearLayout = view.findViewById(R.id.llayout);
        screenBtn = view.findViewById(R.id.screenBtn);

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

    }

    public void setViews(){
        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();

        house = mymap.get("house");

        String is_screened = house.getScreened();
        String incomeSource = house.getFam_source_income();
        String iscome = house.getIncome();
        String beds = house.getBeds();
        //String malaria = house.getMalaria();
        String male5 = house.getMales_less_5();
        String female5 = house.getFemales_less_5();
        String male10 = house.getMales_10_17();
        String female10 = house.getFemales_10_17();
        String emergency_name = house.getEmergency_name();
        String contact_number = house.getContact_number();

        txtIncome.setText(iscome);
        txtBeds.setText(beds);
        txtIncomeSource.setText(incomeSource);
        txtMales5.setText(male5);
        txtFemales5.setText(female5);
        txtMales10.setText(male10);
        txtFemales10.setText(female10);
        txtName.setText(emergency_name);
        txtNumber.setText(contact_number);

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
