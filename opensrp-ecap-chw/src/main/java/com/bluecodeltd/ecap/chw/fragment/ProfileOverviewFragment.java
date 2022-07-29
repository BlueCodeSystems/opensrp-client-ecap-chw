package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProfileOverviewFragment extends Fragment {

    RelativeLayout myview;
    LinearLayout myview2;
    ImageButton imgBtn;
    TextView txtArtNumber, sub1, sub2, sub3, sub4, sub5, sub6, txtReferred, txtFacility,txtEditedBy,txtDateEdited,
    txtEnrolled, txtArtCheckbox, txtDateStartedArt, txtVlLastDate, txtVlResult, txtIsSuppressed, txtNextVl, txtIsMMD, txtMMDResult,
            txtCaregiverName, txtGender, txtDob, txtHiv, txtRelation, txtPhone,txtcPhone,txtSchool;

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

        HashMap<String, Child> mymap = ( (IndexDetailsActivity) requireActivity()).getData();
        Child childIndex =mymap.get("Child");


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

        long timestamp = Long.parseLong(childIndex.getLast_interacted_with());

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date_time = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();

        if(childIndex.getDate_started_art() != null){
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

        if (Objects.equals(subpop4, "true")){
            sub4.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(subpop5, "true")){
            sub5.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(subpop6, "true")){
            sub6.setVisibility(View.VISIBLE);
        }

        if (childIndex.getArt_number() != null){
            txtArtNumber.setText(childIndex.getArt_number());
        } else {
            txtArtNumber.setText("N/A");
        }

        if (childIndex.getFacility() != null){
            txtFacility.setText(childIndex.getFacility());
        } else {
            txtFacility.setText("N/A");
        }

        if (childIndex.getDate_referred() != null){
            txtReferred.setText(childIndex.getDate_referred());
        } else {
            txtReferred.setText("N/A");
        }


        if (childIndex.getSchoolName() != null){
            txtSchool.setText(childIndex.getSchoolName());
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

        if (childIndex.getVl_suppressed() != null){
            txtIsSuppressed.setText(childIndex.getVl_suppressed());
        } else {
            txtIsSuppressed.setText("N/A");
        }

        if (childIndex.getDate_next_vl() != null){
            txtNextVl.setText(childIndex.getDate_next_vl());
        } else {
            txtNextVl.setText("N/A");
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

        imgBtn.setOnClickListener(v -> {

            TransitionManager.beginDelayedTransition(myview2, new AutoTransition());
            myview2.setVisibility(View.VISIBLE);
            // imgBtn.setImageResource(R.drawable.goto_arrow);

        });

        txtCaregiverName.setText(childIndex.getCaregiver_name());
        String myString = childIndex.getCaregiver_sex();
        txtGender.setText(myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase());
        txtDob.setText(childIndex.getCaregiver_birth_date());
        txtHiv.setText(childIndex.getCaregiver_hiv_status());
        txtRelation.setText(childIndex.getRelation());
        txtPhone.setText(childIndex.getCaregiver_phone());
        txtEditedBy.setText(childIndex.getCaseworker_name());
        txtDateEdited.setText(date_time);
        txtcPhone.setText(childIndex.getPhone());

        return view;

    }

}
