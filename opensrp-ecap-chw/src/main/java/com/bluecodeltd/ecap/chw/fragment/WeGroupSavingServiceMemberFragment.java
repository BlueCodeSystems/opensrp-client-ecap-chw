package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberSavingsAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberSavingsForMemberAdapter;
import com.bluecodeltd.ecap.chw.adapter.WeGroupMemberSocialForMemberAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSavingDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupSavingServiceMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupSavingServiceMemberFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeGroupSavingServiceMemberFragment() {
        // Required empty public constructor
    }
    RecyclerView savings_recyclerview;
    WeGroupMemberSavingsForMemberAdapter savingsListAdapter;
    ArrayList<WeGroupMemberSavings> listSavings;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupSavingServiceMemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupSavingServiceMemberFragment newInstance(String param1, String param2) {
        WeGroupSavingServiceMemberFragment fragment = new WeGroupSavingServiceMemberFragment();
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
        View view = inflater.inflate(R.layout.fragment_we_group_saving_service_member, container, false);
//        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
//        WeGroupModel groupModel = mymap.get("groupID");
        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
        MembersModel membersModel = mymap.get("uniqueID");

        listSavings = new ArrayList<>();
        savings_recyclerview = view.findViewById(R.id.viewGroupSavings);

        TextView infoTxt = view.findViewById(R.id.infoTxt);

        listSavings.addAll(WeGroupMemberSavingDao.getWeGroupMembersSavingsByUniqueId(membersModel.getUnique_id()));

// Check if the list is null or empty
        if (listSavings == null || listSavings.isEmpty()) {
            savings_recyclerview.setVisibility(View.GONE);
            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No savings entries have been added.");
        } else {
            savings_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            savingsListAdapter = new WeGroupMemberSavingsForMemberAdapter(getContext(), listSavings);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            savings_recyclerview.setLayoutManager(layoutManager);
            savings_recyclerview.setAdapter(savingsListAdapter);
        }

        return view;
    }

}