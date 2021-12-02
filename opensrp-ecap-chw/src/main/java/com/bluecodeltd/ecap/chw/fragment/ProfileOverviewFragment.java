package com.bluecodeltd.ecap.chw.fragment;

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
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;

import java.util.HashMap;
import java.util.Objects;

public class ProfileOverviewFragment extends Fragment {

    RelativeLayout myview;
    LinearLayout myview2;
    ImageButton imgBtn;
    TextView txtArtNumber, sub1, sub2, sub3, sub4, sub5, sub6, txtReferred, txtFacility,
    txtEnrolled, txtArtCheckbox, txtDateStartedArt, txtVlLastDate, txtVlResult, txtIsSuppressed, txtNextVl, txtIsMMD, txtMMDResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

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

        txtVlResult = view.findViewById(R.id.last_vl_result);
        txtIsSuppressed = view.findViewById(R.id.vl_suppressed);
        txtNextVl = view.findViewById(R.id.next_vl_test);
        txtIsMMD = view.findViewById(R.id.on_mmd);
        txtFacility = view.findViewById(R.id.facility);
        txtMMDResult = view.findViewById(R.id.mmd_level);

        HashMap<String, String> mymap = ( (IndexDetailsActivity) requireActivity()).getData();

        String subpop1 = mymap.get("subpop1");
        String subpop2 = mymap.get("subpop2");
        String subpop3 = mymap.get("subpop3");
        String subpop4 = mymap.get("subpop4");
        String subpop5 = mymap.get("subpop5");
        String subpop6 = mymap.get("subpop6");

        assert subpop1 != null;
        assert subpop2 != null;
        assert subpop3 != null;
        assert subpop4 != null;
        assert subpop5 != null;
        assert subpop6 != null;

        if(mymap.get("date_started_art") != null){
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

        if (mymap.get("art_number") != null){
            txtArtNumber.setText(mymap.get("art_number"));
        } else {
            txtArtNumber.setText("N/A");
        }

        if (mymap.get("health_facility") != null){
            txtFacility.setText(mymap.get("health_facility"));
        } else {
            txtFacility.setText("N/A");
        }

        if (mymap.get("date_referred") != null){
            txtReferred.setText(mymap.get("date_referred"));
        } else {
            txtReferred.setText("N/A");
        }

        if (mymap.get("date_enrolled") != null){
            txtEnrolled.setText(mymap.get("date_enrolled"));
        } else {
            txtEnrolled.setText("N/A");
        }

        if (mymap.get("art_check_box") != null){
            txtArtCheckbox.setText(mymap.get("art_check_box"));
        } else {
            txtArtCheckbox.setText("N/A");
        }

        if (mymap.get("date_started_art") != null){
            txtDateStartedArt.setText(mymap.get("date_started_art"));
        } else {
            txtDateStartedArt.setText("N/A");
        }

        if (mymap.get("date_last_vl") != null){
            txtVlLastDate.setText(mymap.get("date_last_vl"));
        } else {
            txtVlLastDate.setText("N/A");
        }

        if (mymap.get("vl_last_result") != null){
            txtVlResult.setText(mymap.get("vl_last_result"));
        } else {
            txtVlResult.setText("N/A");
        }

        if (mymap.get("vl_suppressed") != null){
            txtIsSuppressed.setText(mymap.get("vl_suppressed"));
        } else {
            txtIsSuppressed.setText("N/A");
        }

        if (mymap.get("date_next_vl") != null){
            txtNextVl.setText(mymap.get("date_next_vl"));
        } else {
            txtNextVl.setText("N/A");
        }

        if (mymap.get("child_mmd") != null){
            txtIsMMD.setText(mymap.get("child_mmd"));
        } else {
            txtIsMMD.setText("N/A");
        }

        if (mymap.get("level_mmd") != null){
            txtMMDResult.setText(mymap.get("level_mmd"));
        } else {
            txtMMDResult.setText("N/A");
        }

        imgBtn.setOnClickListener(v -> {

            TransitionManager.beginDelayedTransition(myview2, new AutoTransition());
            myview2.setVisibility(View.VISIBLE);
            // imgBtn.setImageResource(R.drawable.goto_arrow);

        });


        return view;

    }
}
