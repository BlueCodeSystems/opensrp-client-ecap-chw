package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.PmctChildMonitoringAdapter;
import com.bluecodeltd.ecap.chw.dao.ChildMonitoringDao;
import com.bluecodeltd.ecap.chw.model.ChildMonitoringModel;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;

import java.util.ArrayList;
import java.util.HashMap;

public class PmctChildDbsMonitoringFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<ChildMonitoringModel> assessmentList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView visitCount;

    public PmctChildDbsMonitoringFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pmct_child_dbs_monitoring, container, false);

        HashMap<String, PmtctChildModel> mymap = ((HeiDetailsActivity) requireActivity()).getClientDetails();

        String uniqueId = null;

        if (mymap != null) {
            PmtctChildModel childDetails = mymap.get("client");
            if (childDetails != null) {
                uniqueId = childDetails.getUnique_id();
            }
        }

        recyclerView = view.findViewById(R.id.visitrecyclerView);
        linearLayout = view.findViewById(R.id.visit_container);
        visitCount = view.findViewById(R.id.tv_visit_count);

        assessmentList.clear();
        assessmentList.addAll(ChildMonitoringDao.getPmctChildMonitoringListDBS(uniqueId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new PmctChildMonitoringAdapter(getContext(), assessmentList);
        recyclerView.setAdapter(recyclerViewadapter);

        int count = recyclerViewadapter != null ? recyclerViewadapter.getItemCount() : 0;
        if (visitCount != null) {
            visitCount.setText(count + (count == 1 ? " visit" : " visits"));
        }

        if (count > 0){
            linearLayout.setVisibility(View.GONE);
        }

        return view;
    }
}