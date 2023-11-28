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
import com.bluecodeltd.ecap.chw.adapter.WeGroupSocialDiscussionsAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMemberSocialFundDao;
import com.bluecodeltd.ecap.chw.dao.WeGroupSocialDiscussionsDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;
import com.bluecodeltd.ecap.chw.model.WeGroupSocialDiscussionsModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupSocialDiscussionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupSocialDiscussionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView social_discussions_recyclerview;
    WeGroupSocialDiscussionsAdapter socialDiscussionsListAdapter;
    ArrayList<WeGroupSocialDiscussionsModel> listSocialDiscussions;

    public WeGroupSocialDiscussionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupSocialDiscussionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupSocialDiscussionsFragment newInstance(String param1, String param2) {
        WeGroupSocialDiscussionsFragment fragment = new WeGroupSocialDiscussionsFragment();
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
        View view = inflater.inflate(R.layout.fragment_we_group_social_discussions, container, false);

        HashMap<String, WeGroupModel> mymap = ((WeGroupProfileActivity) requireActivity()).getGroupData();
        WeGroupModel groupModel = mymap.get("groupID");

//        HashMap<String, MembersModel> mymap = ((WeGroupMemberProfileActivity) requireActivity()).getData();
//        MembersModel membersModel = mymap.get("uniqueID");

        listSocialDiscussions = new ArrayList<>();
        social_discussions_recyclerview = view.findViewById(R.id.viewGroupSocialDiscussions);

        TextView infoTxt = view.findViewById(R.id.infoTxt);

        listSocialDiscussions.addAll(WeGroupSocialDiscussionsDao.getWeGroupSocialDiscussionsByGroup(groupModel.getGroup_id()));


        if (listSocialDiscussions == null || listSocialDiscussions.isEmpty()) {
            social_discussions_recyclerview.setVisibility(View.GONE);
            infoTxt.setVisibility(View.VISIBLE);
            infoTxt.setText("No social discussions entries have been added.");
        } else {
            social_discussions_recyclerview.setVisibility(View.VISIBLE);
            infoTxt.setVisibility(View.GONE);
            socialDiscussionsListAdapter = new WeGroupSocialDiscussionsAdapter(getContext(), listSocialDiscussions);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            social_discussions_recyclerview.setLayoutManager(layoutManager);
            social_discussions_recyclerview.setAdapter(socialDiscussionsListAdapter);
        };



        return view;
    }
}