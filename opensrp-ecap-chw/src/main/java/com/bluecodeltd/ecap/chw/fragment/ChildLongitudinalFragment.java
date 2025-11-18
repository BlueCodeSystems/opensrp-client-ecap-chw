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

import com.bluecodeltd.ecap.chw.activity.ChildNonPmtctDetail;
import com.bluecodeltd.ecap.chw.adapter.ChildLongitudinalAdapter;
import com.bluecodeltd.ecap.chw.dao.ChildLongitudinalFollowUpDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentChildLongitudinalBinding;
import com.bluecodeltd.ecap.chw.model.ChildLongitudinalFollowUpModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChildLongitudinalFragment extends Fragment {

    private FragmentChildLongitudinalBinding binding;
    private RecyclerView recyclerView;
    private View emptyView;

    public static ChildLongitudinalFragment newInstance() {
        return new ChildLongitudinalFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChildLongitudinalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;
        final View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        String baseEntityId = null;
        String householdId = null;
        String uniqueId = null;
        try {
            ChildNonPmtctDetail act = (ChildNonPmtctDetail) requireActivity();
            baseEntityId = act.getBaseEntityId();
            householdId = act.getHouseholdId();
            uniqueId = act.getUniqueId();
        } catch (Exception ignored) {
        }

        final String fBase = baseEntityId;
        final String fHousehold = householdId;
        final String fUnique = uniqueId;

        Threading.io(() -> {
            List<ChildLongitudinalFollowUpModel> list = fBase != null
                    ? ChildLongitudinalFollowUpDao.listByBaseEntityId(fBase)
                    : new ArrayList<>();
            final List<ChildLongitudinalFollowUpModel> items = list != null ? list : new ArrayList<>();
            Threading.main(() -> {
                if (!isAdded()) return;
                setupList(items, fHousehold, fUnique);
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private void setupList(List<ChildLongitudinalFollowUpModel> items, String householdId, String uniqueId) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ChildLongitudinalAdapter(requireContext(), items, householdId, uniqueId));
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
