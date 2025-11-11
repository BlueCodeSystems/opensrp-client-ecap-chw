package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.IndexMotherModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rey.material.widget.Button;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HouseholdOverviewFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentHouseholdOverviewBinding binding;


    TextView housetitle, txtIncome, txtIncomeSource, txtBeds, txtGpsLocation,txtMalaria, txtMalesLessThanFive, txtFemales, txtNumber, txtName,txtPhone, txtDate,txtEdited_by,txtMalesBetweenTenAndSeventeen,txtDateStartedArt, txtVlLastDate, txtVlResult, txtRecentVLResult, txtIsSuppressed, txtNextVl, txtIsMMD,txtRecentMMD, txtOnART, txtArtNumber, txtLevelMMD, txtSubpopulation;
    LinearLayout linearLayout, muacView;
    Button screenBtn;
    ImageButton arrowButton;
    FloatingActionButton fab;
    Household house;
    CaregiverAssessmentModel caregiverAssessmentModel;
    String nutritionWarning;
    ImageView signatureIV;
    private TextView txtFemalesBetweenTenAndSeventeen;
    private TextView txtFemalesLessThanFive;
    RelativeLayout relativeLayout;
    LinearLayout layout;
    LinearLayout layoutSubpopulation;
    private String householdId;
    private IndexMotherModel indexMotherModel;


    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentHouseholdOverviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        housetitle = binding.overviewSectionHeader;
        txtIncome = binding.income;
        txtIncomeSource = binding.incomeSource;
        txtBeds = binding.beds;
        // txtMalaria not used currently
        txtMalesLessThanFive = binding.males;
        txtFemalesLessThanFive = binding.females;
        txtName = binding.emergencyName;
        txtNumber = binding.emergencyNumber;
        txtPhone = binding.hPhone;
        txtDate  = binding.hDate;
        signatureIV = binding.signatureView;
        txtEdited_by = binding.hEditedBy;
        txtMalesBetweenTenAndSeventeen = binding.malesBetweenTenAndSeventeen;
        txtFemalesBetweenTenAndSeventeen = binding.femalesBetweenTenAndSeventeen;
        linearLayout = binding.llayout;
        muacView = binding.muacWarning;
        screenBtn = binding.screenBtn;

        txtOnART = binding.isArt;
        txtArtNumber = binding.artNumber;
        txtDateStartedArt = binding.artDate;
        txtVlLastDate = binding.dateLastVl;
        txtVlResult = binding.lastVlResult;
        txtRecentVLResult = binding.recentVlResult;
        txtIsSuppressed = binding.vlSuppressed;
        txtNextVl = binding.nextVlTest;
        txtIsMMD = binding.onMmd;
        txtLevelMMD = binding.mmdLevel;
        txtRecentMMD = binding.recentMmdLevel;

        relativeLayout = binding.myview;
        layout = binding.mylayout;
        arrowButton = binding.arrowButton;
        layoutSubpopulation = binding.layoutSubpopulation;

        fab = getActivity().findViewById(R.id.fabx);
        txtGpsLocation = binding.gpsLocation;
        txtSubpopulation = binding.txtSubpopulation;



        try {
            // Get the householdId from the parent activity
            HouseholdDetails parent = (HouseholdDetails) requireActivity();
            householdId = parent != null ? parent.householdId : null;
        } catch (Throwable ignored) {}

        if (householdId != null) {
            indexMotherModel = IndexMotherDao.getIndexMotherByHouseholdId(householdId);
        } else {
            indexMotherModel = null;
        }

        setViews();

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setImageViewFromBase64(String base64Str, ImageView imageView) {
        // Decode Base64 encoded string to byte array
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);

        // Convert byte array to Bitmap
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        // Set the Bitmap to ImageView
        imageView.setImageBitmap(decodedBitmap);
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
        String encodedSignature = house.getSignature();

        if(encodedSignature!= null && encodedSignature != "") {
            setImageViewFromBase64(encodedSignature, signatureIV);
        }

        // Show/Hide Subpopulation row based on pregnancy, breastfeeding, or mother_age_range
        try {
            if (layoutSubpopulation != null) {
                boolean show = false;
                if (indexMotherModel != null) {
                    String isPregnant = indexMotherModel.getPregnant_mother();
                    String isBreastfeeding = indexMotherModel.getMother_breastfeeding();
                    String motherAgeRange = indexMotherModel.getMother_age_range();
                    boolean pregnantYes = isPregnant != null && isPregnant.equalsIgnoreCase("yes");
                    boolean breastfeedingYes = isBreastfeeding != null && isBreastfeeding.equalsIgnoreCase("yes");
                    boolean ageYes = motherAgeRange != null && motherAgeRange.equalsIgnoreCase("yes");
                    show = pregnantYes || breastfeedingYes || ageYes;
                }
                layoutSubpopulation.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception ignored) {}

        // Subpopulation indicator
        try {
            if (indexMotherModel != null) {
                String hivStatus = indexMotherModel.getCaregiver_hiv_status();
                String isPregnant = indexMotherModel.getPregnant_mother();
                String isBreastfeeding = indexMotherModel.getMother_breastfeeding();
                String motherAgeRange = indexMotherModel.getMother_age_range();

                boolean hivPositive = hivStatus != null && hivStatus.equalsIgnoreCase("positive");
                boolean hivNegative = hivStatus != null && hivStatus.equalsIgnoreCase("negative");
                boolean pregnantYes = isPregnant != null && isPregnant.equalsIgnoreCase("yes");
                boolean breastfeedingYes = isBreastfeeding != null && isBreastfeeding.equalsIgnoreCase("yes");
                boolean motherAgeYes = motherAgeRange != null && motherAgeRange.equalsIgnoreCase("yes");

                if (hivPositive && pregnantYes && breastfeedingYes) {
                    txtSubpopulation.setText("HIV-positive Pregnant mother/HIV-positive Pregnant/Breastfeeding Women (P/BFW)");
                } else if (hivPositive && pregnantYes && !breastfeedingYes) {
                    txtSubpopulation.setText("HIV-positive Pregnant mother");
                } else if (hivPositive && !pregnantYes && breastfeedingYes) {
                    txtSubpopulation.setText("HIV-positive Pregnant/Breastfeeding Women (P/BFW)");
                } else if (hivNegative && motherAgeYes) {
                    txtSubpopulation.setText("HIV-negative PBFW");
                }
            }
        } catch (Exception ignored) {}
        txtIncome.setText(income);
        txtBeds.setText(beds);
        txtIncomeSource.setText(incomeSource);
        //txtMalaria.setText(household_member_had_malaria);


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

        txtVlResult.setText(house.getViral_load_results() != null ? house.getViral_load_results() : "Not Set");


        List<HouseholdServiceReportModel> sModel = HouseholdServiceReportDao.getRecentVLServicesByHousehold(house.getHousehold_id());

        String viralLoadResult = null;

        if (!sModel.isEmpty()) {
            HouseholdServiceReportModel serviceM= sModel.get(0);
            viralLoadResult = serviceM.getVl_last_result();
        } else {
            viralLoadResult = house.getViral_load_results();
        }

        try {
            if (viralLoadResult != null) {
                int intValue = Integer.parseInt(viralLoadResult);
                txtIsSuppressed.setText(intValue <= 1000 ? "Yes" : "No");
            } else {
                txtIsSuppressed.setText("Not set");
            }
        } catch (NumberFormatException e) {
            txtIsSuppressed.setText("Update VL Results");
        }


//        txtIsSuppressed.setText(house.getVl_suppressed() != null ? house.getVl_suppressed() : "Not Set");

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
            if (serviceModel.getDate_next_vl() != null) {
                txtNextVl.setText(serviceModel.getDate_next_vl());
            } else {
                txtNextVl.setText(house.getDate_next_vl() != null ? house.getDate_next_vl() : "Not Set");
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
            txtNextVl.setText(house.getDate_next_vl() != null ? house.getDate_next_vl() : "Not Set");


        }

        txtGpsLocation.setText(house.getHousehold_location() != null ? formatGpsCoordinates(house.getHousehold_location()) : "Not Set");
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

    // Public method to refresh the UI from Activity without re-adding the fragment
    public void refreshData() {
        if (!isAdded()) return;
        // Ensure view/binding is available
        if (binding == null) return;
        try {
            setViews();
        } catch (Exception ignored) { }
    }

    public static String formatGpsCoordinates(String location) {

        String pattern = "(-?\\d+\\.\\d+)\\s+(-?\\d+\\.\\d+)\\s+(\\d+\\.\\d+)\\s+(\\d+\\.\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(location);
        if (m.find()) {
            String latitude = m.group(1);
            String longitude = m.group(2);
            String altitude = m.group(3);
            String accuracy = m.group(4);
            return "Latitude: " + latitude + "\nLongitude: " + longitude + "\nAltitude: " + altitude + "\nAccuracy: " + accuracy;
        } else {
            return "Invalid input format";
        }
    }
}
