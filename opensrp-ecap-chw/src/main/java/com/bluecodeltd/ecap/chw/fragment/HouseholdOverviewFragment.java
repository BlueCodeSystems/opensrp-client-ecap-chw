package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rey.material.widget.Button;

import java.util.HashMap;
import java.util.List;


public class HouseholdOverviewFragment extends Fragment {


    TextView housetitle, txtIncome, txtIncomeSource, txtBeds, txtMalaria, txtMalesLessThanFive, txtFemales, txtNumber, txtName,txtPhone, txtDate,txtEdited_by,txtMalesBetweenTenAndSeventeen,txtDateStartedArt, txtVlLastDate, txtVlResult, txtRecentVLResult, txtIsSuppressed, txtNextVl, txtIsMMD,txtRecentMMD, txtOnART, txtArtNumber, txtLevelMMD;
    LinearLayout linearLayout, muacView;
    Button screenBtn;
    ImageButton arrowButton;
    FloatingActionButton fab;
    Household house;
    CaregiverAssessmentModel caregiverAssessmentModel;
    String nutritionWarning;
    private TextView txtFemalesBetweenTenAndSeventeen;
    private TextView txtFemalesLessThanFive;
    RelativeLayout relativeLayout;
    LinearLayout layout;

    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_household_overview, container, false);

        housetitle = view.findViewById(R.id.overview_section_header);
        txtIncome = view.findViewById(R.id.income);
        txtIncomeSource = view.findViewById(R.id.income_source);
        txtBeds = view.findViewById(R.id.beds);
        txtMalaria = view.findViewById(R.id.malaria);
        txtMalesLessThanFive = view.findViewById(R.id.males);
        txtFemalesLessThanFive = view.findViewById(R.id.females);
        //txtFemales = view.findViewById(R.id.females);
        txtName = view.findViewById(R.id.emergency_name);
        txtNumber = view.findViewById(R.id.emergency_number);
        txtPhone = view.findViewById(R.id.h_phone);
        txtDate  = view.findViewById(R.id.h_date);
        txtEdited_by = view.findViewById(R.id.h_edited_by);
        txtMalesBetweenTenAndSeventeen = view.findViewById(R.id.malesBetweenTenAndSeventeen);
        txtFemalesBetweenTenAndSeventeen = view.findViewById(R.id.femalesBetweenTenAndSeventeen);
        linearLayout = view.findViewById(R.id.llayout);
        muacView = view.findViewById(R.id.muac_warning);
        screenBtn = view.findViewById(R.id.screenBtn);

        txtOnART = view.findViewById(R.id.is_art);
        txtArtNumber = view.findViewById(R.id.art_number);
        txtDateStartedArt = view.findViewById(R.id.art_date);
        txtVlLastDate = view.findViewById(R.id.date_last_vl);
        txtVlResult = view.findViewById(R.id.last_vl_result);
        txtRecentVLResult = view.findViewById(R.id.recent_vl_result);
        txtIsSuppressed = view.findViewById(R.id.vl_suppressed);
        txtNextVl = view.findViewById(R.id.next_vl_test);
        txtIsMMD = view.findViewById(R.id.on_mmd);
        txtLevelMMD = view.findViewById(R.id.mmd_level);
        txtRecentMMD = view.findViewById(R.id.recent_mmd_level);

        relativeLayout = view.findViewById(R.id.myview);
        layout = view.findViewById(R.id.mylayout);
        arrowButton = view.findViewById(R.id.arrow_button);



        fab = getActivity().findViewById(R.id.fabx);

        setViews();

        return view;

    }

    @SuppressLint("RestrictedApi")
    public void setViews(){

        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();
        HashMap<String, CaregiverAssessmentModel> vmap = ( (HouseholdDetails) requireActivity()).getVulnerabilities();

        String females = ( (HouseholdDetails) requireActivity()).countFemales;
        String lessThanFiveMales = ( (HouseholdDetails) requireActivity()).lessThanFiveMales;
        String betweenTenAndSevenTeen= ( (HouseholdDetails) requireActivity()).malesBetweenTenAndSevenTeen;
        String lessThanFiveFemales = ( (HouseholdDetails) requireActivity()).lessThanFiveFemales;
        String femalesBetweenTenAndSevenTeen= ( (HouseholdDetails) requireActivity()).FemalesBetweenTenAndSevenTeen;

        house = mymap.get("house");
        caregiverAssessmentModel = vmap.get("vulnerabilities");

       if (caregiverAssessmentModel != null){

           nutritionWarning = caregiverAssessmentModel.getHousehold_eaten_month();

       }


        if(nutritionWarning != null && (nutritionWarning.equals("sometimes") || nutritionWarning.equals("warning"))){

            muacView.setVisibility(View.VISIBLE);

        } else {

            muacView.setVisibility(View.GONE);

        }

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

        String phone = house.getPhone();
        String date_edited = house.getDate_edited();
        String edited_by = house.getCaseworker_name();

        txtIncome.setText(income);
        txtBeds.setText(beds);
        txtIncomeSource.setText(incomeSource);
        txtMalaria.setText(household_member_had_malaria);


        if(lessThanFiveMales != null)
        {
            txtMalesLessThanFive.setText(lessThanFiveMales);
        }

        if(lessThanFiveFemales != null)
        {
            txtFemalesLessThanFive.setText(lessThanFiveFemales);
        }


       if(betweenTenAndSevenTeen != null) {
           txtMalesBetweenTenAndSeventeen.setText(betweenTenAndSevenTeen);
       }

        if(femalesBetweenTenAndSevenTeen != null) {
            txtFemalesBetweenTenAndSeventeen.setText(femalesBetweenTenAndSevenTeen);
        }

        if(emergency_name != null)
        {
            txtName.setText(emergency_name);
        }
        if(contact_number != null)
        {
            txtNumber.setText(contact_number);
        }

        if(phone != null)
        {
            txtPhone.setText(phone);
        }
        if(date_edited != null)
        {
            txtDate.setText(date_edited);
        }
        if(edited_by != null)
        {
            txtEdited_by.setText(edited_by);
        }
        if(house.getCaregiver_hiv_status() != null && house.getCaregiver_hiv_status().equals("negative")){
         relativeLayout.setVisibility(View.GONE);
         layout.setVisibility(View.GONE);
        }
        arrowButton.setOnClickListener(view -> {
            layout.setVisibility(View.VISIBLE);
        });

        txtOnART.setText(house.getActive_on_treatment() != null ? house.getActive_on_treatment() : "Not Set");
        txtArtNumber.setText(house.getCaregiver_art_number() != null ? house.getCaregiver_art_number() : "Not Set");
        txtDateStartedArt.setText(house.getDate_started_art() != null ? house.getDate_started_art() : "Not Set");
        txtVlLastDate.setText(house.getDate_of_last_viral_load() != null ? house.getDate_of_last_viral_load() : "Not Set");
        txtNextVl.setText(house.getDate_next_vl() != null ? house.getDate_next_vl() : "Not Set");
        txtVlResult.setText(house.getViral_load_results() != null ? house.getViral_load_results() : "Not Set");
        txtIsSuppressed.setText(house.getVl_suppressed() != null ? house.getVl_suppressed() : "Not Set");
        txtIsMMD.setText(house.getCaregiver_mmd() != null ? house.getCaregiver_mmd() : "Not Set");
        txtLevelMMD.setText(house.getLevel_mmd() != null ? house.getLevel_mmd() : "Not Set");


        List<HouseholdServiceReportModel> serviceModels = HouseholdServiceReportDao.getRecentVLServicesByHousehold(house.getHousehold_id());
        if (!serviceModels.isEmpty()) {
            HouseholdServiceReportModel serviceModel = serviceModels.get(0);
            if (serviceModel.getLevel_mmd() != null) {
                txtRecentMMD.setText(serviceModel.getLevel_mmd());
            } else {
                if (house.getLevel_mmd() != null) {
                    txtRecentMMD.setText(house.getLevel_mmd());
                } else {
                    txtRecentMMD.setText("N/A");
                }
            }

            if (serviceModel.getVl_last_result() != null) {
                txtRecentVLResult.setText(serviceModel.getVl_last_result());
            } else {
                if (house.getViral_load_results() != null) {
                    txtRecentVLResult.setText(house.getViral_load_results());
                } else {
                    txtRecentVLResult.setText("N/A");
                }
            }

        }
        else {
            if (house.getLevel_mmd() != null) {
                txtRecentMMD.setText(house.getLevel_mmd());
            } else {
                txtRecentMMD.setText("N/A");
            }


            if (house.getViral_load_results() != null) {
                txtRecentVLResult.setText(house.getViral_load_results());
            } else {
                txtRecentVLResult.setText("N/A");
            }


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
