package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdVisitsViewModel;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.util.ArrayList;
import java.util.HashMap;
import com.bluecodeltd.ecap.chw.util.Threading;

public class HouseholdVisitsFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentChildvisitsBinding binding;

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private final ArrayList<CaregiverVisitationModel> visitList = new ArrayList<>();
    private LinearLayout linearLayout;
    // Use centralized Threading

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Actual layout used is fragment_childvisits (second inflation took effect previously)
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentChildvisitsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        HashMap<String, Household> mymap = ((HouseholdDetails) requireActivity()).getData();
        Household house = mymap.get("house");
        String houseId = house != null ? house.getHousehold_id() : null;

        recyclerView = binding.visitrecyclerView;
        linearLayout = binding.visitContainer;

        visitList.clear();

        // subtle loading indicator
        View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        HouseholdVisitsViewModel vm = new ViewModelProvider(this).get(HouseholdVisitsViewModel.class);
        vm.getVisits().observe(getViewLifecycleOwner(), list -> {
            if (!isAdded() || list == null) return;
            RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(eLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            visitList.clear();
            visitList.addAll(list);
            recyclerViewadapter = new CaregiverVisitAdapter(visitList, getContext());
            recyclerView.setAdapter(recyclerViewadapter);
            recyclerViewadapter.notifyDataSetChanged();

            if (recyclerViewadapter.getItemCount() > 0) {
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            if (progress != null) progress.setVisibility(View.GONE);
        });
        if (houseId != null) {
            vm.refresh(houseId);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
