package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.util.Threading;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HouseholdChildrenAdapter extends RecyclerView.Adapter<HouseholdChildrenAdapter.ViewHolder> {

    private final Context context;
    private final List<Child> children;
    @SuppressWarnings("unused")
    private final String txtMuac;
    private String indexUniqueIdToShow;
    private int indexCacheSize = -1;

    public HouseholdChildrenAdapter(List<Child> children, Context context, String txtMuac) {
        super();
        this.children = children;
        this.context = context;
        this.txtMuac = txtMuac;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_household_child, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Child listChild = (position >= 0 && position < children.size()) ? children.get(position) : null;
        if (listChild == null) {
            resetViewHolder(holder);
            return;
        }

        final String childUniqueId = listChild.getUnique_id();
        if (TextUtils.isEmpty(childUniqueId)) {
            resetViewHolder(holder);
            return;
        }

        final Child initialChild = listChild;
        final String rowTag = childUniqueId;
        holder.itemView.setTag(R.id.tag_row_id, rowTag);
        holder.itemView.setTag(initialChild);

        ensureIndexCache();
        bindBasicRow(holder, initialChild);

        final String dobLocal = safeConvertDob(initialChild.getAdolescent_birthdate());
        final String memberAge = getAgeWithoutText(dobLocal);

        Threading.ioBestEffort(() -> {
            Child fetchedChild = null;
            try {
                fetchedChild = IndexPersonDao.getChildByBaseId(childUniqueId);
            } catch (Exception ignored) {
            }
            Child effectiveChild = fetchedChild != null ? fetchedChild : initialChild;

            String resolvedCaseStatus = null;
            try {
                String baseEntityId = effectiveChild != null ? effectiveChild.getBaseEntity_id() : null;
                if (!TextUtils.isEmpty(baseEntityId)) {
                    resolvedCaseStatus = IndexPersonDao.getIndexStatus(baseEntityId);
                }
            } catch (Exception ignored) {
            }

            final Child finalChild = effectiveChild;
            final String finalCaseStatus = resolvedCaseStatus;

            Threading.main(() -> {
                Object currentTag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(currentTag instanceof String) || !rowTag.equals(currentTag)) return;

                if (finalChild != null) {
                    holder.itemView.setTag(finalChild);
                    bindBasicRow(holder, finalChild);
                }

                if ("1".equals(finalCaseStatus)) {
                    holder.colorView.setBackgroundColor(Color.parseColor("#05b714"));
                } else if ("0".equals(finalCaseStatus)) {
                    holder.colorView.setBackgroundColor(Color.parseColor("#ff0000"));
                } else if ("2".equals(finalCaseStatus)) {
                    holder.colorView.setBackgroundColor(Color.parseColor("#ffa500"));
                } else {
                    holder.colorView.setBackgroundColor(Color.parseColor("#696969"));
                }
            });
        });

        holder.container.setOnClickListener(v -> {
            if (v.getId() != R.id.register_columns) return;

            Child child = holder.itemView.getTag() instanceof Child ? (Child) holder.itemView.getTag() : initialChild;
            String subpop3 = child != null ? child.getSubpop3() : null;
            if (subpop3 == null) {
                Intent editIntent = new Intent(context, IndexDetailsActivity.class);
                editIntent.putExtra("Child", child != null ? child.getUnique_id() : childUniqueId);
                editIntent.putExtra("open_vca_edit_if_incomplete", true);
                context.startActivity(editIntent);
                return;
            }

            try {
                if (!TextUtils.isEmpty(memberAge) && Integer.parseInt(memberAge) < 24) {
                    Intent intent = new Intent(context, IndexDetailsActivity.class);
                    intent.putExtra("fromIndex", "321");
                    intent.putExtra("Child", child != null ? child.getUnique_id() : childUniqueId);
                    context.startActivity(intent);
                } else {
                    Toasty.warning(context, "Member is not enrolled on the Program", Toast.LENGTH_LONG, true).show();
                }
            } catch (Exception e) {
                Toasty.error(context, "Unable to open child details", Toast.LENGTH_LONG, true).show();
            }
        });
    }

    private void bindBasicRow(ViewHolder holder, Child child) {
        if (child == null) {
            resetViewHolder(holder);
            return;
        }

        String firstName = child.getFirst_name();
        String lastName = child.getLast_name();
        holder.fullName.setText((firstName != null ? firstName : "") + (lastName != null ? (" " + lastName) : ""));

        String dob = safeConvertDob(child.getAdolescent_birthdate());
        String ageText = (!TextUtils.isEmpty(dob) && !"Invalid birthdate format".equals(dob)) ? getAge(dob) : null;
        String gender = child.getGender();
        StringBuilder ageGenderLine = new StringBuilder();
        if (!TextUtils.isEmpty(ageText)) ageGenderLine.append(ageText);
        if (!TextUtils.isEmpty(gender)) {
            if (ageGenderLine.length() > 0) ageGenderLine.append(" \u2022 ");
            ageGenderLine.append(gender.substring(0, 1).toUpperCase(Locale.ENGLISH))
                    .append(gender.length() > 1 ? gender.substring(1).toLowerCase(Locale.ENGLISH) : "");
        }
        holder.ageGender.setText(ageGenderLine.toString());

        String caregiver = child.getCaregiver_name();
        holder.caregiverName.setText(!TextUtils.isEmpty(caregiver) ? ("Mother: " + caregiver) : "");

        try {
            String uniqueId = child.getUnique_id();
            boolean shouldShow = isIndexVca(child.getIndex_check_box())
                    && !TextUtils.isEmpty(uniqueId)
                    && uniqueId.equals(indexUniqueIdToShow);
            holder.isIndex.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        } catch (Exception ignored) {
            holder.isIndex.setVisibility(View.GONE);
        }
    }

    private void resetViewHolder(ViewHolder holder) {
        holder.fullName.setText("");
        holder.ageGender.setText("");
        holder.caregiverName.setText("");
        holder.isIndex.setVisibility(View.GONE);
        holder.colorView.setBackgroundColor(Color.parseColor("#696969"));
    }

    private void ensureIndexCache() {
        int currentSize = children != null ? children.size() : 0;
        if (currentSize == indexCacheSize && (indexUniqueIdToShow != null || currentSize == 0)) {
            return;
        }

        indexCacheSize = currentSize;
        indexUniqueIdToShow = null;
        if (children == null || children.isEmpty()) {
            return;
        }

        // children list is ordered DESC by id from the query; pick the first "index" entry only
        for (Child child : children) {
            if (child == null) continue;
            if (isIndexVca(child.getIndex_check_box()) && !TextUtils.isEmpty(child.getUnique_id())) {
                indexUniqueIdToShow = child.getUnique_id();
                break;
            }
        }
    }

    private static boolean isIndexVca(String indexCheckBoxValue) {
        return "yes".equalsIgnoreCase(indexCheckBoxValue) || "1".equals(indexCheckBoxValue);
    }

    private static String safeConvertDob(String date) {
        if (TextUtils.isEmpty(date)) return null;
        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) return date;

        DateTimeFormatter oldFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate localDate = LocalDate.parse(date, oldFormatter);
            return localDate.format(newFormatter);
        } catch (DateTimeParseException e) {
            Log.e("HouseholdChildrenAdapter", "Invalid date format: " + e.getMessage());
            return "Invalid birthdate format";
        }
    }

    private static String getAge(String birthdate) {
        if (TextUtils.isEmpty(birthdate) || "Invalid birthdate format".equals(birthdate)) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
            LocalDate today = LocalDate.now();
            Period periodBetween = Period.between(localDateBirthdate, today);
            if (periodBetween.getYears() > 0) {
                return periodBetween.getYears() + " Years";
            } else if (periodBetween.getMonths() > 0) {
                return periodBetween.getMonths() + " Months";
            } else if (periodBetween.getDays() >= 0) {
                return periodBetween.getDays() + " Days";
            } else {
                return "";
            }
        } catch (DateTimeParseException e) {
            Timber.w(e);
            return "";
        }
    }

    private static String getAgeWithoutText(String birthdate) {
        if (TextUtils.isEmpty(birthdate) || "Invalid birthdate format".equals(birthdate)) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
            LocalDate today = LocalDate.now();
            Period periodBetween = Period.between(localDateBirthdate, today);
            if (periodBetween.getYears() > 0) {
                return String.valueOf(periodBetween.getYears());
            } else if (periodBetween.getMonths() > 0) {
                return String.valueOf(periodBetween.getMonths());
            } else if (periodBetween.getDays() >= 0) {
                return String.valueOf(periodBetween.getDays());
            } else {
                return "";
            }
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return children != null ? children.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView fullName;
        final TextView ageGender;
        final TextView caregiverName;
        final TextView isIndex;
        final View colorView;
        final RelativeLayout container;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.register_columns);
            colorView = itemView.findViewById(R.id.mycolor);
            fullName = itemView.findViewById(R.id.child_name);
            ageGender = itemView.findViewById(R.id.child_age_gender);
            caregiverName = itemView.findViewById(R.id.caregiver_name);
            isIndex = itemView.findViewById(R.id.index_icon);
        }
    }
}
