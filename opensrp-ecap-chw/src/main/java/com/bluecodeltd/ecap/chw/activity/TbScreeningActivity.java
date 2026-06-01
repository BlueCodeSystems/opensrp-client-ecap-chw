package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.TbScreeningDao;
// removed outcomes list usage
import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
// removed outcomes list usage
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class TbScreeningActivity extends AppCompatActivity {
    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    public static final String EXTRA_UNIQUE_ID = "unique_id";
    public static final String EXTRA_VCA_NAME = "vca_name";

    private String baseEntityId;
    private String uniqueId;
    private String vcaName;

    private RecyclerView rvScreenings;
    private View emptyView;
    private com.bluecodeltd.ecap.chw.adapter.TbScreeningAdapter screeningAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tb_screening);

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        uniqueId = getIntent().getStringExtra(EXTRA_UNIQUE_ID);
        vcaName = getIntent().getStringExtra(EXTRA_VCA_NAME);

        TextView title = findViewById(R.id.title);
        if (vcaName != null) title.setText("TB Screening · " + vcaName);

        Button btnNew = findViewById(R.id.btnNewScreening);
        btnNew.setOnClickListener(v -> startTbScreeningForm());

        rvScreenings = findViewById(R.id.rvScreenings);
        emptyView = findViewById(R.id.emptyView);
        rvScreenings.setLayoutManager(new LinearLayoutManager(this));

        screeningAdapter = new com.bluecodeltd.ecap.chw.adapter.TbScreeningAdapter(
                this,
                new ArrayList<>(),
                new com.bluecodeltd.ecap.chw.adapter.TbScreeningAdapter.Listener() {
                    @Override public void onAddOutcome(com.bluecodeltd.ecap.chw.model.TbScreeningModel item) {
                        startTbOutcomeForm(item.getUnique_tb_id());
                    }
                    @Override public void onEdit(com.bluecodeltd.ecap.chw.model.TbScreeningModel item) {
                        openForm("tb_screening", item.getUnique_tb_id());
                    }
                }
        );
        rvScreenings.setAdapter(screeningAdapter);

        // no outcomes list
        // Auto-open form when requested (from fragment)
        try {
            String autoForm = getIntent().getStringExtra("open_form");
            String autoUniqueTbId = getIntent().getStringExtra("unique_tb_id");
            if (autoForm != null) {
                openForm(autoForm, autoUniqueTbId);
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadLists();
    }

    private void reloadLists() {
        List<TbScreeningModel> screenings = TbScreeningDao.listByVcaId(uniqueId);
        screeningAdapter.setItems(screenings);
        try {
            if (emptyView != null) emptyView.setVisibility((screenings == null || screenings.isEmpty()) ? View.VISIBLE : View.GONE);
        } catch (Exception ignored) {}

        // outcomes list removed
    }

    private void startTbScreeningForm() { openForm("tb_screening", org.smartregister.util.JsonFormUtils.generateRandomUUIDString()); }

    private void startTbOutcomeForm(@Nullable String uniqueTbId) { openForm("tb_screening_outcome", uniqueTbId); }

    private void openForm(String formName, @Nullable String uniqueTbId) {
        try {
            org.smartregister.util.FormUtils formUtils = new org.smartregister.util.FormUtils(this);
            JSONObject formToBeOpened = formUtils.getFormJson(formName);
            try {
                String title = (vcaName != null ? vcaName : "") + " : TB";
                formToBeOpened.getJSONObject("step1").put("title", title);
                // populate unique_id in first field when present
                JSONArray fields = formToBeOpened.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject f = fields.getJSONObject(i);
                    String key = f.optString("key");
                    if ("unique_id".equals(key)) {
                        f.put("value", uniqueId);
                    } else if ("unique_tb_id".equals(key)) {
                        String val = uniqueTbId;
                        if (val == null || val.trim().isEmpty()) val = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
                        f.put("value", val);
                    }
                }
            } catch (Exception ignored) {}
            // Ensure entity_id is base_entity_id (align with other adapters)
            try {
                if (baseEntityId != null && !baseEntityId.trim().isEmpty()) {
                    formToBeOpened.put("entity_id", baseEntityId);
                } else if (uniqueId != null && !uniqueId.trim().isEmpty()) {
                    formToBeOpened.put("entity_id", uniqueId);
                } else {
                    formToBeOpened.put("entity_id", org.smartregister.util.JsonFormUtils.generateRandomUUIDString());
                }
            } catch (Exception ignored) {}

            // launch
            Form form = new Form();
            form.setWizard(false);
            form.setName(getString(org.smartregister.chw.core.R.string.child_details));
            form.setHideSaveLabel(true);
            form.setNextLabel(getString(R.string.next));
            form.setPreviousLabel(getString(R.string.previous));
            form.setSaveLabel(getString(R.string.submit));
            form.setNavigationBackground(R.color.primary);
                        // Prefill from model when editing existing record (fallback)
            try {
                if (uniqueTbId != null && !uniqueTbId.trim().isEmpty()) {
                    com.bluecodeltd.ecap.chw.model.TbScreeningModel m = com.bluecodeltd.ecap.chw.dao.TbScreeningDao.getByUniqueTbId(uniqueTbId);
                    if (m != null) {
                        org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(formToBeOpened, new com.fasterxml.jackson.databind.ObjectMapper().convertValue(m, java.util.Map.class));
                    }
                }
            } catch (Exception ignored) {}Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, formToBeOpened.toString());
            startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
            Snackbar.make(rvScreenings, "Unable to open form", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK && data != null) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            boolean isEdit = false;
            try {
                JSONObject obj = new JSONObject(jsonString);
                isEdit = !obj.optString("entity_id").isEmpty();
            } catch (JSONException ignored) {}
            try {
                ChildIndexEventClient ec = processRegistration(jsonString);
                if (ec != null) saveRegistration(ec, isEdit);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public static class ChildIndexEventClient {
        private final Event event; private final Client client;
        public ChildIndexEventClient(Event e, Client c) { this.event = e; this.client = c; }
        public Event getEvent() { return event; }
        public Client getClient() { return client; }
    }

    private ChildIndexEventClient processRegistration(String jsonString) throws JSONException {
        JSONObject formJsonObject = new JSONObject(jsonString);
        String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
        String entityId = formJsonObject.optString("entity_id");
        if (entityId.isEmpty()) { entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString(); }
        JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);
        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

        switch (encounterType) {
            case "TB Screening": {
                FormTag tag = getFormTag();
                Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, tag, entityId,
                        encounterType, Constants.EcapClientTable.EC_TB_SCREENING);
                tagSyncMetadata(event);
                Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, tag, entityId);
                return new ChildIndexEventClient(event, client);
            }
            case "TB Screening Outcome": {
                FormTag tag = getFormTag();
                Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, tag, entityId,
                        encounterType, Constants.EcapClientTable.EC_TB_SCREENING);
                tagSyncMetadata(event);
                Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, tag, entityId);
                return new ChildIndexEventClient(event, client);
            }
        }
        return null;
    }

    private boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {
        Runnable runnable = () -> {
            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();
            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();
                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());
                    // Be defensive: treat missing existing client as new to avoid NPE on merge
                    if (isEditMode && existingClientJsonObject != null) {
                        JSONObject mergedClientJsonObject = org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }
                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);
                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                    runOnUiThread(() -> {
                        reloadLists();
                        try { Snackbar.make(rvScreenings, "Submitted", Snackbar.LENGTH_SHORT).show(); } catch (Exception ignore) {}
                    });
                } catch (Exception e) { Timber.e(e); }
            }
        };
        try {
            new org.smartregister.family.util.AppExecutors().diskIO().execute(runnable);
            return true;
        } catch (Exception e) { Timber.e(e); return false; }
    }

    private ECSyncHelper getECSyncHelper() { return ChwApplication.getInstance().getEcSyncHelper(); }
    private AllSharedPreferences getAllSharedPreferences() { return ChwApplication.getInstance().getContext().allSharedPreferences(); }
    private org.smartregister.sync.ClientProcessorForJava getClientProcessorForJava() { return ChwApplication.getInstance().getClientProcessorForJava(); }
    private FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    private String fmt(String millisStr) {
        try {
            long v = Long.parseLong(millisStr);
            return new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(v));
        } catch (Exception e) {
            return millisStr != null ? millisStr : "";
        }
    }

    class ScreeningAdapter extends RecyclerView.Adapter<ScreeningAdapter.VH> {
        private List<TbScreeningModel> items;
        ScreeningAdapter(List<TbScreeningModel> items) { this.items = items; }
        void setItems(List<TbScreeningModel> data) { this.items = data != null ? data : new ArrayList<>(); notifyDataSetChanged(); }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tb_screening, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            TbScreeningModel m = items.get(pos);
            h.tvDate.setText(fmt(m.getLast_interacted_with()));
            String summary = m.getReferred_for_tb_evaluation() != null ? ("Referred: " + m.getReferred_for_tb_evaluation()) : "";
            h.tvSummary.setText(summary);
            h.itemView.setOnClickListener(v -> startTbOutcomeForm(m.getUnique_tb_id()));
        }
        @Override public int getItemCount() { return items.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvDate, tvSummary;
            VH(@NonNull View itemView) { super(itemView); tvDate = itemView.findViewById(R.id.tvDate); tvSummary = itemView.findViewById(R.id.tvSummary); }
        }
    }

    // Outcome list and adapter removed per requirements
}







