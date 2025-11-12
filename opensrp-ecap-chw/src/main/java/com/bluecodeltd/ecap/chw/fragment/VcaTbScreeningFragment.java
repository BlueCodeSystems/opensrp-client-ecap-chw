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

import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.adapter.TbScreeningAdapter;
import com.bluecodeltd.ecap.chw.dao.TbScreeningDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentVcaTbScreeningBinding;
import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.jetbrains.annotations.NotNull;
import org.smartregister.client.utils.domain.Form;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import org.smartregister.util.FormUtils;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VcaTbScreeningFragment extends Fragment {

    private FragmentVcaTbScreeningBinding binding;
    private RecyclerView recyclerView;
    private View emptyView;

    public static VcaTbScreeningFragment newInstance() { return new VcaTbScreeningFragment(); }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVcaTbScreeningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.visitrecyclerView;
        emptyView = binding.visitContainer;

        final View progress = binding.progressLoading;
        if (progress != null) progress.setVisibility(View.VISIBLE);

        String vcaId = ((IndexDetailsActivity) requireActivity()).uniqueId;
        String vcaName = null;
        try {
            com.bluecodeltd.ecap.chw.model.VcaScreeningModel v = ((IndexDetailsActivity) requireActivity()).indexVCA;
            if (v != null) {
                String first = (v.getAdolescent_first_name() != null && !v.getAdolescent_first_name().trim().isEmpty()) ? v.getAdolescent_first_name() : v.getFirst_name();
                String last = (v.getAdolescent_last_name() != null && !v.getAdolescent_last_name().trim().isEmpty()) ? v.getAdolescent_last_name() : v.getLast_name();
                vcaName = ((first != null ? first : "") + (last != null && !last.isEmpty() ? (" " + last) : "")).trim();
            }
        } catch (Exception ignored) { }

        String finalVcaName = vcaName;
        Threading.io(() -> {
            List<TbScreeningModel> list = TbScreeningDao.listByVcaId(vcaId);
            final List<TbScreeningModel> items = list != null ? list : new ArrayList<>();
            Threading.main(() -> {
                if (!isAdded()) return;
                setupList(items, vcaId, finalVcaName);
                if (progress != null) progress.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private void setupList(List<TbScreeningModel> items, String vcaId, String vcaName) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new TbScreeningAdapter(requireContext(), items, new TbScreeningAdapter.Listener() {
            @Override
            public void onAddOutcome(TbScreeningModel item) { openTbForm("tb_screening_outcome", item, vcaId, vcaName); }
            @Override
            public void onEdit(TbScreeningModel item) { openTbForm("tb_screening", item, vcaId, vcaName); }
        }));
        emptyView.setVisibility(items != null && items.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void openTbForm(String formName, TbScreeningModel item, String vcaId, String vcaName) {
        try {
            FormUtils formUtils = new FormUtils(requireContext());
            org.json.JSONObject form = formUtils.getFormJson(formName);
            try {
                String title = (vcaName != null ? vcaName : "") + " : TB";
                form.getJSONObject("step1").put("title", title);
                org.json.JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    org.json.JSONObject f = fields.getJSONObject(i);
                    String key = f.optString("key");
                    if ("unique_id".equals(key)) {
                        f.put("value", vcaId);
                    } else if ("unique_tb_id".equals(key)) {
                        String val = item != null ? item.getUnique_tb_id() : null;
                        if (val == null || val.trim().isEmpty()) val = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
                        f.put("value", val);
                        form.put("entity_id", val);
                    }
                }
                if ("tb_screening_outcome".equals(formName)) {
                    form.remove(JsonFormConstants.ENCOUNTER_TYPE);
                    form.put(JsonFormConstants.ENCOUNTER_TYPE, "ECAPII TB Screening - Sections C and D");
                    try {
                        com.bluecodeltd.ecap.chw.model.TbScreeningModel latest = com.bluecodeltd.ecap.chw.dao.TbScreeningDao.getLatestByBaseEntityId(item.getBase_entity_id());
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
