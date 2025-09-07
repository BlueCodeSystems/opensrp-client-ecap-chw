package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.MotherDetail;
import com.bluecodeltd.ecap.chw.adapter.ChildrenAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenState;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenViewModel;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import com.bluecodeltd.ecap.chw.util.Threading;

public class MotherChildrenFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<Child> childList = new ArrayList<>();
    private HouseholdChildrenViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_children, container, false);

        HashMap<String, CommonPersonObjectClient> mymap = ( (MotherDetail) requireActivity()).getData();

        CommonPersonObjectClient mother = mymap.get("mother");
        String houseId = mother.getColumnmaps().get("household_id");

        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ChildrenAdapter(childList, getContext(), "0");
        recyclerView.setAdapter(recyclerViewadapter);

        View progress = view.findViewById(R.id.progress_loading);
        if (progress != null) progress.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(this).get(HouseholdChildrenViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), this::applyChildrenState);
        viewModel.refresh(houseId);


        return view;

    }

    private void applyChildrenState(HouseholdChildrenState state) {
        if (!isAdded() || state == null) return;
        childList.clear();
        if (state.getChildren() != null) childList.addAll(state.getChildren());
        recyclerViewadapter.notifyDataSetChanged();
        View progress = getView() != null ? getView().findViewById(R.id.progress_loading) : null;
        if (progress != null) progress.setVisibility(View.GONE);
    }
}
