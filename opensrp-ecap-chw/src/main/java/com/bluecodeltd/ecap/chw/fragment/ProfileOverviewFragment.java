package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import com.bluecodeltd.ecap.chw.util.Threading;
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.ProfileOverviewViewModel;
import com.bluecodeltd.ecap.chw.viewmodel.ProfileOverviewState;
import android.text.TextUtils;
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
import java.util.Objects;

public class ProfileOverviewFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentOverviewBinding binding;

    RelativeLayout myview;
    Button moreSubpopBtn;
    LinearLayout myview2,linearlayout_name,linearlayout_gender,linearlayout_dob,linearlayout_status,linearlayout_relation,linearlayout_phone,subPopLayout1,abymSubpopulation;
    ImageButton imgBtn;
    TextView abymTxt,disabledTxt,agedTxt,illnessTxt,childHeadTxt,notChildHeadTx,femaleHeadedTxt,survivorTxt;
    TextView txtArtNumber, sub1, sub2, sub3, sub4, sub5, sub6, sub7, sub8, sub9, sub10, sub11, sub12, sub13, sub14, sub15, sub16, otherSub,otherMemberSub,txtReferred, txtFacility,txtEditedBy,txtDateEdited,
            txtEnrolled, txtArtCheckbox, txtDateStartedArt, txtVlLastDate, txtVlResult, txtIsSuppressed, txtNextVl, txtIsMMD, txtMMDResult,
            txtCaregiverName, txtGender, txtDob, txtHiv, txtRelation, txtPhone,txtcPhone,txtSchool,recent_vl_result,recent_mmd_level,
            new_caregiver_name, overview_section_header3,overview_section_header5,overview_section_details_left, new_caregiver_gender, new_caregiver_dob, new_hiv_status, new_child_relation, new_caregiver_phone;

    AbymSubpopulationModel abym;

    MuacModel muacModel;
    VcaScreeningModel childScreeningModel;
    // Use centralized Threading
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentOverviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        txtEditedBy = binding.editedBy;
        txtDateEdited = binding.dateLastEdited;
        txtcPhone = binding.cPhone;
        txtArtNumber = binding.artNumber;
        myview2 = binding.mylayout;
        imgBtn = binding.arrowButton;
        sub1 = binding.subpop1;
        sub2 = binding.subpop2;
        sub3 = binding.subpop3;
        sub4 = binding.subpop4;
        sub5 = binding.subpop5;
        sub6 = binding.subpop6;
        sub7 = binding.subpop7;
        sub8 = binding.subpop8;
        sub9 = binding.subpop9;
        sub10 = binding.subpop10;
        sub11 = binding.subpop11;
        sub12 = binding.subpop12;
        sub13 = binding.subpop13;
        sub14 = binding.subpop14;
        sub15 = binding.subpop15;
        sub16 = binding.subpop16;
        subPopLayout1 = binding.subPopLayout1;
        myview = binding.myview;
        txtReferred = binding.referred;
        txtEnrolled = binding.enrolled;
        txtArtCheckbox = binding.isArt;
        txtDateStartedArt = binding.artDate;
        txtVlLastDate = binding.dateLastVl;
        txtSchool = binding.school;

        txtVlResult = binding.lastVlResult;
        txtIsSuppressed = binding.vlSuppressed;
        txtNextVl = binding.nextVlTest;
        txtIsMMD = binding.onMmd;
        txtFacility = binding.facility;
        txtMMDResult = binding.mmdLevel;

        //Caregiver Views
        txtCaregiverName= binding.caregiverName;
        txtGender = binding.caregiverGender;
        txtDob= binding.caregiverDob;
        txtHiv = binding.hivStatus;
        txtRelation = binding.childRelation;
        txtPhone = binding.caregiverPhone;
        recent_vl_result = binding.recentVlResult;
        recent_mmd_level = binding.recentMmdLevel;

        new_caregiver_name = binding.newCaregiverName;
        new_caregiver_gender = binding.newCaregiverGender;
        new_caregiver_dob = binding.newCaregiverDob;
        new_hiv_status = binding.newHivStatus;
        new_child_relation = binding.newChildRelation;
        new_caregiver_phone = binding.newCaregiverPhone;
        overview_section_header3 = binding.overviewSectionHeader3;
        overview_section_header5 = binding.overviewSectionHeader5;


        linearlayout_name =  binding.linearlayoutName;
        linearlayout_gender = binding.linearlayoutGender;
        linearlayout_dob = binding.linearlayoutDob;
        linearlayout_status = binding.linearlayoutStatus;
        linearlayout_relation = binding.linearlayoutRelation;
        linearlayout_phone = binding.linearlayoutPhone;


        moreSubpopBtn = binding.morePopulation;





        HashMap<String, Child> mymap = ( (IndexDetailsActivity) requireActivity()).getData();
        Child childIndex =mymap.get("Child");
        if (childIndex == null) {
            txtFacility.setText("N/A");
            txtArtNumber.setText("N/A");
            txtReferred.setText("N/A");
            txtEnrolled.setText("N/A");
            txtArtCheckbox.setText("N/A");
            txtDateStartedArt.setText("N/A");
            txtVlLastDate.setText("N/A");
            txtVlResult.setText("N/A");
            txtIsSuppressed.setText("Not set");
            txtNextVl.setText("N/A");
            txtIsMMD.setText("N/A");
            txtMMDResult.setText("N/A");
            txtCaregiverName.setText("Not Set");
            txtGender.setText("N/A");
            txtDob.setText("Not Set");
            txtHiv.setText("Not Set");
            txtRelation.setText("Not Set");
            txtPhone.setText("Not Set");
            txtEditedBy.setText("Not Set");
            txtDateEdited.setText("Not Set");
            txtcPhone.setText("Not Set");
            moreSubpopBtn.setVisibility(View.GONE);
            return view;
        }

        HashMap<String, newCaregiverModel> caregiverDetails = ((IndexDetailsActivity) requireActivity()).getUpdatedCaregiverData();
        newCaregiverModel updateCaregiver = caregiverDetails.get("UpdatedCaregiver");

        new_caregiver_name.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_name() != null ? updateCaregiver.getNew_caregiver_name() : "Not Set");
        new_caregiver_gender.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_sex() != null ? updateCaregiver.getNew_caregiver_sex() : "Not Set");
        new_caregiver_dob.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_birth_date() != null ? updateCaregiver.getNew_caregiver_birth_date() : "Not Set");
        new_hiv_status.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_hiv_status() != null ? updateCaregiver.getNew_caregiver_hiv_status() : "Not Set");
        new_child_relation.setText(updateCaregiver != null && updateCaregiver.getNew_relation() != null ? updateCaregiver.getNew_relation() : "Not Set");
        new_caregiver_phone.setText(updateCaregiver != null && updateCaregiver.getNew_caregiver_phone() != null ? updateCaregiver.getNew_caregiver_phone() : "Not Set");

        if(updateCaregiver != null && !TextUtils.isEmpty(updateCaregiver.getNew_caregiver_name())){
            overview_section_header3.setText("Previous Caregiver Details");
            linearlayout_gender.setVisibility(View.VISIBLE);
            linearlayout_dob.setVisibility(View.VISIBLE);
            linearlayout_status.setVisibility(View.VISIBLE);
            linearlayout_relation.setVisibility(View.VISIBLE);
            linearlayout_phone.setVisibility(View.VISIBLE);
            linearlayout_name.setVisibility(View.VISIBLE);
            overview_section_header5.setVisibility(View.VISIBLE);
        } else {
            overview_section_header5.setVisibility(View.GONE);
            linearlayout_gender.setVisibility(View.GONE);
            linearlayout_dob.setVisibility(View.GONE);
            linearlayout_status.setVisibility(View.GONE);
            linearlayout_relation.setVisibility(View.GONE);
            linearlayout_phone.setVisibility(View.GONE);
            linearlayout_name.setVisibility(View.GONE);
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

        String householdId = childIndex.getHousehold_id();
        Household householdByVCA = householdId != null ? HouseholdDao.getHouseholdByVCA(householdId) : null;
        String uniqueId = childIndex.getUnique_id();
        Boolean check = uniqueId != null ? HouseholdDao.hasNonNullSubPopulationByVCA(uniqueId) : Boolean.FALSE;
        if(check.equals(true)) {


        }




        String subpop1 = childIndex.getSubpop1();
        String subpop2 = childIndex.getSubpop2();
        String subpop3 = childIndex.getSubpop3();
        String subpop4 = childIndex.getSubpop4();
        String subpop5 = childIndex.getSubpop5();
        String subpop6 = childIndex.getSubpop6();

        // Subpopulation details loaded asynchronously below


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

        if (childIndex.getArt_number() != null){
            txtArtNumber.setText(childIndex.getArt_number());
        } else {
            txtArtNumber.setText("N/A");
        }

        String facility = null;
        // Screening fetch moved off main thread
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

        // Suppression computed after async fetch







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

        // Recent service values set after async fetch
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


        // Button visibility set after async fetch

        moreSubpopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // Use ViewModel: fetch background data; apply UI from state
        ProfileOverviewViewModel vm = new ViewModelProvider(this).get(ProfileOverviewViewModel.class);
        vm.getState().observe(getViewLifecycleOwner(), st -> {
            if (!isAdded() || st == null) return;
            List<VCAServiceModel> recentServices = st.getRecentServices();
            AbymSubpopulationModel abymLocal = st.getAbym();
            MuacModel muacLocal = st.getMuac();
            VcaScreeningModel screenLocal = st.getScreen();
            Household household = st.getHousehold();

            if (household != null && household.getCaseworker_name() != null) {
                txtEditedBy.setText(household.getCaseworker_name());
            } else if (childIndex.getCaseworker_name() != null) {
                txtEditedBy.setText(childIndex.getCaseworker_name());
            } else {
                txtEditedBy.setText("Not Set");
            }

            updateOverviewSubpops(screenLocal, muacLocal, st.getPbfwStatus());

            String viralLoadResult = null;
            if (recentServices != null && !recentServices.isEmpty()) {
                VCAServiceModel serviceM = recentServices.get(0);
                viralLoadResult = serviceM.getVl_last_result();
            }
            if (viralLoadResult == null) viralLoadResult = childIndex.getVl_last_result();
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

            if (recentServices != null && !recentServices.isEmpty()) {
                VCAServiceModel serviceModel = recentServices.get(0);
                recent_vl_result.setText(serviceModel.getVl_last_result() != null ? serviceModel.getVl_last_result() : (childIndex.getVl_last_result() != null ? childIndex.getVl_last_result() : "N/A"));
                recent_mmd_level.setText(serviceModel.getLevel_mmd() != null ? serviceModel.getLevel_mmd() : (childIndex.getLevel_mmd() != null ? childIndex.getLevel_mmd() : "N/A"));
                txtNextVl.setText(serviceModel.getDate_next_vl() != null ? serviceModel.getDate_next_vl() : (childIndex.getDate_next_vl() != null ? childIndex.getDate_next_vl() : "N/A"));
            } else {
                recent_vl_result.setText(childIndex.getVl_last_result() != null ? childIndex.getVl_last_result() : "N/A");
                recent_mmd_level.setText(childIndex.getLevel_mmd() != null ? childIndex.getLevel_mmd() : "N/A");
                txtNextVl.setText(childIndex.getDate_next_vl() != null ? childIndex.getDate_next_vl() : "N/A");
            }

            boolean showMore = muacLocal != null && ("red".equals(muacLocal.getMuac()) || "yellow".equals(muacLocal.getMuac()));
            moreSubpopBtn.setVisibility(showMore ? View.VISIBLE : View.GONE);
        });
        if (householdId != null && uniqueId != null) {
            new ViewModelProvider(this).get(ProfileOverviewViewModel.class)
                    .refresh(householdId, uniqueId);
        }

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateOverviewSubpops(VcaScreeningModel screenLocal, MuacModel muacLocal, String pbfwStatus) {
        if (screenLocal == null) {
            hideOverviewSubpops();
            subPopLayout1.setVisibility(View.GONE);
            return;
        }

        hideOverviewSubpops();

        String gender = screenLocal.getGender();
        boolean anyVisible = false;

        anyVisible |= setSubpopVisibility(sub1, isYesFlag(screenLocal.getIs_hiv_positive()));
        anyVisible |= setSubpopVisibility(sub2, isYesFlag(screenLocal.getPositive_mother()));
        anyVisible |= setSubpopVisibility(sub3, isYesFlag(screenLocal.getIs_biological_child_of_mother_living_with_hiv()));
        anyVisible |= setSubpopVisibility(sub4, isYesFlag(screenLocal.getAgyw_sexually_active()) && "female".equalsIgnoreCase(gender));
        anyVisible |= setSubpopVisibility(sub5, isYesFlag(screenLocal.getChild_ever_experienced_sexual_violence()));
        anyVisible |= setSubpopVisibility(sub6, isYesFlag(screenLocal.getIs_the_child_caregiver_an_fsw()));
        anyVisible |= setSubpopVisibility(sub7, isYesFlag(screenLocal.getAbym_years()) && isYesFlag(screenLocal.getAbym_sexually_active()) && "male".equalsIgnoreCase(gender));
        anyVisible |= setSubpopVisibility(sub8, isNoFlag(screenLocal.getIndex_check_box()));
        anyVisible |= setSubpopVisibility(sub9, isNoFlag(screenLocal.getAdult_primary_caregiver_present()));
        if ("positive".equalsIgnoreCase(pbfwStatus)) {
            sub10.setText("C/A PBFW Positive");
            anyVisible |= setSubpopVisibility(sub10, true);
        } else if ("negative".equalsIgnoreCase(pbfwStatus)) {
            sub10.setText("C/A PBFW Negative");
            anyVisible |= setSubpopVisibility(sub10, true);
        }
        anyVisible |= setSubpopVisibility(sub11, isYesFlag(screenLocal.getCaregiver_chronically_ill()));
        anyVisible |= setSubpopVisibility(sub12, isYesFlag(screenLocal.getSingle_woman_headed_household()));
        anyVisible |= setSubpopVisibility(sub13, isYesFlag(screenLocal.getLiving_with_disability()));
        anyVisible |= setSubpopVisibility(sub14, isYesFlag(screenLocal.getCaregiver_aged_65_plus()));
        anyVisible |= setSubpopVisibility(sub15, muacLocal != null && ("red".equalsIgnoreCase(muacLocal.getMuac()) || "yellow".equalsIgnoreCase(muacLocal.getMuac())));
        anyVisible |= setSubpopVisibility(sub16, isYesFlag(screenLocal.getAny_form_of_violence()));
        subPopLayout1.setVisibility(anyVisible ? View.VISIBLE : View.GONE);
    }

    private void hideOverviewSubpops() {
        setSubpopVisibility(sub1, false);
        setSubpopVisibility(sub2, false);
        setSubpopVisibility(sub3, false);
        setSubpopVisibility(sub4, false);
        setSubpopVisibility(sub5, false);
        setSubpopVisibility(sub6, false);
        setSubpopVisibility(sub7, false);
        setSubpopVisibility(sub8, false);
        setSubpopVisibility(sub9, false);
        setSubpopVisibility(sub10, false);
        setSubpopVisibility(sub11, false);
        setSubpopVisibility(sub12, false);
        setSubpopVisibility(sub13, false);
        setSubpopVisibility(sub14, false);
        setSubpopVisibility(sub15, false);
        setSubpopVisibility(sub16, false);
    }

    private boolean setSubpopVisibility(TextView textView, boolean visible) {
        if (textView != null) {
            textView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return visible;
    }


    private boolean isYes(String value) {
        return value != null && value.equals("yes");
    }
    private boolean isYesFlag(String value) {
        if (TextUtils.isEmpty(value)) return false;
        String v = value.trim().toLowerCase(Locale.ENGLISH);
        return "yes".equals(v) || "1".equals(v) || "true".equals(v);
    }

    private boolean isNoFlag(String value) {
        if (TextUtils.isEmpty(value)) return false;
        String v = value.trim().toLowerCase(Locale.ENGLISH);
        return "no".equals(v) || "0".equals(v) || "false".equals(v);
    }

}
