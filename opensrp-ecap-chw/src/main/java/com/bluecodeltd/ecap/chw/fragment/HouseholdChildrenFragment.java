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
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenViewModel;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenState;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import com.bluecodeltd.ecap.chw.util.Threading;

public class HouseholdChildrenFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentChildrenBinding binding;

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
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

        recyclerView = binding.recyclerView;
        View progress = binding.progressLoading;

        childList.clear();

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ChildrenAdapter(childList, getContext(), muacScore);
        recyclerView.setAdapter(recyclerViewadapter);

        // ViewModel: observe and refresh
        viewModel = new ViewModelProvider(this).get(HouseholdChildrenViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), state -> applyChildrenState(state));
        if (progress != null) progress.setVisibility(View.VISIBLE);
        viewModel.refresh(houseId);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    reloadChildrenList(houseId);
    }


    public void reloadChildrenList(String houseId) {
        View progress2 = (binding != null) ? binding.progressLoading : null;
        if (progress2 != null) progress2.setVisibility(View.VISIBLE);
        viewModel.refresh(houseId);
    }

    private void applyChildrenState(HouseholdChildrenState state) {
        if (!isAdded() || state == null) return;
        childList.clear();
        if (state.getChildren() != null) childList.addAll(state.getChildren());
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
        ((HouseholdDetails) requireActivity()).childrenCount = state.getCount();
        ((HouseholdDetails) requireActivity()).childTabCount.setText(state.getCount());
        View progress = (binding != null) ? binding.progressLoading : null;
        if (progress != null) progress.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
