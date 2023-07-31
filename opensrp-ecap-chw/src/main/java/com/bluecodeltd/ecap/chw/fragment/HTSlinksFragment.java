package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HTSDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.HTSlinksAdapter;
import com.bluecodeltd.ecap.chw.dao.HTSLinksDao;
import com.bluecodeltd.ecap.chw.model.HIVTestingServiceModel;
import com.bluecodeltd.ecap.chw.model.HTSlinksModel;

import java.util.ArrayList;
import java.util.HashMap;

public class HTSlinksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HTSlinksFragment() {
        // Required empty public constructor
    }

    public static HTSlinksFragment newInstance(String param1, String param2) {
        HTSlinksFragment fragment = new HTSlinksFragment();
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

    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    TextView linkText;
    private ArrayList<HTSlinksModel> htsLinksModel = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_h_t_slinks, container, false);

        recyclerView = view.findViewById(R.id.hts_links);
        linkText = view.findViewById(R.id.hts_text);

        htsLinksModel.clear();
        HashMap<String, HIVTestingServiceModel> mymap = ((HTSDetailsActivity) requireActivity()).getLinkID();
        HIVTestingServiceModel htsModel = mymap.get("client");
        String id = htsModel.getClient_number();

        htsLinksModel.addAll(HTSLinksDao.getHTSLinks(id));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new HTSlinksAdapter(getContext(), htsLinksModel);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        if (recyclerViewAdapter.getItemCount() > 0){

            linkText.setVisibility(View.VISIBLE);
        }

        return view;
    }
}