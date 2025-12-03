package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.MotherDetail;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public class MotherOverviewFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentMotherOverviewBinding binding;

    TextView txtHouseholdId, txtAddress, txtPhone, txtTreatment, txtArt;
    FloatingActionButton fab;
    CommonPersonObjectClient mother;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentMotherOverviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        txtHouseholdId = binding.hhId;
        txtAddress = binding.pAddress;
        txtPhone = binding.phone;
        txtTreatment = binding.treatment;
        txtArt = binding.artNumber;

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

//


        return view;

   }

    public void setViews(){

        HashMap<String, CommonPersonObjectClient> mymap = ( (MotherDetail) requireActivity()).getData();
        mother = mymap.get("mother");

        txtHouseholdId.setText(mother.getColumnmaps().get("household_id"));
        txtAddress.setText(mother.getColumnmaps().get("homeaddress"));
        txtPhone.setText(mother.getColumnmaps().get("caregiver_phone"));
        txtTreatment.setText(mother.getColumnmaps().get("active_on_treatment"));
        txtArt.setText(mother.getColumnmaps().get("caregiver_art_number"));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
