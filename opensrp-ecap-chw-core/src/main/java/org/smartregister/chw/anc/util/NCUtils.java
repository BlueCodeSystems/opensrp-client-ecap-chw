package org.smartregister.chw.anc.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncWomanCallDialogContract;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.opensrp_chw_anc.R;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.db.EventClient;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.PermissionUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.JsonFormUtils.cleanString;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class NCUtils {

    private static String[] default_obs = {"start", "end", "deviceid", "subscriberid", "simserial", "phonenumber"};

    public static String firstCharacterUppercase(String str) {
        if (TextUtils.isEmpty(str)) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String convertToDateFormateString(String timeAsDDMMYYYY, SimpleDateFormat dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");//12-08-2018
        try {
            Date date = sdf.parse(timeAsDDMMYYYY);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    public static Integer daysBetweenDateAndNow(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(new Date(Long.valueOf(date)));
                Days days = Days.daysBetween(duration.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay());
                return days.getDays();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return null;
    }

    public static String getLocalForm(String jsonForm) {
        String suffix = Locale.getDefault().getLanguage().equals("fr") ? "_fr" : "";
        return MessageFormat.format("{0}{1}", jsonForm, suffix);
    }

    public static org.smartregister.Context context() {
        return AncLibrary.getInstance().context();
    }

    public static boolean launchDialer(final Activity activity, final BaseAncWomanCallDialogContract.View callView, final String phoneNumber) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // set a pending call execution request
            if (callView != null) {
                callView.setPendingCallRequest(new BaseAncWomanCallDialogContract.Dialer() {
                    @Override
                    public void callMe() {
                        NCUtils.launchDialer(activity, callView, phoneNumber);
                    }
                });
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            return false;
        } else {

            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Timber.i("No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(activity, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
                // no phone
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static String getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public static SimpleDateFormat getSourceDateFormat() {
        return new SimpleDateFormat(AncLibrary.getInstance().getSourceDateFormat(), Locale.getDefault());
    }

    public static SimpleDateFormat getSaveDateFormat() {
        return new SimpleDateFormat(AncLibrary.getInstance().getSaveDateFormat(), Locale.getDefault());
    }

    public static void addEvent(AllSharedPreferences allSharedPreferences, Event baseEvent) throws Exception {
        if (baseEvent != null) {
            JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
        }
    }

    public static void processEvent(String baseEntityID, JSONObject eventJson) throws Exception {
        if (eventJson != null) {
            getSyncHelper().addEvent(baseEntityID, eventJson);

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        }
    }

    public static void startClientProcessing() throws Exception {
        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
        getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    public static ECSyncHelper getSyncHelper() {
        return AncLibrary.getInstance().getEcSyncHelper();
    }

    public static ClientProcessorForJava getClientProcessorForJava() {
        return AncLibrary.getInstance().getClientProcessorForJava();
    }

    public static Visit eventToVisit(Event event, String visitID) throws JSONException {
        Visit visit = new Visit();
        visit.setVisitId(visitID);
        visit.setBaseEntityId(event.getBaseEntityId());
        visit.setDate(event.getEventDate());
        visit.setVisitType(event.getEventType());
        visit.setEventId(event.getEventId());
        visit.setFormSubmissionId(event.getFormSubmissionId());
        visit.setJson(new JSONObject(JsonFormUtils.gson.toJson(event)).toString());
        visit.setProcessed(false);
        visit.setCreatedAt(new Date());
        visit.setUpdatedAt(new Date());
        Map<String, String> eventDetails = event.getDetails();
        if (eventDetails != null)
            visit.setVisitGroup(eventDetails.get(Constants.HOME_VISIT_GROUP));

        Map<String, List<VisitDetail>> details = new HashMap<>();
        if (event.getObs() != null) {
            details = eventsObsToDetails(event.getObs(), visit.getVisitId(), null);
        }

        visit.setVisitDetails(details);
        return visit;
    }

    public static Map<String, List<VisitDetail>> eventsObsToDetails(List<Obs> obsList, String visitID, String baseEntityID) throws JSONException {
        List<String> exceptions = Arrays.asList(default_obs);
        Map<String, List<VisitDetail>> details = new HashMap<>();
        if (obsList == null)
            return details;

        for (Obs obs : obsList) {
            if (!exceptions.contains(obs.getFormSubmissionField())) {
                VisitDetail detail = new VisitDetail();
                detail.setVisitDetailsId(JsonFormUtils.generateRandomUUIDString());
                detail.setVisitId(visitID);
                detail.setBaseEntityId(baseEntityID);
                detail.setVisitKey(obs.getFormSubmissionField());
                detail.setParentCode(obs.getParentCode());
                detail.setDetails(getDetailsValue(detail, obs.getValues().toString()));
                detail.setHumanReadable(getDetailsValue(detail, obs.getHumanReadableValues().toString()));
                detail.setJsonDetails(new JSONObject(JsonFormUtils.gson.toJson(obs)).toString());
                detail.setProcessed(false);
                detail.setCreatedAt(new Date());
                detail.setUpdatedAt(new Date());

                List<VisitDetail> currentList = details.get(detail.getVisitKey());
                if (currentList == null)
                    currentList = new ArrayList<>();

                currentList.add(detail);
                details.put(detail.getVisitKey(), currentList);
            }
        }

        return details;
    }

    public static String getFormattedDate(SimpleDateFormat source_sdf, SimpleDateFormat dest_sdf, String value) {
        if (StringUtils.isBlank(value) ||
                Constants.HOME_VISIT.DOSE_NOT_GIVEN.equals(value) ||
                Constants.HOME_VISIT.VACCINE_NOT_GIVEN.equals(value))
            return "";

        try {
            Date date = source_sdf.parse(value);
            return dest_sdf.format(date);
        } catch (Exception e) {
            try {
                // fallback for long datetypes
                Date date = new Date(Long.parseLong(value));
                return dest_sdf.format(date);
            } catch (NumberFormatException | NullPointerException nfe) {
                Timber.e(e);
            }
            Timber.e(e);
        }
        return value;
    }

    // executed before processing
    public static Visit eventToVisit(Event event) throws JSONException {
        return eventToVisit(event, JsonFormUtils.generateRandomUUIDString());
    }

    public static void processHomeVisit(EventClient baseEvent) {
        processHomeVisit(baseEvent, null);
    }

    public static void processSubHomeVisit(EventClient baseEvent, String parentEventType) {
        processHomeVisit(baseEvent, null, parentEventType);
    }

    public static void processSubHomeVisit(EventClient baseEvent, SQLiteDatabase database, String parentEventType) {
        processHomeVisit(baseEvent, database, parentEventType);
    }

    public static void processHomeVisit(EventClient baseEvent, SQLiteDatabase database) {
        processHomeVisit(baseEvent, database, null);
    }

    public static void processHomeVisit(EventClient baseEvent, SQLiteDatabase database, String parentEventType) {
        try {
            Visit visit = AncLibrary.getInstance().visitRepository().getVisitByFormSubmissionID(baseEvent.getEvent().getFormSubmissionId());
            if (visit == null) {
                visit = eventToVisit(baseEvent.getEvent());

                if (StringUtils.isNotBlank(parentEventType) && !parentEventType.equalsIgnoreCase(visit.getVisitType())) {
                    String parentVisitID = AncLibrary.getInstance().visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate());
                    visit.setParentVisitID(parentVisitID);
                }

                if (database != null) {
                    AncLibrary.getInstance().visitRepository().addVisit(visit, database);
                } else {
                    AncLibrary.getInstance().visitRepository().addVisit(visit);
                }
                if (visit.getVisitDetails() != null) {
                    for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                        if (entry.getValue() != null) {
                            for (VisitDetail detail : entry.getValue()) {
                                if (database != null) {
                                    AncLibrary.getInstance().visitDetailsRepository().addVisitDetails(detail, database);
                                } else {
                                    AncLibrary.getInstance().visitDetailsRepository().addVisitDetails(detail);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    // executed by event client processor
    public static Visit eventToVisit(org.smartregister.domain.Event event) throws JSONException {
        List<String> exceptions = Arrays.asList(default_obs);

        Visit visit = new Visit();
        visit.setVisitId(org.smartregister.chw.anc.util.JsonFormUtils.generateRandomUUIDString());
        visit.setBaseEntityId(event.getBaseEntityId());
        visit.setDate(event.getEventDate().toDate());
        visit.setVisitType(event.getEventType());
        visit.setEventId(event.getEventId());
        visit.setFormSubmissionId(event.getFormSubmissionId());
        visit.setJson(new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(event)).toString());
        visit.setProcessed(true);
        visit.setCreatedAt(new Date());
        visit.setUpdatedAt(new Date());
        Map<String, String> eventDetails = event.getDetails();
        if (eventDetails != null)
            visit.setVisitGroup(eventDetails.get(Constants.HOME_VISIT_GROUP));

        Map<String, List<VisitDetail>> details = new HashMap<>();
        if (event.getObs() != null) {
            for (org.smartregister.domain.Obs obs : event.getObs()) {
                if (!exceptions.contains(obs.getFormSubmissionField())) {
                    VisitDetail detail = new VisitDetail();
                    detail.setVisitDetailsId(org.smartregister.chw.anc.util.JsonFormUtils.generateRandomUUIDString());
                    detail.setVisitId(visit.getVisitId());
                    detail.setVisitKey(obs.getFormSubmissionField());
                    detail.setParentCode(obs.getParentCode());
                    detail.setDetails(getDetailsValue(detail, obs.getValues().toString()));
                    detail.setHumanReadable(getDetailsValue(detail, obs.getHumanReadableValues().toString()));
                    detail.setProcessed(true);
                    detail.setCreatedAt(new Date());
                    detail.setUpdatedAt(new Date());

                    List<VisitDetail> currentList = details.get(detail.getVisitKey());
                    if (currentList == null)
                        currentList = new ArrayList<>();

                    currentList.add(detail);
                    details.put(detail.getVisitKey(), currentList);
                }
            }
        }

        visit.setVisitDetails(details);
        return visit;
    }

    public static String getDetailsValue(VisitDetail detail, String val) {
        String clean_val = cleanString(val);
        if (detail.getVisitKey().contains("date")) {
            return getFormattedDate(getSourceDateFormat(), getSaveDateFormat(), clean_val);
        }

        return clean_val;
    }

    @Nullable
    public static int getMemberProfileImageResourceIDentifier(@Nullable String memberType) {
        if ("anc".equals(memberType)) {
            return R.drawable.anc_woman;
        } else if ("pnc".equals(memberType)) {
            return R.drawable.pnc_woman;
        } else
            return R.mipmap.ic_member;
    }

    public static String gestationAgeString(String lmp, Context context, boolean full) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        int ga = Days.daysBetween(formatter.parseDateTime(lmp), new DateTime()).getDays() / 7;
        if (full)
            return String.format(context.getString(R.string.gest_age), String.valueOf(ga)) + " " + context.getString(R.string.gest_age_weeks);
        return String.valueOf(ga);
    }

    @Nullable
    public static JSONObject getVisitJSONFromWrapper(@Nullable Context context, String entityID, Map<VaccineWrapper, String> vaccineWrapperDateMap) {
        Context _context = context;
        if (context == null)
            _context = AncLibrary.getInstance().context().applicationContext();

        try {
            JSONObject jsonObject = FormUtils.getInstance(_context).getFormJson(Constants.FORMS.IMMUNIZATIOIN_VISIT);
            jsonObject.put("entity_id", entityID);
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);


            for (Map.Entry<VaccineWrapper, String> entry : vaccineWrapperDateMap.entrySet()) {
                JSONObject field = new JSONObject();
                field.put(JsonFormConstants.KEY, removeSpaces(entry.getKey().getName()));
                field.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "vaccine");
                field.put(JsonFormConstants.OPENMRS_ENTITY, "concept");
                field.put(JsonFormConstants.TYPE, JsonFormConstants.EDIT_TEXT);
                field.put(JsonFormConstants.OPENMRS_ENTITY_ID, removeSpaces(entry.getKey().getName()));
                field.put(JsonFormConstants.VALUE, entry.getValue());

                jsonArray.put(field);
            }

            return jsonObject;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Nullable
    public static JSONObject getVisitJSONFromVisitDetails(@Nullable Context context, String entityID, Map<String, List<VisitDetail>> detailsMap, List<VaccineDisplay> vaccineDisplays) {
        Context _context = context;
        if (context == null)
            _context = AncLibrary.getInstance().context().applicationContext();

        try {
            JSONObject jsonObject = FormUtils.getInstance(_context).getFormJson(Constants.FORMS.IMMUNIZATIOIN_VISIT);
            jsonObject.put("entity_id", entityID);
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            for (VaccineDisplay vaccineDisplay : vaccineDisplays) {
                JSONObject field = new JSONObject();

                String name = removeSpaces(vaccineDisplay.getVaccineWrapper().getName());
                String value = getText(detailsMap.get(name));

                field.put(JsonFormConstants.KEY, name);
                field.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "vaccine");
                field.put(JsonFormConstants.OPENMRS_ENTITY, "concept");
                field.put(JsonFormConstants.TYPE, JsonFormConstants.EDIT_TEXT);
                field.put(JsonFormConstants.OPENMRS_ENTITY_ID, name);
                field.put(JsonFormConstants.VALUE, value);

                jsonArray.put(field);
            }

            return jsonObject;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static String removeSpaces(String s) {
        return s.replace(" ", "_").toLowerCase();
    }

    /**
     * Extract value from VisitDetail
     *
     * @return
     */
    @NotNull
    public static String getText(@Nullable VisitDetail visitDetail) {
        if (visitDetail == null)
            return "";

        String val = visitDetail.getHumanReadable();
        if (StringUtils.isNotBlank(val))
            return val.trim();

        return (StringUtils.isNotBlank(visitDetail.getDetails())) ? visitDetail.getDetails().trim() : "";
    }

    @NotNull
    public static String getText(@Nullable List<VisitDetail> visitDetails) {
        if (visitDetails == null)
            return "";

        List<String> vals = new ArrayList<>();
        for (VisitDetail vd : visitDetails) {
            String val = getText(vd);
            if (StringUtils.isNotBlank(val))
                vals.add(val);
        }

        return toCSV(vals);
    }

    public static List<String> getTexts(@Nullable List<VisitDetail> visitDetails) {
        if (visitDetails == null)
            return null;

        List<String> texts = new ArrayList<>();
        for (VisitDetail vd : visitDetails) {
            String val = getText(vd);
            if (StringUtils.isNotBlank(val))
                texts.add(val);
        }

        return texts;
    }

    public static String toCSV(List<String> list) {
        String result = "";
        if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append(", ");
            }
            result = sb.deleteCharAt(sb.length() - 2).toString();
        }
        return result.trim();
    }

    public static String getStringResourceByName(String name, Context context) {
        if (context == null || name == null)
            return name;

        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(name, "string", packageName);
        return (resId == 0) ? name : context.getString(resId);
    }

}
