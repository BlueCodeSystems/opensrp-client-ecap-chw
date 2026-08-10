package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.adapter.HouseholdChildrenAdapter;
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenViewModel;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenState;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseholdChildrenFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentChildrenBinding binding;

    private RecyclerView recyclerView;
    private HouseholdChildrenAdapter householdChildrenAdapter;
    private ArrayList<Child> childList = new ArrayList<>();
    String nutritionWarning, muacScore;
    private HouseholdChildrenViewModel viewModel;
    CaregiverAssessmentModel caregiverAssessmentModel;
    String houseId;
    // Use centralized Threading

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentChildrenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Activity hostActivity = getActivity();
        HouseholdDetails detailsActivity = hostActivity instanceof HouseholdDetails ? (HouseholdDetails) hostActivity : null;
        HashMap<String, Household> mymap = detailsActivity != null ? detailsActivity.getData() : null;
        HashMap<String, CaregiverAssessmentModel> vmap = detailsActivity != null ? detailsActivity.getVulnerabilities() : null;

        Household house = mymap != null ? mymap.get("house") : null;
        if (house == null) {
            house = detailsActivity != null ? detailsActivity.house : null;
        }
        houseId = house != null ? house.getHousehold_id() : null;
        if ((houseId == null || houseId.trim().isEmpty()) && detailsActivity != null) {
            houseId = detailsActivity.householdId;
        }

        caregiverAssessmentModel = vmap != null ? vmap.get("vulnerabilities") : null;

        if (caregiverAssessmentModel != null){
            nutritionWarning = caregiverAssessmentModel.getHousehold_eaten_month();
        }

        if(nutritionWarning != null && (nutritionWarning.equals("sometimes") || nutritionWarning.equals("Rarely (once or twice)"))){

            muacScore = "1";

        } else {

            muacScore = "0";

        }

        recyclerView = binding.recyclerView;
        View progress = binding.progressLoading;

        childList.clear();

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        householdChildrenAdapter = new HouseholdChildrenAdapter(childList, getContext(), muacScore);
        recyclerView.setAdapter(householdChildrenAdapter);

        // ViewModel: observe and refresh
        viewModel = new ViewModelProvider(this).get(HouseholdChildrenViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), state -> applyChildrenState(state));
        if (progress != null) progress.setVisibility(View.VISIBLE);
        if (houseId != null && !houseId.trim().isEmpty()) {
            viewModel.refresh(houseId);
        } else if (progress != null) {
            progress.setVisibility(View.GONE);
        }


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    reloadChildrenList(houseId);
    }


    public void reloadChildrenList(String houseId) {
        String resolvedHouseId = houseId;
        if ((resolvedHouseId == null || resolvedHouseId.trim().isEmpty()) && isAdded()) {
            try {
                Activity hostActivity = getActivity();
                if (hostActivity instanceof HouseholdDetails) {
                    resolvedHouseId = ((HouseholdDetails) hostActivity).householdId;
                }
            } catch (Exception ignored) { }
        }
        this.houseId = resolvedHouseId;
        View progress2 = (binding != null) ? binding.progressLoading : null;
        if (progress2 != null) progress2.setVisibility(View.VISIBLE);
        if (viewModel != null) viewModel.refresh(resolvedHouseId);
    }

    private void applyChildrenState(HouseholdChildrenState state) {
        if (!isAdded() || state == null) return;
        childList.clear();
        if (state.getChildren() != null) childList.addAll(state.getChildren());
        try { if (householdChildrenAdapter != null) householdChildrenAdapter.notifyDataSetChanged(); } catch (Exception ignored) {}
        String count = (state.getCount() != null && !state.getCount().trim().isEmpty()) ? state.getCount().trim() : "0";
        Activity hostActivity = getActivity();
        if (hostActivity instanceof HouseholdDetails) {
            HouseholdDetails detailsActivity = (HouseholdDetails) hostActivity;
            detailsActivity.childrenCount = count;
            if (detailsActivity.childTabCount != null) {
                detailsActivity.childTabCount.setText(count);
            }
        }
        View progress = (binding != null) ? binding.progressLoading : null;
        if (progress != null) progress.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
