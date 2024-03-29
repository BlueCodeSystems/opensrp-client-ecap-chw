package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.HouseholdIndexActivity;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.ChildrenAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HouseholdChildrenFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<Child> childList = new ArrayList<>();
    String nutritionWarning, muacScore;
    CaregiverAssessmentModel caregiverAssessmentModel;
    String houseId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_children, container, false);

        HashMap<String, Household> mymap = ( (HouseholdDetails) requireActivity()).getData();
        HashMap<String, CaregiverAssessmentModel> vmap = ( (HouseholdDetails) requireActivity()).getVulnerabilities();

        Household house = mymap.get("house");
       houseId = house.getHousehold_id();

        caregiverAssessmentModel = vmap.get("vulnerabilities");

        if (caregiverAssessmentModel != null){
            nutritionWarning = caregiverAssessmentModel.getHousehold_eaten_month();
        }

        if(nutritionWarning != null && (nutritionWarning.equals("sometimes") || nutritionWarning.equals("Rarely (once or twice)"))){

            muacScore = "1";

        } else {

            muacScore = "0";

        }

        recyclerView = view.findViewById(R.id.recyclerView);

        childList.clear();

        childList.addAll(IndexPersonDao.getFamilyChildren(houseId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ChildrenAdapter(childList, getContext(), muacScore);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    reloadChildrenList(houseId);
    }


    public void reloadChildrenList(String houseId) {
        childList.clear();

        childList.addAll(IndexPersonDao.getFamilyChildren(houseId));
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

       String childrenCount = ((HouseholdDetails) requireActivity()).childrenCount = IndexPersonDao.countChildren(houseId);

        ((HouseholdDetails) requireActivity()).childTabCount.setText(childrenCount);
    }
}
