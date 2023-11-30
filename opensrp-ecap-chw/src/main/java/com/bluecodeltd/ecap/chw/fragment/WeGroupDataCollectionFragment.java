package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.MembersListAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupDataCollectionAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupDataCollectionDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.WeGroupDataCollectionModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupDataCollectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupDataCollectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeGroupDataCollectionFragment() {
        // Required empty public constructor
    }
    RecyclerView reports;
    WeGroupDataCollectionAdapter reportAdapter;
    ArrayList<WeGroupDataCollectionModel> listReports;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupDataCollectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupDataCollectionFragment newInstance(String param1, String param2) {
        WeGroupDataCollectionFragment fragment = new WeGroupDataCollectionFragment();
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

    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_we_group_data_collection, container, false);

        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

            listReports = new ArrayList<>();
            reports = view.findViewById(R.id.recyclerReport);
            listReports.addAll(WeGroupDataCollectionDao.getWeGroupDataCollectionId(groupModel.getGroup_id()));
            reportAdapter = new WeGroupDataCollectionAdapter(getContext(), listReports);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            reports.setLayoutManager(layoutManager);
            reports.setAdapter(reportAdapter);
            reportAdapter.setOnDataUpdateListener(() -> requireActivity().runOnUiThread(() -> {
                refreshFragment();
            }));



        return view;
    }

    public void refreshFragment() {
        FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }
}