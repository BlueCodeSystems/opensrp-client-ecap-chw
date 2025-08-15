package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.VcaHivAssessmentUnder15Adapter;
import com.bluecodeltd.ecap.chw.adapter.VcaHiveAssessmentAbove15Adapter;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentAbove15Dao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentUnder15Dao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VcaHivAssesmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VcaHivAssesmentFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;


    private LinearLayout linearLayout;
    View vieww;
    public VcaScreeningModel indexVCA;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VcaHivAssesmentFragment() {
        // Required empty public constructor
    }

    public static VcaHivAssesmentFragment newInstance(String param1, String param2) {
        VcaHivAssesmentFragment fragment = new VcaHivAssesmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vieww = inflater.inflate(R.layout.fragment_vca_hiv_assesment, container, false);

        String childId  = ( (IndexDetailsActivity) requireActivity()).uniqueId;
        indexVCA = VCAScreeningDao.getVcaScreening(childId);


        recyclerView = vieww.findViewById(R.id.visitrecyclerView);
        linearLayout = vieww.findViewById(R.id.visit_container);


        if (indexVCA != null && indexVCA.getAdolescent_birthdate() != null) {
            int compareAge = calculateAge(indexVCA.getAdolescent_birthdate());
            if (compareAge <= 14){
                getAssessmentUnder15(recyclerView,recyclerViewadapter,childId);
            } else {
                getAssessmentAbove15(recyclerView,recyclerViewadapter,childId);
            }
        }

        return vieww;

    }
    private int calculateAge(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dateOfBirth = sdf.parse(inputDate);

            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public void getAssessmentUnder15(RecyclerView recyclerView, RecyclerView.Adapter recyclerViewadapter, String childId){
        ArrayList<HivRiskAssessmentUnder15Model> assessmentList = new ArrayList<>();

        assessmentList.clear();

        assessmentList.addAll(HivAssessmentUnder15Dao.getHivAssessment(childId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new VcaHivAssessmentUnder15Adapter(getContext(), assessmentList);
        recyclerView.setAdapter(recyclerViewadapter);

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }

    }
    public void getAssessmentAbove15(RecyclerView recyclerView, RecyclerView.Adapter recyclerViewadapter, String childId){
        ArrayList<HivRiskAssessmentAbove15Model> assessmentList2 = new ArrayList<>();

        assessmentList2.clear();

        assessmentList2.addAll(HivAssessmentAbove15Dao.getHivAssessment(childId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new VcaHiveAssessmentAbove15Adapter(getContext(), assessmentList2);
        recyclerView.setAdapter(recyclerViewadapter);

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }

    }

}