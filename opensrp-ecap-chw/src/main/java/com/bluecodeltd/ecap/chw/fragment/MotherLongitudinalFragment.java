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
import com.bluecodeltd.ecap.chw.adapter.MotherLongitudinalAdapter;
import com.bluecodeltd.ecap.chw.dao.MotherLongitudinalFollowUpDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentMotherLongitudinalBinding;
import com.bluecodeltd.ecap.chw.model.MotherLongitudinalFollowUpModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.jetbrains.annotations.NotNull;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotherLongitudinalFragment extends Fragment {

    private FragmentMotherLongitudinalBinding binding;
    private RecyclerView recyclerView;
    private View emptyView;

    public static MotherLongitudinalFragment newInstance() {
        return new MotherLongitudinalFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMotherLongitudinalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;
        final View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        String baseEntityId = null;
        try {
            HashMap<String, CommonPersonObjectClient> map = ((MotherDetail) requireActivity()).getData();
            CommonPersonObjectClient mother = map != null ? map.get("mother") : null;
            if (mother != null) {
                baseEntityId = mother.getCaseId();
            }
        } catch (Exception ignored) {
        }

        final String finalBaseEntityId = baseEntityId;
        Threading.io(() -> {
            List<MotherLongitudinalFollowUpModel> list = finalBaseEntityId != null
                    ? MotherLongitudinalFollowUpDao.listByBaseEntityId(finalBaseEntityId)
                    : new ArrayList<>();
            final List<MotherLongitudinalFollowUpModel> items = list != null ? list : new ArrayList<>();
            Threading.main(() -> {
                if (!isAdded()) return;
                setupList(items);
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private void setupList(List<MotherLongitudinalFollowUpModel> items) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new MotherLongitudinalAdapter(requireContext(), items));
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

