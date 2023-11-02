package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.MembersListAdapter;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeGroupFragmentMembers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeGroupFragmentMembers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView members_recyclerview;
    String username, password;
    MembersListAdapter memberListAdapter;
    ArrayList<MembersModel> listMembers;

    public WeGroupFragmentMembers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeGroupFragmentMembers.
     */
    // TODO: Rename and change types and number of parameters
    public static WeGroupFragmentMembers newInstance(String param1, String param2) {
        WeGroupFragmentMembers fragment = new WeGroupFragmentMembers();
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
        View rootView = inflater.inflate(R.layout.fragment_we_group_members, container, false);
        listMembers = new ArrayList<>();
        members_recyclerview = rootView.findViewById(R.id.viewGroupMembers);


        if (getArguments() != null) {
            String groupId = getArguments().getString("groupId");
            listMembers.addAll(WeGroupMembersDao.getWeGroupMembersByGroupId(groupId));
            memberListAdapter = new MembersListAdapter(getContext(), listMembers);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            members_recyclerview.setLayoutManager(layoutManager);
            members_recyclerview.setAdapter(memberListAdapter);
        }
        return rootView;
    }
}