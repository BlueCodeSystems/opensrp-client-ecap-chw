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
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.CasePlanAdapter;
import com.bluecodeltd.ecap.chw.adapter.ChildrenAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.util.Threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildCasePlanFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<CasePlanModel> casePlanList = new ArrayList<>();
    private LinearLayout linearLayout;

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_childcaseplans, container, false);

        String childId  = ((IndexDetailsActivity) requireActivity()).uniqueId;
        String hivStatus = ((IndexDetailsActivity) requireActivity()).is_hiv_positive;

        recyclerView = view.findViewById(R.id.planrecyclerView);
        linearLayout = view.findViewById(R.id.visit_container);

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new CasePlanAdapter(casePlanList, getContext(), hivStatus);
        recyclerView.setAdapter(recyclerViewadapter);

        // Reading case plans on the main thread blocked on the DB lock while sync ran (ANR).
        // Load them in the background and fill the adapter when they arrive.
        Threading.io(() -> {
            List<CasePlanModel> plans = IndexPersonDao.getCasePlansById(childId);
            Threading.main(() -> {
                if (!isAdded() || getView() != view) return;
                casePlanList.clear();
                if (plans != null) casePlanList.addAll(plans);
                recyclerViewadapter.notifyDataSetChanged();
                linearLayout.setVisibility(casePlanList.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });

        return view;

    }
}
