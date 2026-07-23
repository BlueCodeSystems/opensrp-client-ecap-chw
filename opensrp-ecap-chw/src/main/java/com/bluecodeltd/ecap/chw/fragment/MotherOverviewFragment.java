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
import com.bluecodeltd.ecap.chw.model.IndexMotherModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Locale;

public class MotherOverviewFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentMotherOverviewBinding binding;

    TextView txtHouseholdId, txtAddress, txtPhone, txtTreatment, txtArt,
            txtHivStatus, txtFacility, txtScreeningDate, txtPregnant, txtBreastfeeding;
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
        txtHivStatus = binding.hivStatus;
        txtFacility = binding.facility;
        txtScreeningDate = binding.screeningDate;
        txtPregnant = binding.pregnantStatus;
        txtBreastfeeding = binding.breastfeedingStatus;

        fab = getActivity().findViewById(R.id.fabx);

        setViews();

//


        return view;

   }

    public void setViews(){
        MotherDetail activity = (MotherDetail) requireActivity();

        HashMap<String, CommonPersonObjectClient> mymap = activity.getData();
        mother = mymap != null ? mymap.get("mother") : null;

        IndexMotherModel motherIndex = activity.getMotherIndex();
        Household family = activity.getFamily();

        String householdId = null;
        try {
            householdId = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("household_id") : null;
        } catch (Exception ignored) { }
        txtHouseholdId.setText(householdId);

        String address = motherIndex != null ? motherIndex.getHomeaddress() : null;
        if ((address == null || address.isEmpty()) && family != null) address = family.getHomeaddress();
        if ((address == null || address.isEmpty())) {
            try { address = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("homeaddress") : null; } catch (Exception ignored) { }
        }
        txtAddress.setText(address);

        String phone = motherIndex != null ? motherIndex.getCaregiver_phone() : null;
        if ((phone == null || phone.isEmpty()) && family != null) phone = family.getCaregiver_phone();
        if ((phone == null || phone.isEmpty())) {
            try { phone = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("caregiver_phone") : null; } catch (Exception ignored) { }
        }
        txtPhone.setText(phone);

        String treatment = motherIndex != null ? motherIndex.getActive_on_treatment() : null;
        if ((treatment == null || treatment.isEmpty()) && family != null) treatment = family.getActive_on_treatment();
        if ((treatment == null || treatment.isEmpty())) {
            try { treatment = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("active_on_treatment") : null; } catch (Exception ignored) { }
        }
        txtTreatment.setText(treatment);

        String artNumber = motherIndex != null ? motherIndex.getCaregiver_art_number() : null;
        if ((artNumber == null || artNumber.isEmpty()) && family != null) artNumber = family.getCaregiver_art_number();
        if ((artNumber == null || artNumber.isEmpty())) {
            try { artNumber = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("caregiver_art_number") : null; } catch (Exception ignored) { }
        }
        txtArt.setText(artNumber);

        String hivStatus = motherIndex != null ? motherIndex.getCaregiver_hiv_status() : null;
        if ((hivStatus == null || hivStatus.isEmpty()) && family != null) hivStatus = family.getCaregiver_hiv_status();
        if ((hivStatus == null || hivStatus.isEmpty())) {
            try { hivStatus = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("caregiver_hiv_status") : null; } catch (Exception ignored) { }
        }
        txtHivStatus.setText(hivStatus);
        applyHivStatusVisibility(hivStatus);

        String facility = motherIndex != null ? motherIndex.getFacility() : null;
        if ((facility == null || facility.isEmpty()) && family != null) facility = family.getFacility();
        if ((facility == null || facility.isEmpty())) {
            try { facility = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("facility") : null; } catch (Exception ignored) { }
        }
        txtFacility.setText(facility);

        String screeningDate = motherIndex != null ? motherIndex.getMother_screening_date() : null;
        if ((screeningDate == null || screeningDate.isEmpty())) {
            try { screeningDate = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("mother_screening_date") : null; } catch (Exception ignored) { }
        }
        txtScreeningDate.setText(screeningDate);

        String pregnant = motherIndex != null ? motherIndex.getPregnant_mother() : null;
        if ((pregnant == null || pregnant.isEmpty())) {
            try { pregnant = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("mother_pregnant") : null; } catch (Exception ignored) { }
        }
        txtPregnant.setText(pregnant);

        String breastfeeding = motherIndex != null ? motherIndex.getMother_breastfeeding() : null;
        if ((breastfeeding == null || breastfeeding.isEmpty())) {
            try { breastfeeding = mother != null && mother.getColumnmaps() != null ? mother.getColumnmaps().get("mother_breastfeeding") : null; } catch (Exception ignored) { }
        }
        txtBreastfeeding.setText(breastfeeding);
    }

    private void applyHivStatusVisibility(@Nullable String hivStatus) {
        if (binding == null) return;

        boolean showPositiveOnly = isPositiveHivStatus(hivStatus);
        int visibility = showPositiveOnly ? View.VISIBLE : View.GONE;

        binding.treatmentRow.setVisibility(visibility);
        binding.artRow.setVisibility(visibility);

        if (!showPositiveOnly) {
            txtTreatment.setText(null);
            txtArt.setText(null);
        }
    }

    private boolean isPositiveHivStatus(@Nullable String hivStatus) {
        if (hivStatus == null) return false;
        String normalized = hivStatus.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("positive")
                || normalized.equals("pos")
                || normalized.equals("reactive")
                || normalized.equals("hiv positive")
                || normalized.equals("hiv-positive");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            try { setViews(); } catch (Exception ignored) { }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
