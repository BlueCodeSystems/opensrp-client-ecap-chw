package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.AbymSubpopulationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.MuacDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao;
import com.bluecodeltd.ecap.chw.model.AbymSubpopulationModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.MuacModel;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfileOverviewFragment extends Fragment {

    RelativeLayout myview;
    Button moreSubpopBtn;
    LinearLayout myview2,linearlayout_name,linearlayout_gender,linearlayout_dob,linearlayout_status,linearlayout_relation,linearlayout_phone,subPopLayout1,subPopLayout2,abymSubpopulation;
    ImageButton imgBtn;
    TextView abymTxt,disabledTxt,agedTxt,illnessTxt,childHeadTxt,notChildHeadTx,femaleHeadedTxt,survivorTxt;
    TextView txtArtNumber, sub1, sub2, sub3, sub4, sub5, sub6,otherSub,otherMemberSub, txtSubPopulation,txtReferred, txtFacility,txtEditedBy,txtDateEdited,
            txtEnrolled, txtArtCheckbox, txtDateStartedArt, txtVlLastDate, txtVlResult, txtIsSuppressed, txtNextVl, txtIsMMD, txtMMDResult,
            txtCaregiverName, txtGender, txtDob, txtHiv, txtRelation, txtPhone,txtcPhone,txtSchool,recent_vl_result,recent_mmd_level,
            new_caregiver_name, overview_section_header3,overview_section_header5,overview_section_details_left, new_caregiver_gender, new_caregiver_dob, new_hiv_status, new_child_relation, new_caregiver_phone;

    AbymSubpopulationModel abym;

    LinearLayout abymSub,siblingSubPop;

    MuacModel muacModel;
    VcaScreeningModel childScreeningModel;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        txtEditedBy = view.findViewById(R.id.edited_by);
        txtDateEdited = view.findViewById(R.id.date_last_edited);
        txtcPhone = view.findViewById(R.id.cPhone);
        txtArtNumber = view.findViewById(R.id.art_number);
        myview2 = view.findViewById(R.id.mylayout);
        imgBtn = view.findViewById(R.id.arrow_button);
        sub1 = view.findViewById(R.id.subpop1);
        sub2 = view.findViewById(R.id.subpop2);
        sub3 = view.findViewById(R.id.subpop3);
        sub4 = view.findViewById(R.id.subpop4);
        sub5 = view.findViewById(R.id.subpop5);
        sub6 = view.findViewById(R.id.subpop6);
        abymSub = view.findViewById(R.id.abymSub);
        siblingSubPop = view.findViewById(R.id.siblingSubPop);
        txtSubPopulation = view.findViewById(R.id.sub_population);
        subPopLayout1 = view.findViewById(R.id.subPopLayout1);
        subPopLayout2 = view.findViewById(R.id.subPopLayout2);
        myview = view.findViewById(R.id.myview);
        txtReferred = view.findViewById(R.id.referred);
        txtEnrolled = view.findViewById(R.id.enrolled);
        txtArtCheckbox = view.findViewById(R.id.is_art);
        txtDateStartedArt = view.findViewById(R.id.art_date);
        txtVlLastDate = view.findViewById(R.id.date_last_vl);
        txtSchool = view.findViewById(R.id.school);

        txtVlResult = view.findViewById(R.id.last_vl_result);
        txtIsSuppressed = view.findViewById(R.id.vl_suppressed);
        txtNextVl = view.findViewById(R.id.next_vl_test);
        txtIsMMD = view.findViewById(R.id.on_mmd);
        txtFacility = view.findViewById(R.id.facility);
        txtMMDResult = view.findViewById(R.id.mmd_level);

        //Caregiver Views
        txtCaregiverName= view.findViewById(R.id.caregiver_name);
        txtGender = view.findViewById(R.id.caregiver_gender);
        txtDob= view.findViewById(R.id.caregiver_dob);
        txtHiv = view.findViewById(R.id.hiv_status);
        txtRelation = view.findViewById(R.id.child_relation);
        txtPhone = view.findViewById(R.id.caregiver_phone);
        recent_vl_result = view.findViewById(R.id.recent_vl_result);
        recent_mmd_level = view.findViewById(R.id.recent_mmd_level);

        new_caregiver_name = view.findViewById(R.id.new_caregiver_name);
        new_caregiver_gender = view.findViewById(R.id.new_caregiver_gender);
        new_caregiver_dob = view.findViewById(R.id.new_caregiver_dob);
        new_hiv_status = view.findViewById(R.id.new_hiv_status);
        new_child_relation = view.findViewById(R.id.new_child_relation);
        new_caregiver_phone = view.findViewById(R.id.new_caregiver_phone);
        overview_section_header3 = view.findViewById(R.id.overview_section_header3);
        overview_section_header5 = view.findViewById(R.id.overview_section_header5);


        linearlayout_name =  view.findViewById(R.id. linearlayout_name);
        linearlayout_gender = view.findViewById(R.id.linearlayout_gender);
        linearlayout_dob = view.findViewById(R.id.linearlayout_dob);
        linearlayout_status = view.findViewById(R.id.linearlayout_status);
        linearlayout_relation = view.findViewById(R.id.linearlayout_relation);
        linearlayout_phone = view.findViewById(R.id.linearlayout_phone);


        moreSubpopBtn = view.findViewById(R.id.morePopulation);





        HashMap<String, Child> mymap = ( (IndexDetailsActivity) requireActivity()).getData();
        Child childIndex =mymap.get("Child");

        HashMap<String, newCaregiverModel> caregiverDetails = ((IndexDetailsActivity) requireActivity()).getUpdatedCaregiverData();
        newCaregiverModel updateCaregiver = caregiverDetails.get("UpdatedCaregiver");

        new_caregiver_name.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_name() != null ? updateCaregiver.getNew_caregiver_name() : "Not Set");
        new_caregiver_gender.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_sex() != null ? updateCaregiver.getNew_caregiver_sex() : "Not Set");
        new_caregiver_dob.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_birth_date() != null ? updateCaregiver.getNew_caregiver_birth_date() : "Not Set");
        new_hiv_status.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_hiv_status() != null ? updateCaregiver.getNew_caregiver_hiv_status() : "Not Set");
        new_child_relation.setText(updateCaregiver != null && updateCaregiver.getNew_relation() != null ? updateCaregiver.getNew_relation() : "Not Set");
        new_caregiver_phone.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_phone() != null ? updateCaregiver.getNew_caregiver_phone() : "Not Set");

        try {
            if(updateCaregiver.getNew_caregiver_name() != null && !updateCaregiver.getNew_caregiver_name().isEmpty()){
                overview_section_header3.setText("Previous Caregiver Details");
                linearlayout_gender.setVisibility(View.VISIBLE);
                linearlayout_dob.setVisibility(View.VISIBLE);
                linearlayout_status.setVisibility(View.VISIBLE);
                linearlayout_relation.setVisibility(View.VISIBLE);
                linearlayout_phone.setVisibility(View.VISIBLE);
                linearlayout_name.setVisibility(View.VISIBLE);
                overview_section_header5.setVisibility(View.VISIBLE);
            }


        } catch (NullPointerException e) {
            Log.e("TAG", "Error: " + e.getMessage());

        }
