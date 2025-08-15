package com.bluecodeltd.ecap.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import com.bluecodeltd.ecap.chw.util.Utils;
import org.smartregister.domain.Alert;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class DewormingAction extends HomeVisitActionHelper {
    private Context context;
    private String serviceIteration;
    private String str_date;
    private Date parsedDate;
    private Alert alert;

    public DewormingAction(Context context, String serviceIteration, Alert alert) {
        this.context = context;
        this.serviceIteration = serviceIteration;
        this.alert = alert;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        // prevent default behavoiur
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return isOverDue() ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
    }

    private boolean isOverDue() {
        return new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
    }

    public JSONObject preProcess(JSONObject jsonObject, String iteration) {
        try {
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            String title = jsonObject.getJSONObject(JsonFormConstants.STEP1).getString("title");
            String formatted_count = MessageFormat.format("{0}", Utils.getDayOfMonthWithSuffix(Integer.valueOf(serviceIteration), context));
            jsonObject.getJSONObject(JsonFormConstants.STEP1).put("title", MessageFormat.format(title, formatted_count));

            JSONObject visit_field = JsonFormUtils.getFieldJSONObject(fields, "deworming{0}_date");
            visit_field.put("key", MessageFormat.format(visit_field.getString("key"), iteration, context));
            visit_field.put("hint", MessageFormat.format(visit_field.getString("hint"), Utils.getDayOfMonthWithSuffix(Integer.valueOf(serviceIteration), context)));
        }catch (Exception e){
            Timber.e(e);
        }

        return jsonObject;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            str_date = com.bluecodeltd.ecap.chw.util.JsonFormUtils.getValue(jsonObject, MessageFormat.format("deworming{0}_date", serviceIteration));

            try {
                parsedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(str_date);
            } catch (ParseException e) {
                parsedDate = null;
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (parsedDate != null) {
            return MessageFormat.format("{0} {1}",
                    context.getString(R.string.date_given),
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsedDate));
        }
        return context.getString(org.smartregister.chw.core.R.string.not_given);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(str_date)) {
            return BaseAncHomeVisitAction.Status.PENDING;
        }

        if (parsedDate != null) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }
}