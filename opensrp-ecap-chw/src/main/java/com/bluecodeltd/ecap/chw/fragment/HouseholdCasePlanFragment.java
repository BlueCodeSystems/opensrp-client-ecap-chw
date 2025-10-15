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
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.CasePlanAdapter;
import com.bluecodeltd.ecap.chw.adapter.HouseholdCasePlanAdapter;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdCasePlanViewModel;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import com.bluecodeltd.ecap.chw.util.Threading;

public class HouseholdCasePlanFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentHouseholdcaseplansBinding binding;

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<CasePlanModel> householdCasePlanList = new ArrayList<>();
    private LinearLayout linearLayout;
    // Use centralized Threading

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentHouseholdcaseplansBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        String householdId = ( (HouseholdDetails) requireActivity()).house.getHousehold_id();
        Household house = ( (HouseholdDetails) requireActivity()).house;
        recyclerView = binding.householdRecycler;
        linearLayout = binding.householdVisitContainer;
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new HouseholdCasePlanAdapter(householdCasePlanList, getContext(),house);
        recyclerView.setAdapter(recyclerViewadapter);

        View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);
        HouseholdCasePlanViewModel vm = new ViewModelProvider(this).get(HouseholdCasePlanViewModel.class);
        vm.getCasePlans().observe(getViewLifecycleOwner(), list -> {
            if (!isAdded() || list == null) return;
            householdCasePlanList.clear();
            householdCasePlanList.addAll(list);
            try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
            if (recyclerViewadapter.getItemCount() > 0){
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            if (progress != null) progress.setVisibility(View.GONE);
        });
        vm.refresh(householdId);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(recyclerViewadapter);
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
