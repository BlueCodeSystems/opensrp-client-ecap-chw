package com.bluecodeltd.ecap.chw.fragment;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_household_overview, container, false);

        TextView housetitle = view.findViewById(R.id.overview_section_header);
        TextView txtIncome = view.findViewById(R.id.income);
        TextView txtIncomeSource = view.findViewById(R.id.income_source);
        TextView txtBeds = view.findViewById(R.id.beds);
        TextView txtMalaria = view.findViewById(R.id.malaria);
        TextView txtMales5 = view.findViewById(R.id.males_less_5);
        TextView txtFemales5 = view.findViewById(R.id.females_less_5);
        TextView txtMales10 = view.findViewById(R.id.males_10);
        TextView txtFemales10 = view.findViewById(R.id.females_10);
        LinearLayout linearLayout = view.findViewById(R.id.llayout);
        Button screenBtn = view.findViewById(R.id.screenBtn);

        FloatingActionButton fab = getActivity().findViewById(R.id.fabx);

        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();

        Household house = mymap.get("house");
        String is_screened = house.getScreened();

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

        return view;

    }
}
