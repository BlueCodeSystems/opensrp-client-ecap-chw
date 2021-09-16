package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;

import java.util.HashMap;
import java.util.Objects;

public class ProfileOverviewFragment extends Fragment {

    TextView txtArtNumber, sub1, sub2, sub3, sub4, sub5, sub6, txtReferred,
    txtEnrolled, txtArtCheckbox, txtDateStartedArt, txtVlLastDate, txtVlResult, txtIsSuppressed, txtNextVl, txtIsMMD, txtMMDResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        txtArtNumber = view.findViewById(R.id.art_number);
        sub1 = view.findViewById(R.id.subpop1);
        sub2 = view.findViewById(R.id.subpop2);
        sub3 = view.findViewById(R.id.subpop3);
        sub4 = view.findViewById(R.id.subpop4);
        sub5 = view.findViewById(R.id.subpop5);
        sub6 = view.findViewById(R.id.subpop6);
        txtReferred = view.findViewById(R.id.referred);
        txtEnrolled = view.findViewById(R.id.enrolled);
        txtArtCheckbox = view.findViewById(R.id.is_art);
        txtDateStartedArt = view.findViewById(R.id.art_date);
        txtVlLastDate = view.findViewById(R.id.date_last_vl);

        txtVlResult = view.findViewById(R.id.last_vl_result);
        txtIsSuppressed = view.findViewById(R.id.vl_suppressed);
        txtNextVl = view.findViewById(R.id.next_vl_test);
        txtIsMMD = view.findViewById(R.id.on_mmd);
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


        txtArtNumber.setText(mymap.get("art_number"));
        txtReferred.setText(mymap.get("date_referred"));
        txtEnrolled.setText(mymap.get("date_enrolled"));
        txtArtCheckbox.setText(mymap.get("art_check_box"));
        txtDateStartedArt.setText(mymap.get("date_started_art"));
        txtVlLastDate.setText(mymap.get("date_last_vl"));
        txtVlResult.setText(mymap.get("vl_last_result"));
        txtIsSuppressed.setText(mymap.get("vl_suppressed"));
        txtNextVl.setText(mymap.get("date_next_vl"));
        txtIsMMD.setText(mymap.get("child_mmd"));
        txtMMDResult.setText(mymap.get("level_mmd"));

        return view;

    }

}
