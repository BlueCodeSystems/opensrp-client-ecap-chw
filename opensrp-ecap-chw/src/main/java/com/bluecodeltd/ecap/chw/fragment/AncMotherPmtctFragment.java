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
import com.bluecodeltd.ecap.chw.activity.MotherPmtctProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.AncMotherAdapter;
import com.bluecodeltd.ecap.chw.dao.PmctMotherAncDao;
import com.bluecodeltd.ecap.chw.model.PmctMotherAncModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AncMotherPmtctFragment extends Fragment {

    private RecyclerView recyclerView;
    private AncMotherAdapter recyclerViewadapter;
    private final List<PmctMotherAncModel> ancList = new ArrayList<>();
    private LinearLayout linearLayout;
    private View vieww;

    public AncMotherPmtctFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vieww = inflater.inflate(R.layout.fragment_mother_anc_pmtct, container, false);

        recyclerView = vieww.findViewById(R.id.visitrecyclerView);
        linearLayout = vieww.findViewById(R.id.visit_container);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshViews();

        return vieww;
    }

    public void refreshViews() {
        if (!isAdded() || getActivity() == null || recyclerView == null || linearLayout == null) return;

        HashMap<String, PtctMotherModel> mymap = ((MotherPmtctProfileActivity) requireActivity()).getClientDetails();

        String householdId = null;
        if (mymap != null) {
            PtctMotherModel motherDetails = mymap.get("client");
            if (motherDetails != null) {
                householdId = motherDetails.getHousehold_id();
            }
        }

        ancList.clear();
        if (householdId != null && !householdId.isEmpty()) {
            List<PmctMotherAncModel> records = PmctMotherAncDao.getPostnatalAncMother(householdId);
            if (records != null) ancList.addAll(records);
        }

        if (recyclerViewadapter == null) {
            recyclerViewadapter = new AncMotherAdapter(getContext(), ancList);
            recyclerView.setAdapter(recyclerViewadapter);
        } else {
            recyclerViewadapter.notifyDataSetChanged();
        }

        linearLayout.setVisibility(recyclerViewadapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }
}
