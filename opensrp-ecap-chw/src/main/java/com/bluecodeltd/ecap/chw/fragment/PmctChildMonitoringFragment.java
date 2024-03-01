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
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.PmctChildMonitoringAdapter;
import com.bluecodeltd.ecap.chw.dao.PtmctMotherMonitoringDao;
import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PmctChildMonitoringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PmctChildMonitoringFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PmctChildMonitoringFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<PtmctMotherMonitoringModel> assessmentList = new ArrayList<>();
    private LinearLayout linearLayout;
    View vieww;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PmctChildMonitoringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PmctChildMonitoringFragment newInstance(String param1, String param2) {
        PmctChildMonitoringFragment fragment = new PmctChildMonitoringFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vieww = inflater.inflate(R.layout.fragment_pmct_child_monitoring, container, false);

        HashMap<String, PtmctMotherMonitoringModel> mymap = ((HeiDetailsActivity) requireActivity()).getClientDetails();

// Initialize motherDetails as null.
        PtmctMotherMonitoringModel motherDetails = null;

        String uniqueId = null;


        if (mymap != null) {
            motherDetails = mymap.get("client");

            if (motherDetails != null) {
                uniqueId = motherDetails.getUnique_id();

                if (uniqueId == null || uniqueId.isEmpty()) {
                }
            } else {

            }
        } else {

        }


        recyclerView = vieww.findViewById(R.id.visitrecyclerView);
        linearLayout = vieww.findViewById(R.id.visit_container);

        assessmentList.clear();

        assessmentList.addAll(PtmctMotherMonitoringDao.getChildMonitoring(uniqueId));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new PmctChildMonitoringAdapter( getContext(), assessmentList);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }


        return vieww;

    }
}