//        if(updateCaregiver.getHousehold_case_status() == null && updateCaregiver.getHousehold_case_status().equals("1") || updateCaregiver.getHousehold_case_status().equals("2")){
//            overview_section_header5.setVisibility(View.GONE);
//
//            linearlayout_gender.setVisibility(View.GONE);
//            linearlayout_dob.setVisibility(View.GONE);
//            linearlayout_status.setVisibility(View.GONE);
//            linearlayout_relation.setVisibility(View.GONE);
//            linearlayout_phone.setVisibility(View.GONE);
//            linearlayout_name.setVisibility(View.GONE);
//        }

        Household householdByVCA = HouseholdDao.getHouseholdByVCA(childIndex.getHousehold_id());
        Boolean check = HouseholdDao.hasNonNullSubPopulationByVCA(childIndex.getUnique_id());
        if(check.equals(true)) {


        }




        String subpop1 = childIndex.getSubpop1();
        String subpop2 = childIndex.getSubpop2();
        String subpop3 = childIndex.getSubpop3();
        String subpop4 = childIndex.getSubpop4();
        String subpop5 = childIndex.getSubpop5();
        String subpop6 = childIndex.getSubpop6();

        assert subpop1 != null;
        assert subpop2 != null;
        assert subpop3 != null;
        assert subpop4 != null;
        assert subpop5 != null;
        assert subpop6 != null;

        Household household = HouseholdDao.getVcaSubPop(childIndex.getHousehold_id(),childIndex.getUnique_id());
        if(household.getSub_population() != null){
            subPopLayout1.setVisibility(View.GONE);
            subPopLayout2.setVisibility(View.VISIBLE);
        }
        else{
            subPopLayout1.setVisibility(View.VISIBLE);
            subPopLayout2.setVisibility(View.GONE);
        }
        String subPopulations = household.getSub_population();
        String subPopulationsValues = (subPopulations != null) ? keysToValues(subPopulations) : null;
        txtSubPopulation.setText((subPopulationsValues != null) ? subPopulationsValues : "");


        long timestamp = Long.parseLong(childIndex.getLast_interacted_with());

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date_time = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();

        if (childIndex.getDate_started_art() != null && childIndex.getIs_hiv_positive() != null &&
                childIndex.getIs_hiv_positive().equals("yes")){
            myview.setVisibility(View.VISIBLE);
        } else {
            myview.setVisibility(View.GONE);
        }

        if(Objects.equals(subpop1, "true")){
            sub1.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(subpop2, "true")){
            sub2.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(subpop3, "true")){
            sub3.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(subpop5, "true")){
            sub5.setVisibility(View.VISIBLE);
        }





        if (childIndex.getArt_number() != null){
            txtArtNumber.setText(childIndex.getArt_number());
        } else {
            txtArtNumber.setText("N/A");
        }

        String facility = null;
        VcaScreeningModel vcaScreeningModel = VCAScreeningDao.getVcaScreening(childIndex.getUnique_id());
        if (childIndex.getFacility() != null) {
            facility = childIndex.getFacility();
        }
        else {
            facility = "N/A";
        }
        txtFacility.setText(facility);


        if (childIndex.getCaregiver_sex() != null){
            txtGender.setText(childIndex.getGender());
            String myString = childIndex.getCaregiver_sex();
            txtGender.setText(myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase());
        } else {
            txtGender.setText("N/A");
        }

        if (childIndex.getDate_referred() != null){
            txtReferred.setText(childIndex.getDate_referred());
        } else {
            txtReferred.setText("N/A");
        }

        if (childIndex.getSchool() != null) {
            if (childIndex.getSchool().equals("not_in_school")) {
                txtSchool.setText("Not In School");
            } else if (childIndex.getSchool().equals("other")) {
                txtSchool.setText(childIndex.getOther_school() != null ? childIndex.getOther_school() : "N/A");
            } else {
                txtSchool.setText(childIndex.getSchoolName() != null ? childIndex.getSchoolName() : "N/A");
            }
        } else {
            txtSchool.setText("N/A");
        }




        if (childIndex.getDate_enrolled()!= null){
            txtEnrolled.setText(childIndex.getDate_enrolled());
        } else {
            txtEnrolled.setText("N/A");
        }

        if (childIndex.getArt_check_box() != null){
            txtArtCheckbox.setText(childIndex.getArt_check_box());
        } else {
            txtArtCheckbox.setText("N/A");
        }

        if (childIndex.getDate_started_art() != null){
            txtDateStartedArt.setText(childIndex.getDate_started_art());
        } else {
            txtDateStartedArt.setText("N/A");
        }

        if (childIndex.getDate_last_vl() != null){
            txtVlLastDate.setText(childIndex.getDate_last_vl());
        } else {
            txtVlLastDate.setText("N/A");
        }

        if (childIndex.getVl_last_result() != null){
            txtVlResult.setText(childIndex.getVl_last_result());
        } else {
            txtVlResult.setText("N/A");
        }

        List<VCAServiceModel> sModel = VCAServiceReportDao.getRecentServicesByVCAID(childIndex.getUnique_id());

        String viralLoadResult = null;

        if (!sModel.isEmpty()) {
            VCAServiceModel serviceM = sModel.get(0);
            viralLoadResult = serviceM.getVl_last_result();
        }

        if (viralLoadResult == null) {
            viralLoadResult = childIndex.getVl_last_result();
        }

        if (viralLoadResult != null) {
            try {
                int intValue = Integer.parseInt(viralLoadResult);
                txtIsSuppressed.setText(intValue <= 1000 ? "Yes" : "No");
            } catch (NumberFormatException e) {
                txtIsSuppressed.setText("Update VL Results");
            }
        } else {
            txtIsSuppressed.setText("Not set");
        }







        if (childIndex.getChild_mmd() != null){
            txtIsMMD.setText(childIndex.getChild_mmd());
        } else {
            txtIsMMD.setText("N/A");
        }

        if (childIndex.getLevel_mmd() != null){
            txtMMDResult.setText(childIndex.getLevel_mmd());
        } else {
            txtMMDResult.setText("N/A");
        }

        List<VCAServiceModel> serviceModels = VCAServiceReportDao.getRecentServicesByVCAID(childIndex.getUnique_id());

        if (!serviceModels.isEmpty()) {
            VCAServiceModel serviceModel = serviceModels.get(0);
            if(serviceModel.getVl_last_result() != null){
                recent_vl_result.setText(serviceModel.getVl_last_result());
            } else {
                if (childIndex.getVl_last_result() != null){
                    recent_vl_result.setText(childIndex.getVl_last_result());
                } else {
                    recent_vl_result.setText("N/A");
                }
            }

            if(serviceModel.getLevel_mmd() != null){
                recent_mmd_level.setText(serviceModel.getLevel_mmd());
            } else {
                if (childIndex.getLevel_mmd() != null){
                    recent_mmd_level.setText(childIndex.getLevel_mmd());
                } else {
                    recent_mmd_level.setText("N/A");
                }
            }

            if (serviceModel.getDate_next_vl() != null){
                txtNextVl.setText(serviceModel.getDate_next_vl());
            } else {
                if (childIndex.getDate_next_vl() != null){
                    txtNextVl.setText(childIndex.getDate_next_vl());
                } else {
                    txtNextVl.setText("N/A");
                }
            }


        } else {
            if (childIndex.getVl_last_result() != null){
                recent_vl_result.setText(childIndex.getVl_last_result());
            } else {
                recent_vl_result.setText("N/A");
            }

            if (childIndex.getLevel_mmd() != null){
                recent_mmd_level.setText(childIndex.getLevel_mmd());
            } else {
                recent_mmd_level.setText("N/A");
            }

            if (childIndex.getDate_next_vl() != null){
                txtNextVl.setText(childIndex.getDate_next_vl());
            } else {
                txtNextVl.setText("N/A");
            }
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myview2.setVisibility(View.VISIBLE);
            }
        });


        txtCaregiverName.setText(childIndex.getCaregiver_name() != null ? childIndex.getCaregiver_name() : "Not Set");
        txtDob.setText(childIndex.getCaregiver_birth_date() != null ? childIndex.getCaregiver_birth_date() : "Not Set");
        txtHiv.setText(childIndex.getCaregiver_hiv_status() != null ? childIndex.getCaregiver_hiv_status() : "Not Set");
        txtRelation.setText(childIndex.getRelation() != null ? childIndex.getRelation() : "Not Set");
        txtPhone.setText(childIndex.getCaregiver_phone() != null ? childIndex.getCaregiver_phone() : "Not Set");
        txtEditedBy.setText(childIndex.getCaseworker_name() != null ? childIndex.getCaseworker_name() : "Not Set");
        txtDateEdited.setText(date_time != null ? date_time : "Not Set");
        txtcPhone.setText(childIndex.getPhone() != null ? childIndex.getPhone() : "Not Set");


        abym = AbymSubpopulationDao.getAbymSubpopulation(childIndex.getUnique_id());
        muacModel = MuacDao.getMuac(childIndex.getUnique_id());
        childScreeningModel = VCAScreeningDao.getVcaScreening(childIndex.getUnique_id());

        boolean isSubpopulationVisible = false;

        if ( !"female".equals(childIndex.getGender()) && abym != null && "yes".equals(abym.getAbym_years()) ) {
            abymSub.setVisibility(View.VISIBLE);
        } else{
            abymSub.setVisibility(View.GONE);
        }

