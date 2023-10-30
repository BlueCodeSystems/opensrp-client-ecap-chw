package com.bluecodeltd.ecap.chw.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ServicesModeAdapter;
import com.bluecodeltd.ecap.chw.model.ServicesModel;

import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {

    private RecyclerView servicesRecyclerView;
    private ServicesModeAdapter servicesModelAdapter;
    private List<ServicesModel> servicesList;

    public ServicesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView);
        servicesList = createDummyData();

        servicesModelAdapter = new ServicesModeAdapter(requireContext(), servicesList);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        servicesRecyclerView.setAdapter(servicesModelAdapter);

        return view;
    }

    private List<ServicesModel> createDummyData() {
        List<ServicesModel> addService = new ArrayList<>();
        addService.add(new ServicesModel("Saving", "User Saving: K 0", "Group Saving: K 0"));
        addService.add(new ServicesModel("Loan", "User Loan: K 0", "Group Loan: K 0"));
        addService.add(new ServicesModel("Fine", "User Fine: K 0", "Group Fine: K 0"));
        addService.add(new ServicesModel("Social Fund", "User Social Fund: K 0", "Group Social Fund: K 0"));
        addService.add(new ServicesModel("Repayment", "User Repayment: K 0", "Group Repayment: K 0"));
        addService.add(new ServicesModel("IGA", "User IGA: K 0", "Group IGA: K 0"));
        return addService;
    }
}
