package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.activity.MotherDetail;
import com.bluecodeltd.ecap.chw.adapter.MotherPostnatalAdapter;
import com.bluecodeltd.ecap.chw.dao.MotherPostnatalCareDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentMotherPostnatalBinding;
import com.bluecodeltd.ecap.chw.model.MotherPostnatalCareModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.jetbrains.annotations.NotNull;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotherPostnatalFragment extends Fragment {

    private FragmentMotherPostnatalBinding binding;
    private RecyclerView recyclerView;
    private View emptyView;
    private MotherPostnatalAdapter adapter;
    private String householdId;

    public static MotherPostnatalFragment newInstance() {
        return new MotherPostnatalFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMotherPostnatalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;
        final View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        householdId = null;
        try {
            HashMap<String, CommonPersonObjectClient> map = ((MotherDetail) requireActivity()).getData();
            CommonPersonObjectClient mother = map != null ? map.get("mother") : null;
            if (mother != null) {
                householdId = mother.getColumnmaps().get("household_id");
            }
        } catch (Exception ignored) {
        }

        loadVisits(progress);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        final View progress = binding != null ? binding.progressLoading : null;
        if (progress != null) progress.setVisibility(View.VISIBLE);
        loadVisits(progress);
    }

    private void loadVisits(View progress) {
        final String finalHouseholdId = householdId;
        Threading.io(() -> {
            List<MotherPostnatalCareModel> list = finalHouseholdId != null
                    ? MotherPostnatalCareDao.listByHouseholdId(finalHouseholdId)
                    : new ArrayList<>();
            final List<MotherPostnatalCareModel> items = list != null ? list : new ArrayList<>();
            Threading.main(() -> {
                if (!isAdded()) return;
                 if (adapter == null) {
                     setupList(items);
                 } else {
                     adapter.setItems(items);
                     emptyView.setVisibility(items.size() > 0 ? View.GONE : View.VISIBLE);
                 }
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });
    }

    private void setupList(List<MotherPostnatalCareModel> items) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MotherPostnatalAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
