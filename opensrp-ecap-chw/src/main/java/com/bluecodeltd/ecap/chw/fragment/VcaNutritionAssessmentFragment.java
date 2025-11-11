package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.NutritionAssessmentInterventionAdapter;
import com.bluecodeltd.ecap.chw.dao.NutritionAssessmentInterventionDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentVcaNutritionAssessmentBinding;
import com.bluecodeltd.ecap.chw.model.NutritionAssessmentInterventionModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VcaNutritionAssessmentFragment extends Fragment {

    private FragmentVcaNutritionAssessmentBinding binding;
    private RecyclerView recyclerView;
    private LinearLayout emptyView;

    public static VcaNutritionAssessmentFragment newInstance() {
        return new VcaNutritionAssessmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVcaNutritionAssessmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;

        String childId = ((IndexDetailsActivity) requireActivity()).uniqueId;
        View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        Threading.io(() -> {
            VcaScreeningModel screen = VCAScreeningDao.getVcaScreening(childId);
            boolean eligible = isUnderOrEqualFive(screen != null ? screen.getAdolescent_birthdate() : null);
            final List<NutritionAssessmentInterventionModel> items;
            if (eligible) {
                List<NutritionAssessmentInterventionModel> list = NutritionAssessmentInterventionDao.listByVcaId(childId);
                items = list != null ? list : new ArrayList<>();
            } else {
                items = new ArrayList<>();
            }
            Threading.main(() -> {
                if (!isAdded()) return;
                setupList(items);
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private void setupList(List<NutritionAssessmentInterventionModel> items) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new NutritionAssessmentInterventionAdapter(requireContext(), items));
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private boolean isUnderOrEqualFive(String birthdate) {
        if (birthdate == null || birthdate.trim().isEmpty()) return false;
        try {
            Date dob = new SimpleDateFormat("dd-MM-yyyy").parse(birthdate);
            Calendar dobCal = Calendar.getInstance();
            dobCal.setTime(dob);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) age--;
            return age <= 5;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

