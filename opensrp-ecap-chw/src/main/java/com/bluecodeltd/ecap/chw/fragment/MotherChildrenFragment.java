package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.MotherDetail;
import com.bluecodeltd.ecap.chw.adapter.ChildrenAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.IndexMotherModel;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenState;
import com.bluecodeltd.ecap.chw.viewmodel.HouseholdChildrenViewModel;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import com.bluecodeltd.ecap.chw.util.Threading;

import timber.log.Timber;

public class MotherChildrenFragment extends Fragment {

    private com.bluecodeltd.ecap.chw.databinding.FragmentChildrenBinding binding;

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<Child> childList = new ArrayList<>();
    private HouseholdChildrenViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = com.bluecodeltd.ecap.chw.databinding.FragmentChildrenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        HashMap<String, CommonPersonObjectClient> mymap = ( (MotherDetail) requireActivity()).getData();

        CommonPersonObjectClient mother = mymap.get("mother");
        String houseId = (mother != null && mother.getColumnmaps() != null) ? mother.getColumnmaps().get("household_id") : null;
        if (houseId == null && mother != null && mother.getColumnmaps() != null) {
            houseId = mother.getColumnmaps().get("householdId");
        }
        if (houseId == null && mother != null && mother.getColumnmaps() != null) {
            houseId = mother.getColumnmaps().get("hh_id");
        }
        String motherBaseEntityId = (mother != null && mother.getColumnmaps() != null) ? mother.getColumnmaps().get("base_entity_id") : null;
        String motherSourceFrom = (mother != null && mother.getColumnmaps() != null) ? mother.getColumnmaps().get("source_from") : null;
        Timber.d("MotherChildrenFragment init: houseId=%s base_entity_id=%s source_from(col)=%s", houseId, motherBaseEntityId, motherSourceFrom);

        recyclerView = binding.recyclerView;

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Whether a child row may open the VCA profile can need two DB lookups. Reading them here on the
        // main thread blocked on the DB lock while sync or validation ran (ANR), so resolve it in the
        // background and attach the adapter once it is known. The children list loads in parallel.
        final String finalHouseId = houseId;
        Threading.io(() -> {
            boolean allow = resolveAllowVcaProfileNavigation(motherSourceFrom, motherBaseEntityId, finalHouseId);
            Threading.main(() -> {
                if (!isAdded() || binding == null || getView() != view) return;
                recyclerViewadapter = new ChildrenAdapter(childList, getContext(), "0", allow);
                recyclerView.setAdapter(recyclerViewadapter);
            });
        });

        View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(this).get(HouseholdChildrenViewModel.class);
        viewModel.getState().observe(getViewLifecycleOwner(), this::applyChildrenState);
        viewModel.refresh(houseId, true);


        return view;

    }

    private void applyChildrenState(HouseholdChildrenState state) {
        if (!isAdded() || state == null) return;
        childList.clear();
        if (state.getChildren() != null) childList.addAll(state.getChildren());
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
        View progress = (binding != null) ? binding.progressLoading : null;
        if (progress != null) progress.setVisibility(View.GONE);
    }

    /**
     * Children of a mother registered through VCA screening must not open the VCA profile. The mother's
     * column maps may not carry source_from, so fall back to her DB record by base_entity_id, then to any
     * mother record in the household. Runs off the main thread.
     */
    private static boolean resolveAllowVcaProfileNavigation(String motherSourceFrom, String motherBaseEntityId, String houseId) {
        if (isVcaScreening(motherSourceFrom)) {
            return false;
        }
        try {
            IndexMotherModel indexMother = null;
            if (motherBaseEntityId != null && !motherBaseEntityId.trim().isEmpty()) {
                indexMother = IndexMotherDao.getIndexMotherByBaseEntityId(motherBaseEntityId);
            }
            String dbSourceFrom = indexMother != null ? indexMother.getSource_from() : null;
            Timber.d("MotherChildrenFragment db(by base_entity_id): source_from=%s", dbSourceFrom);
            if (isVcaScreening(dbSourceFrom)) {
                return false;
            }
            if (houseId != null && !houseId.trim().isEmpty()) {
                java.util.List<IndexMotherModel> mothers = IndexMotherDao.getIndexMothersByHouseholdId(houseId);
                Timber.d("MotherChildrenFragment db(by household): mothers=%s", mothers != null ? mothers.size() : 0);
                if (mothers != null) {
                    for (IndexMotherModel m : mothers) {
                        if (m != null && isVcaScreening(m.getSource_from())) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return true;
    }

    private static boolean isVcaScreening(String sourceFrom) {
        return sourceFrom != null && sourceFrom.trim().equalsIgnoreCase("vca_screening");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
