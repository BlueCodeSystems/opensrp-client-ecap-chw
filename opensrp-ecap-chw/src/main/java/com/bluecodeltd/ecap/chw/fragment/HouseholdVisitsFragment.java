package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
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
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.adapter.CaregiverVisitAdapter;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseholdVisitsFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<CaregiverVisitationModel> visitList = new ArrayList<>();
    private LinearLayout linearLayout;
    View vieww;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vieww = inflater.inflate(R.layout.fragment_housevisits, container, false);

        vieww = inflater.inflate(R.layout.fragment_childvisits, container, false);

        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();
        Household house = mymap.get("house");
        String houseId = house.getHousehold_id();

        recyclerView = vieww.findViewById(R.id.visitrecyclerView);
        linearLayout = vieww.findViewById(R.id.visit_container);

        visitList.clear();

        visitList.addAll(CaregiverVisitationDao.getVisitsByID(houseId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new CaregiverVisitAdapter(visitList, getContext());
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }


        return vieww;


    }

}