//        HouseholdMemberModel memberModel = HouseholdMemberDao.getMember(childIndex.getUnique_id());
//        if(memberModel != null && "sibling".equals(memberModel.getMember_type())){
//            siblingSubPop.setVisibility(View.VISIBLE);
//        } else {
//            siblingSubPop.setVisibility(View.GONE);
//        }

        Household sub = HouseholdDao.getHousehold(childIndex.getHousehold_id());
        VcaScreeningModel screen = VCAScreeningDao.getVcaScreening(childIndex.getUnique_id());

        if (sub != null) {
            String mappedValues = keysToValues(sub.getSub_population());
            if (mappedValues.contains("SIBS/INDEX FAMILY") && (!screen.getIndex_check_box().equals("yes") && !screen.getIndex_check_box().equals("1"))){
                siblingSubPop.setVisibility(View.VISIBLE);
            } else {
                siblingSubPop.setVisibility(View.GONE);
            }
        } else {
            siblingSubPop.setVisibility(View.GONE); // Default to hidden if no data
        }



        if(muacModel != null &&  ("red".equals(muacModel.getMuac()) || "yellow".equals(muacModel.getMuac()))){
            isSubpopulationVisible = true;
        }





        moreSubpopBtn.setVisibility(isSubpopulationVisible ? View.VISIBLE : View.GONE);

        moreSubpopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;

    }


    private boolean isYes(String value) {
        return value != null && value.equals("yes");
    }
    public String keysToValues(String keys) {
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put("subpop1", "C/ALHIV");
        keyValues.put("subpop2", "HEI");
        keyValues.put("subpop3", "C/WLHIV");
        keyValues.put("subpop4", "AGYW");
        keyValues.put("subpop5", "S/SV");
        keyValues.put("subpop", "C/FSWs");
        keyValues.put("PBFW", "PBFW");
        keyValues.put("Siblings of the Index and other family members", "SIBS/INDEX FAMILY");
        StringBuilder values = new StringBuilder();
        String[] keysArray = keys.replace("[", "").replace("]", "").replace("\"", "").split(",");
        for (String key : keysArray) {
            String value = keyValues.get(key.trim());
            if (value != null) {
                if (values.length() > 0) {
                    values.append(", ");
                }
                values.append(value);
            }
        }
        return values.toString();
    }

}
