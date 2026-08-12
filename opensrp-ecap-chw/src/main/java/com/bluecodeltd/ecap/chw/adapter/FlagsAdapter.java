package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.util.Threading;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FlagsAdapter extends RecyclerView.Adapter<FlagsAdapter.ViewHolder> {
    private final List<JsonObject> items;
    private final Set<String> expandedIds = new HashSet<>();

    public FlagsAdapter(List<JsonObject> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.single_flag, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JsonObject item = items.get(position);
        holder.bind(item, expandedIds.contains(idOf(item)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void toggleExpanded(int position) {
        if (position == RecyclerView.NO_POSITION || position >= items.size()) {
            return;
        }
        String id = idOf(items.get(position));
        if (id == null) {
            return;
        }
        if (!expandedIds.remove(id)) {
            expandedIds.add(id);
        }
        notifyItemChanged(position);
    }

    private static String idOf(JsonObject object) {
        if (object == null || !object.has("id") || object.get("id").isJsonNull()) {
            return null;
        }
        return object.get("id").getAsString();
    }

    /** True when a value is present and not a placeholder such as "-" or "N/A". */
    public static boolean isMeaningful(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.US);
        return !normalized.isEmpty()
                && !normalized.equals("-")
                && !normalized.equals("n/a")
                && !normalized.equals("na")
                && !normalized.equals("null")
                && !normalized.equals("not applicable");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View accent;
        private final TextView name;
        private final TextView status;
        private final TextView meta;
        private final ImageView toggle;
        private final View details;
        private final TextView openProfile;
        private final FieldRow householdId;
        private final FieldRow vcaId;
        private final FieldRow caregiver;
        private final FieldRow facility;
        private final FieldRow caseworker;
        private final FieldRow caseworkerPhone;
        private final FieldRow verifier;
        private final FieldRow comment;

        ViewHolder(@NonNull View itemView, @NonNull FlagsAdapter adapter) {
            super(itemView);
            accent = itemView.findViewById(R.id.flag_accent);
            name = itemView.findViewById(R.id.flag_name);
            status = itemView.findViewById(R.id.flag_status);
            meta = itemView.findViewById(R.id.flag_meta);
            toggle = itemView.findViewById(R.id.flag_toggle);
            details = itemView.findViewById(R.id.flag_details);
            openProfile = itemView.findViewById(R.id.flag_open_profile);
            householdId = new FieldRow(itemView.findViewById(R.id.row_household_id), "Household ID");
            vcaId = new FieldRow(itemView.findViewById(R.id.row_vca_id), "VCA ID");
            caregiver = new FieldRow(itemView.findViewById(R.id.row_caregiver_name), "Caregiver");
            facility = new FieldRow(itemView.findViewById(R.id.row_facility), "Facility");
            caseworker = new FieldRow(itemView.findViewById(R.id.row_caseworker_name), "Caseworker");
            caseworkerPhone = new FieldRow(itemView.findViewById(R.id.row_caseworker_phone), "Phone");
            verifier = new FieldRow(itemView.findViewById(R.id.row_verifier), "Verifier");
            comment = new FieldRow(itemView.findViewById(R.id.row_comment), "Comment");

            itemView.findViewById(R.id.flag_header)
                    .setOnClickListener(v -> adapter.toggleExpanded(getBindingAdapterPosition()));
        }

        void bind(JsonObject object, boolean expanded) {
            String statusValue = get(object, "status", "FLAGGED");
            String household = get(object, "household_id", null);
            String vca = get(object, "vca_id", null);
            String caregiverName = get(object, "caregiver_name", null);
            String facilityName = get(object, "facility", null);
            String date = formatDate(get(object, "date_created", ""));

            // Primary identifier for the collapsed row.
            String primaryId = isMeaningful(vca) ? "VCA " + vca.trim()
                    : isMeaningful(household) ? "HH " + household.trim()
                    : null;

            boolean idIsName = false;
            String title;
            if (isMeaningful(caregiverName)) {
                title = displayName(caregiverName);
            } else if (primaryId != null) {
                title = primaryId;
                idIsName = true;
            } else if (isMeaningful(facilityName)) {
                title = facilityName.trim();
            } else {
                title = "Flagged record";
            }
            name.setText(title);

            status.setText(capitalize(statusValue));
            applyStatusStyle(status, accent, statusValue);
            meta.setText(buildMeta(primaryId, idIsName, date));

            // Blank / N/A rows are hidden so caregiver-level and child-level flags
            // only show the fields that actually apply to them.
            householdId.set(household);
            vcaId.set(vca);
            caregiver.set(displayName(caregiverName));
            facility.set(facilityName);
            caseworker.set(displayName(get(object, "caseworker_name", null)));
            caseworkerPhone.set(get(object, "caseworker_phone", null));
            verifier.set(displayName(get(object, "verifier", null)));
            comment.set(get(object, "comment", null));

            bindProfileButton(household, vca);

            details.setVisibility(expanded ? View.VISIBLE : View.GONE);
            toggle.setImageResource(expanded
                    ? R.drawable.baseline_expand_less_24
                    : R.drawable.baseline_expand_more_24);
        }

        /** Grey secondary line: " · <id> · <date>" (status word is a separate colored view). */
        private static String buildMeta(String primaryId, boolean idIsName, String date) {
            StringBuilder sb = new StringBuilder();
            if (primaryId != null && !idIsName) {
                sb.append(" · ").append(primaryId);
            }
            if (date != null && !date.trim().isEmpty()) {
                sb.append(" · ").append(date.trim());
            }
            return sb.toString();
        }

        /**
         * A child-level flag carries a VCA ID and opens the CA profile; a
         * caregiver-level flag carries a Household ID and opens the caregiver
         * (household) profile. If neither is present the button is hidden.
         */
        private void bindProfileButton(String household, String vca) {
            Context context = openProfile.getContext();
            if (isMeaningful(vca)) {
                openProfile.setVisibility(View.VISIBLE);
                openProfile.setText(context.getString(R.string.open_ca_profile) + " ›");
                openProfile.setOnClickListener(v -> openCaProfile(v, vca));
            } else if (isMeaningful(household)) {
                openProfile.setVisibility(View.VISIBLE);
                openProfile.setText(context.getString(R.string.open_caregiver_profile) + " ›");
                openProfile.setOnClickListener(v -> openCaregiverProfile(v, household));
            } else {
                openProfile.setVisibility(View.GONE);
                openProfile.setOnClickListener(null);
            }
        }

        /**
         * Resolve the VCA locally before opening the profile. The flag comes from
         * Directus, so its VCA may not be registered on this device; launching the
         * profile with an unknown id crashes downstream fragments (null Child).
         */
        private static void openCaProfile(View anchor, String vca) {
            final Context context = anchor.getContext();
            anchor.setEnabled(false);
            Threading.io(() -> {
                Child child = null;
                try {
                    child = IndexPersonDao.getChildByBaseId(vca);
                } catch (Exception ignored) {
                    // Treat lookup failure as "not found" below.
                }
                final boolean found = child != null;
                Threading.main(() -> {
                    anchor.setEnabled(true);
                    if (found) {
                        Intent intent = new Intent(context, IndexDetailsActivity.class);
                        intent.putExtra("Child", vca);
                        context.startActivity(intent);
                    } else {
                        showMissingRecordDialog(context, "VCA", vca);
                    }
                });
            });
        }

        /**
         * Resolve the household locally before opening the caregiver profile.
         * {@code HouseholdDao.getHousehold} returns an empty object (never null)
         * when nothing matches, so we check that it carries a real household id.
         */
        private static void openCaregiverProfile(View anchor, String household) {
            final Context context = anchor.getContext();
            anchor.setEnabled(false);
            Threading.io(() -> {
                Household house = null;
                try {
                    house = HouseholdDao.getHousehold(household);
                } catch (Exception ignored) {
                    // Treat lookup failure as "not found" below.
                }
                final boolean found = house != null && isMeaningful(house.getHousehold_id());
                Threading.main(() -> {
                    anchor.setEnabled(true);
                    if (found) {
                        Intent intent = new Intent(context, HouseholdDetails.class);
                        intent.putExtra("householdId", household);
                        context.startActivity(intent);
                    } else {
                        showMissingRecordDialog(context, "Household", household);
                    }
                });
            });
        }


        private static void showMissingRecordDialog(Context context, String recordType, String recordId) {
            new AlertDialog.Builder(context)
                    .setTitle(recordType + " not on this device")
                    .setMessage(recordType + " " + recordId + " is assigned to this provider but is not available locally. Run sync, then reopen this flag after sync completes.")
                    .setPositiveButton("Sync now", (DialogInterface dialog, int which) -> {
                        try {
                            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
                            SyncTaskServiceJob.scheduleJobImmediately(SyncTaskServiceJob.TAG);
                            Toast.makeText(context, "Sync started. Try opening the flag again after sync completes.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(context, "Could not start sync. Please use Sync from the menu.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        }

        private static String displayName(String value) {
            if (!isMeaningful(value)) {
                return value;
            }
            String normalized = value.trim().replaceAll("\\s+", " ");
            StringBuilder builder = new StringBuilder();
            String[] parts = normalized.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    builder.append(' ');
                }
                builder.append(capitalizeNamePart(parts[i]));
            }
            return builder.toString();
        }

        private static String capitalizeNamePart(String part) {
            if (part == null || part.isEmpty()) {
                return "";
            }
            String[] hyphenParts = part.split("-", -1);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < hyphenParts.length; i++) {
                if (i > 0) {
                    builder.append('-');
                }
                builder.append(capitalizeSimpleNamePart(hyphenParts[i]));
            }
            return builder.toString();
        }

        private static String capitalizeSimpleNamePart(String part) {
            if (part == null || part.isEmpty()) {
                return "";
            }
            String lower = part.toLowerCase(Locale.US);
            return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }
        private static String get(JsonObject object, String key, String fallback) {
            if (object == null || key == null || !object.has(key) || object.get(key).isJsonNull()) {
                return fallback;
            }
            String value = object.get(key).getAsString();
            return value == null || value.trim().isEmpty() ? fallback : value;
        }

        private static String formatDate(String rawDate) {
            if (rawDate == null) {
                return "";
            }
            String trimmed = rawDate.trim();
            if (trimmed.isEmpty()) {
                return "";
            }

            String[] inputPatterns = new String[]{
                    "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                    "yyyy-MM-dd'T'HH:mm:ssX",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
            };
            for (String pattern : inputPatterns) {
                try {
                    SimpleDateFormat input = new SimpleDateFormat(pattern, Locale.US);
                    Date parsed = input.parse(trimmed);
                    if (parsed != null) {
                        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(parsed);
                    }
                } catch (ParseException ignored) {
                    // Try the next supported date shape.
                }
            }

            return trimmed;
        }

        private void applyStatusStyle(TextView statusView, View accentView, String rawStatus) {
            String normalized = rawStatus == null ? "" : rawStatus.trim().toLowerCase(Locale.US);
            int color;
            switch (normalized) {
                case "active":
                    color = Color.parseColor("#2E7D32");
                    break;
                case "resolved":
                    color = Color.parseColor("#6B7280");
                    break;
                case "low":
                    color = Color.parseColor("#2E7D32");
                    break;
                case "medium":
                    color = Color.parseColor("#F9A825");
                    break;
                case "high":
                    color = Color.parseColor("#EF6C00");
                    break;
                case "critical":
                    color = Color.parseColor("#C62828");
                    break;
                default:
                    color = Color.parseColor("#546E7A");
                    break;
            }
            statusView.setTextColor(color);
            if (accentView != null) {
                accentView.setBackgroundColor(color);
            }
        }

        private String capitalize(String value) {
            if (value == null) {
                return "";
            }
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                return "";
            }
            String lower = trimmed.toLowerCase(Locale.US);
            return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }
    }

    /** Wraps an included flag_field_row layout (label + value); hides itself when blank. */
    static class FieldRow {
        private final View root;
        private final TextView value;

        FieldRow(View root, String label) {
            this.root = root;
            this.value = root.findViewById(R.id.field_value);
            TextView labelView = root.findViewById(R.id.field_label);
            labelView.setText(label);
        }

        void set(String text) {
            if (isMeaningful(text)) {
                value.setText(text);
                root.setVisibility(View.VISIBLE);
            } else {
                root.setVisibility(View.GONE);
            }
        }
    }
}
