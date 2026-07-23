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

import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.adapter.TbScreeningCaregiverAdapter;
import com.bluecodeltd.ecap.chw.dao.TbScreeningCaregiverDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentCaregiverTbScreeningBinding;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.TbScreeningCaregiverModel;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaregiverTbScreeningFragment extends Fragment {

    private FragmentCaregiverTbScreeningBinding binding;
    private RecyclerView recyclerView;
    private View emptyView;

    public static CaregiverTbScreeningFragment newInstance() { return new CaregiverTbScreeningFragment(); }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCaregiverTbScreeningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;

        final View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        HouseholdDetails act = (HouseholdDetails) requireActivity();
        Household house = act.house;
        String caregiverId = house != null ? house.getHousehold_id() : null;
        String caregiverName = house != null ? house.getCaregiver_name() : null;

        Threading.io(() -> {
            List<TbScreeningCaregiverModel> list = TbScreeningCaregiverDao.listByCaregiverId(caregiverId);
            final List<TbScreeningCaregiverModel> items = list != null ? list : new ArrayList<>();
            Threading.main(() -> {
                if (!isAdded()) return;
                setupList(items, caregiverId, caregiverName, house != null ? house.getBase_entity_id() : null);
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private void setupList(List<TbScreeningCaregiverModel> items, String caregiverId, String caregiverName, String baseEntityId) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new TbScreeningCaregiverAdapter(requireContext(), items, new TbScreeningCaregiverAdapter.Listener() {
            @Override
            public void onAddOutcome(TbScreeningCaregiverModel item) { openTbForm("tb_screening_outcome_caregiver", item, caregiverId, caregiverName, baseEntityId); }
            @Override
            public void onEdit(TbScreeningCaregiverModel item) { openTbForm("tb_screening_caregiver", item, caregiverId, caregiverName, baseEntityId); }
        }));
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void openTbForm(String formName, TbScreeningCaregiverModel item, String caregiverId, String caregiverName, String baseEntityId) {
        try {
            FormUtils formUtils = new FormUtils(requireContext());
            org.json.JSONObject form = formUtils.getFormJson(formName);
            try {
                String title = (caregiverName != null ? caregiverName : "") + " : TB";
                form.getJSONObject("step1").put("title", title);
                org.json.JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    org.json.JSONObject f = fields.getJSONObject(i);
                    String key = f.optString("key");
                    if ("household_id".equals(key)) {
                        f.put("value", caregiverId);
                    } else if ("unique_tb_id".equals(key)) {
                        String val = item != null ? item.getUnique_tb_id() : null;
                        if (val == null || val.trim().isEmpty()) val = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
                        f.put("value", val);
                    }
                }
                String entityId = baseEntityId;
                if (entityId == null || entityId.trim().isEmpty()) entityId = caregiverId;
                form.put("entity_id", entityId);
                if ("tb_screening_outcome_caregiver".equals(formName)) {
                    form.remove(JsonFormConstants.ENCOUNTER_TYPE);
                    form.put(JsonFormConstants.ENCOUNTER_TYPE, "TB Screening Caregiver");
                    try {
                        TbScreeningCaregiverModel latest = TbScreeningCaregiverDao.getLatestByBaseEntityId(entityId);
                        if (latest != null) {
                            CoreJsonFormUtils.populateJsonForm(form, new ObjectMapper().convertValue(latest, Map.class));
                        }
                    } catch (Exception ignored) {}
                } else if (item != null) {
                    CoreJsonFormUtils.populateJsonForm(form, new ObjectMapper().convertValue(item, Map.class));
                }
            } catch (Exception ignored) {}

            Form f = new Form();
            f.setWizard(false);
            f.setName(getString(org.smartregister.chw.core.R.string.child_details));
            f.setHideSaveLabel(true);
            f.setNextLabel(getString(com.bluecodeltd.ecap.chw.R.string.next));
            f.setPreviousLabel(getString(com.bluecodeltd.ecap.chw.R.string.previous));
            f.setSaveLabel(getString(com.bluecodeltd.ecap.chw.R.string.submit));
            f.setNavigationBackground(com.bluecodeltd.ecap.chw.R.color.primary);
            android.content.Intent intent = new android.content.Intent(requireActivity(), org.smartregister.family.util.Utils.metadata().familyFormActivity);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, f);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
            requireActivity().startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) { }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
