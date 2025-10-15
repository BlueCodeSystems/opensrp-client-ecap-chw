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
import com.bluecodeltd.ecap.chw.adapter.CasePlanAdapter;
import com.bluecodeltd.ecap.chw.adapter.VisitAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import androidx.lifecycle.ViewModelProvider;
import com.bluecodeltd.ecap.chw.viewmodel.ChildVisitsViewModel;

import java.util.ArrayList;
import com.bluecodeltd.ecap.chw.util.Threading;

public class ChildVisitsFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<VcaVisitationModel> visitList = new ArrayList<>();
    private LinearLayout linearLayout;
    View vieww;
    // Use ViewModel + centralized Threading
    private ChildVisitsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vieww = inflater.inflate(R.layout.fragment_childvisits, container, false);

        String childId  = ( (IndexDetailsActivity) requireActivity()).uniqueId;

        recyclerView = vieww.findViewById(R.id.visitrecyclerView);
        linearLayout = vieww.findViewById(R.id.visit_container);

        visitList.clear();
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new VisitAdapter(visitList, getContext());
        recyclerView.setAdapter(recyclerViewadapter);

        View progress = vieww.findViewById(R.id.progress_loading);
        if (progress != null) progress.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(this).get(ChildVisitsViewModel.class);
        viewModel.getVisits().observe(getViewLifecycleOwner(), list -> {
            if (!isAdded() || list == null) return;
            visitList.clear();
            visitList.addAll(list);
            try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
            if (recyclerViewadapter.getItemCount() > 0){
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            if (progress != null) progress.setVisibility(View.GONE);
        });
        viewModel.refresh(childId);


        return vieww;

    }

}
