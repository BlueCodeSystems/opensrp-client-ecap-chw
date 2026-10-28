package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.TrainingAttendanceDao;
import com.bluecodeltd.ecap.chw.model.TrainingAttendanceRecord;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.IndexClientsUtils;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.TrainingAttendance;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

/**
 * Lists the trainings the signed-in staff member attended and logs new ones. Opened from the drawer's
 * "Training attendance" item. A logged training saves as a "Training Attendance" event, which syncs to the
 * server like any other form, and the client processor writes it to ec_training_attendance.
 */
public class TrainingAttendanceActivity extends AppCompatActivity {

    private static final SimpleDateFormat STORED = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat SHOWN = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private RecyclerView list;
    private View emptyState;
    private View progress;
    private final TrainingAdapter adapter = new TrainingAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_attendance);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login_header_start));

        Toolbar toolbar = findViewById(R.id.training_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        list = findViewById(R.id.training_list);
        emptyState = findViewById(R.id.training_empty);
        progress = findViewById(R.id.training_progress);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        findViewById(R.id.training_log_button).setOnClickListener(v -> openForm());

        loadTrainings();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTrainings() {
        Threading.io(() -> {
            List<TrainingAttendanceRecord> records = TrainingAttendanceDao.getTrainings();
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                progress.setVisibility(View.GONE);
                adapter.setRecords(records);
                boolean empty = records.isEmpty();
                list.setVisibility(empty ? View.GONE : View.VISIBLE);
                emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void openForm() {
        Threading.io(() -> {
            JSONObject form = null;
            try {
                form = new FormUtils(this).getFormJson(TrainingAttendance.FORM_NAME);
                if (form != null) prefill(form);
            } catch (Exception e) {
                Timber.e(e, "Unable to open %s", TrainingAttendance.FORM_NAME);
            }
            JSONObject finalForm = form;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (finalForm == null) {
                    Toasty.error(this, getString(R.string.training_open_failed), Toast.LENGTH_LONG, true).show();
                    return;
                }
                Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
                Form formParams = new Form();
                formParams.setWizard(false);
                formParams.setHideSaveLabel(true);
                formParams.setSaveLabel(getString(R.string.submit));
                formParams.setNavigationBackground(R.color.primary);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParams);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, finalForm.toString());
                startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            });
        });
    }

    /** Fills the hidden fields that identify who logged the training and where. */
    private void prefill(JSONObject form) throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String caseworker = prefs.getString("caseworker_name", "");
        if (caseworker == null || caseworker.trim().isEmpty()) {
            caseworker = prefs.getString("last_logged_in_username", "");
        }
        String code = prefs.getString("code", "");
        String formId = (code != null && !code.trim().isEmpty() ? code + "/" : "") + new Random().nextInt(100000000);
        JSONArray fields = JsonFormUtils.fields(form);
        setValue(fields, "caseworker_name", caseworker);
        setValue(fields, "facility", prefs.getString("facility", ""));
        setValue(fields, "province", prefs.getString("province", ""));
        setValue(fields, "district", prefs.getString("district", ""));
        setValue(fields, "form_id", formId);
        setValue(fields, "date_created", STORED.format(new Date()));
    }

    private static void setValue(JSONArray fields, String key, String value) throws Exception {
        JSONObject field = JsonFormUtils.getFieldJSONObject(fields, key);
        if (field != null) field.put(JsonFormUtils.VALUE, value != null ? value : "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != JsonFormUtils.REQUEST_CODE_GET_JSON || resultCode != RESULT_OK || data == null) return;
        String json = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
        if (json == null) return;
        try {
            if (TrainingAttendance.ENCOUNTER_TYPE.equalsIgnoreCase(new JSONObject(json).optString(JsonFormConstants.ENCOUNTER_TYPE))) {
                save(json);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Saves the submission the way the monthly reports do: the event and client go to the sync tables, and the
     * client processor writes the row via ec_client_classification.json.
     */
    private void save(String json) {
        Toasty.info(this, getString(R.string.training_saving), Toast.LENGTH_SHORT, true).show();
        Threading.io(() -> {
            boolean saved = false;
            try {
                JSONObject form = new JSONObject(json);
                String entityId = form.optString(Constants.JSON_FORM_KEY.ENTITY_ID);
                if (entityId.isEmpty()) entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
                JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, form.getJSONObject(Constants.METADATA),
                        IndexClientsUtils.getFormTag(), entityId, TrainingAttendance.ENCOUNTER_TYPE, TrainingAttendance.TABLE);
                tagSyncMetadata(event);
                Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, IndexClientsUtils.getFormTag(), entityId);

                ECSyncHelper sync = ChwApplication.getInstance().getEcSyncHelper();
                sync.addClient(client.getBaseEntityId(), new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client)));
                sync.addEvent(event.getBaseEntityId(), new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event)));

                long lastUpdatedAt = IndexClientsUtils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                List<EventClient> savedEvents = sync.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                ChwApplication.getInstance().getClientProcessorForJava().processClient(savedEvents);
                IndexClientsUtils.getAllSharedPreferences().saveLastUpdatedAtDate(lastUpdatedAt);
                saved = true;
            } catch (Exception e) {
                Timber.e(e, "Unable to save training attendance");
            }
            boolean finalSaved = saved;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (finalSaved) {
                    Toasty.success(this, getString(R.string.training_saved), Toast.LENGTH_SHORT, true).show();
                    loadTrainings();
                } else {
                    Toasty.error(this, getString(R.string.training_save_failed), Toast.LENGTH_LONG, true).show();
                }
            });
        });
    }

    private static String shownDate(String stored) {
        if (stored == null || stored.trim().isEmpty()) return "";
        try {
            Date date = STORED.parse(stored.trim());
            return date != null ? SHOWN.format(date) : stored;
        } catch (ParseException e) {
            return stored;
        }
    }

    private String topicLabel(String key) {
        if (key == null) return "";
        switch (key) {
            case "case_management": return getString(R.string.training_topic_case_management);
            case "child_protection": return getString(R.string.training_topic_child_protection);
            case "hiv_pmtct": return getString(R.string.training_topic_hiv_pmtct);
            case "safeguarding": return getString(R.string.training_topic_safeguarding);
            case "data_reporting": return getString(R.string.training_topic_data_reporting);
            case "psychosocial_support": return getString(R.string.training_topic_psychosocial_support);
            case "other": return getString(R.string.training_topic_other);
            default: return key;
        }
    }

    private String modeLabel(String key) {
        if (key == null) return "";
        switch (key) {
            case "in_person": return getString(R.string.training_mode_in_person);
            case "virtual": return getString(R.string.training_mode_virtual);
            case "blended": return getString(R.string.training_mode_blended);
            default: return key;
        }
    }

    private static String joined(String... parts) {
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.trim().isEmpty()) continue;
            if (out.length() > 0) out.append(" · ");
            out.append(part.trim());
        }
        return out.toString();
    }

    private static void showText(TextView view, String text) {
        view.setText(text);
        view.setVisibility(text == null || text.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.Holder> {

        private final List<TrainingAttendanceRecord> records = new ArrayList<>();

        void setRecords(List<TrainingAttendanceRecord> newRecords) {
            records.clear();
            records.addAll(newRecords);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_attendance, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            TrainingAttendanceRecord record = records.get(position);
            holder.title.setText(record.trainingTitle != null && !record.trainingTitle.trim().isEmpty()
                    ? record.trainingTitle.trim() : topicLabel(record.trainingTopic));
            showText(holder.topic, joined(topicLabel(record.trainingTopic), modeLabel(record.trainingMode)));
            String start = shownDate(record.startDate);
            String end = shownDate(record.endDate);
            String dates = end.isEmpty() || end.equals(start) ? start : start + " – " + end;
            showText(holder.when, joined(dates, record.venue));
            showText(holder.facilitator, record.facilitator != null && !record.facilitator.trim().isEmpty()
                    ? getString(R.string.training_facilitator, record.facilitator.trim()) : "");
            holder.certificate.setVisibility("yes".equalsIgnoreCase(record.certificateReceived) ? View.VISIBLE : View.GONE);
            holder.partial.setVisibility("no".equalsIgnoreCase(record.attendedFull) ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView topic;
            final TextView when;
            final TextView facilitator;
            final TextView certificate;
            final TextView partial;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.training_item_title);
                topic = itemView.findViewById(R.id.training_item_topic);
                when = itemView.findViewById(R.id.training_item_when);
                facilitator = itemView.findViewById(R.id.training_item_facilitator);
                certificate = itemView.findViewById(R.id.training_item_certificate);
                partial = itemView.findViewById(R.id.training_item_partial);
            }
        }
    }
}
