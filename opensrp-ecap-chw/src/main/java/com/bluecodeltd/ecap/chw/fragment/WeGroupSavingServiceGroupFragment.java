package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.MembersListAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberSavingsAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSavingDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupSavingServiceGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupSavingServiceGroupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView savings_recyclerview;
    WeGroupMemberSavingsAdapter savingsListAdapter;
    ArrayList<WeGroupMemberSavings> listSavings;

    public WeGroupSavingServiceGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupSavingServiceGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupSavingServiceGroupFragment newInstance(String param1, String param2) {
        WeGroupSavingServiceGroupFragment fragment = new WeGroupSavingServiceGroupFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_we_group_saving_service_group, container, false);
        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

        listSavings = new ArrayList<>();
        savings_recyclerview = view.findViewById(R.id.viewGroupSavings);

        listSavings.addAll(WeGroupMemberSavingDao.getWeGroupMembersSavingsByGroupId(groupModel.getGroup_id()));
        savingsListAdapter = new WeGroupMemberSavingsAdapter(getContext(), listSavings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        savings_recyclerview.setLayoutManager(layoutManager);
        savings_recyclerview.setAdapter(savingsListAdapter);

        return view;
    }
}