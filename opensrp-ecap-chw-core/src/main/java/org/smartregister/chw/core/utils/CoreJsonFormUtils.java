package org.smartregister.chw.core.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.FamilyMemberDao;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.model.CoreFamilyMemberModel;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Photo;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreConstants.EventType.UPDATE_CHILD_REGISTRATION;
import static org.smartregister.chw.core.utils.CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION;

/**
 * Created by keyman on 13/11/2018.
 */
public class CoreJsonFormUtils extends org.smartregister.family.util.JsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String TITLE = "title";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;
    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String READ_ONLY = "read_only";
    private static HashMap<String, String> actionMap = null;
    private static final String LOCATION_UUIDS = "location_uuids";

    public static Intent getJsonIntent(Context context, JSONObject jsonForm, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        return intent;
    }

    public static int getDayFromDate(String dateOfBirth) {
        DateTime date = DateTime.parse(dateOfBirth);
        Days days = Days.daysBetween(date.toLocalDate(), LocalDate.now());
        return days.getDays();
    }

    public static JSONObject getEcdWithDatePass(JSONObject form, String dateOfBirthString) throws Exception {

        if (form == null) {
            return null;
        }
        JSONArray field = fields(form);
        JSONObject datePass = getFieldJSONObject(field, "date_pass");
        int days = getDayFromDate(dateOfBirthString);
        datePass.put("value", days);
        return form;

    }

    public static JSONObject getPreviousECDAsJson(JSONObject form, String baseEntityId) throws Exception {

        if (form == null) {
            return null;
        }
        form.put(ENTITY_ID, baseEntityId);

        return form;

    }

    public static JSONObject getOnsIllnessFormAsJson(JSONObject form, String baseEntityId, String currentLocationId, String dateOfBirthString) throws Exception {

        if (form == null) {
            return null;
        }
        //dateOfBirthString = dateOfBirthString.contains("y") ? dateOfBirthString.substring(0, dateOfBirthString.indexOf("y")) : "";
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);
        form.put(ENTITY_ID, baseEntityId);
        JSONArray field = fields(form);
        JSONObject mindate = getFieldJSONObject(field, "date_of_illness");
        int days = getDayFromDate(dateOfBirthString);
        //if(mindate!=null){
        mindate.put("min_date", "today-" + days + "d");
        //}
        return form;

    }

    public static Event getECDEvent(String jsonString, String homeVisitId, String entityId) {
        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
        if (!registrationFormParams.getLeft()) {
            return null;
        }
        JSONObject jsonForm = registrationFormParams.getMiddle();
        JSONArray fields = registrationFormParams.getRight();

        // String entityIdForm = getString(jsonForm, ENTITY_ID);

        lastInteractedWith(fields);
        //Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(org.smartregister.family.util.Utils.context().allSharedPreferences()), entityId);
        Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(org.smartregister.family.util.Utils.context().allSharedPreferences()),
                entityId, getString(jsonForm, ENCOUNTER_TYPE), CoreConstants.TABLE_NAME.CHILD);
        baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withValue(homeVisitId)
                .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

        tagSyncMetadata(org.smartregister.family.util.Utils.context().allSharedPreferences(), baseEvent);// tag docs
        return baseEvent;

    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    public static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
        event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());

        return event;
    }

    public static List<Object> toList(String... vals) {
        return new ArrayList<>(Arrays.asList(vals));
    }

    public static HashMap<String, String> getChoice(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.yes), "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.no), "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return choices;
    }

    public static HashMap<String, String> getChoiceDietary(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_1), "");
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_2), "");
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_3), "");
        return choices;
    }

    public static HashMap<String, String> getChoiceMuac(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.muac_choice_1), "160909AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.muac_choice_2), "160910AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.muac_choice_3), "127778AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return choices;
    }

    public static JSONObject getFormAsJson(JSONObject form,
                                           String formName, String id,
                                           String currentLocationId, String familyID) throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        if (Utils.metadata().familyRegister.formName.equals(formName) || Utils.metadata().familyMemberRegister.formName.equals(formName) || formName.equalsIgnoreCase(CoreConstants.JSON_FORM.getChildRegister())) {
            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            // Inject opensrp id into the form
            JSONArray field = fields(form);
            JSONObject uniqueId = getFieldJSONObject(field, DBConstants.KEY.UNIQUE_ID);
//            JSONObject insurance_provider = getFieldJSONObject(field, CoreConstants.JsonAssets.INSURANCE_PROVIDER);
            if (uniqueId != null) {
                uniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                uniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, entityId);
            }

//            if (insurance_provider != null) {
//                insurance_provider.remove(CoreConstants.JsonAssets.INSURANCE_PROVIDER);
//                insurance_provider.put(CoreConstants.JsonAssets.INSURANCE_PROVIDER, insurance_provider);
//            }

            if (!StringUtils.isBlank(familyID)) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "family");
                lookup.put("value", familyID);
            }


        } else {
            Timber.w("Unsupported form requested for launch %s", formName);
        }
        Timber.d("form is %s", form.toString());
        return form;
    }

    public static void addRelationship(Context context, Client parent, Client child) {
        try {
            String relationships = AssetHandler.readFileFromAssetsFolder(FormUtils.ecClientRelationships, context);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field") && getString(rObject, "field").equals(ENTITY_ID)) {
                    child.addRelationship(rObject.getString("client_relationship"), parent.getBaseEntityId());
                } /* else {
                    //TODO how to add other kind of relationships
                  } */
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static ArrayList<Address> getAddressFromClientJson(JSONObject clientjson) {
        ArrayList<Address> addresses = new ArrayList<Address>();
        try {
            JSONArray addressArray = clientjson.getJSONArray("addresses");
            for (int i = 0; i < addressArray.length(); i++) {
                Address address = new Address();
                address.setAddressType(addressArray.getJSONObject(i).getString("addressType"));
                JSONObject addressfields = addressArray.getJSONObject(i).getJSONObject("addressFields");

                Iterator<?> keys = addressfields.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (addressfields.get(key) instanceof String) {
                        address.addAddressField(key, addressfields.getString(key));
                    }
                }
                addresses.add(address);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return addresses;
    }

    public static void mergeAndSaveClient(ECSyncHelper ecUpdater, Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject = ecUpdater.getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        ecUpdater.addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static JSONObject getAutoPopulatedJsonEditFormString(String formName, Context context, CommonPersonObjectClient client, String eventType) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Timber.d("Form is %s", form.toString());
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject);

                }

                org.smartregister.family.util.JsonFormUtils.addLocHierarchyQuestions(form);

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    protected static void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {

        String key = jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase();
        switch (key) {
            case Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
                break;
            case DBConstants.KEY.DOB:
                getDob(client, jsonObject);
                break;
            case Constants.KEY.PHOTO:
                getPhoto(client, jsonObject);
                break;
            case DBConstants.KEY.UNIQUE_ID:
                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));
                break;
            case "fam_name":
                String fam_name = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, fam_name);
                break;
            case DBConstants.KEY.VILLAGE_TOWN:
            case DBConstants.KEY.QUATER_CLAN:
            case DBConstants.KEY.STREET:
            case DBConstants.KEY.LANDMARK:
            case DBConstants.KEY.FAMILY_SOURCE_INCOME:
            case ChwDBConstants.NEAREST_HEALTH_FACILITY:
            case DBConstants.KEY.GPS:
            case ChwDBConstants.EVENT_DATE:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), key, false));
                break;

            default:
                Timber.e("ERROR:: Unprocessed Form Object Key %s", jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY));
                break;
        }
        updateOptions(client, jsonObject);
    }

    private static void getDob(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, dd_MM_yyyy.format(dob));
            }
        }
    }

    private static void getPhoto(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    private static void updateOptions(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB)) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        }
    }

    /**
     * @param familyID
     * @param allSharedPreferences
     * @param jsonObject
     * @param providerId
     * @return Returns a triple object <b>DateOfDeath as String, BaseEntityID , List of Events </b>that should be processed
     */
    public static Triple<Pair<Date, String>, String, List<Event>> processRemoveMemberEvent(String familyID, AllSharedPreferences allSharedPreferences, JSONObject jsonObject, String providerId) {

        try {

            List<Event> events = new ArrayList<>();

            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonObject.toString());

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            Date dod = null;


            JSONObject metadata = getJSONObject(registrationFormParams.getMiddle(), METADATA);
            String memberID = getString(registrationFormParams.getMiddle(), ENTITY_ID);

            JSONArray fields = new JSONArray();

            int x = 0;
            while (x < registrationFormParams.getRight().length()) {
                //JSONObject obj = registrationFormParams.getRight().getJSONObject(x);
                String myKey = registrationFormParams.getRight().getJSONObject(x).getString(KEY);

                if (myKey.equalsIgnoreCase(CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DATE_MOVED) ||
                        myKey.equalsIgnoreCase(CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.REASON)
                ) {
                    fields.put(registrationFormParams.getRight().get(x));
                }
                if (myKey.equalsIgnoreCase(CoreConstants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DATE_DIED)) {
                    fields.put(registrationFormParams.getRight().get(x));
                    try {
                        dod = dd_MM_yyyy.parse(registrationFormParams.getRight().getJSONObject(x).getString(VALUE));
                    } catch (Exception e) {
                        Timber.d(e.toString());
                    }
                }
                x++;
            }

            String encounterType = getString(jsonObject, ENCOUNTER_TYPE);

            String eventType;
            String tableName;

            if (encounterType.equalsIgnoreCase(CoreConstants.EventType.REMOVE_CHILD)) {
                eventType = CoreConstants.EventType.REMOVE_CHILD;
                tableName = CoreConstants.TABLE_NAME.CHILD;
            } else if (encounterType.equalsIgnoreCase(CoreConstants.EventType.REMOVE_FAMILY)) {
                eventType = CoreConstants.EventType.REMOVE_FAMILY;
                tableName = CoreConstants.TABLE_NAME.FAMILY;
            } else {
                eventType = CoreConstants.EventType.REMOVE_MEMBER;
                tableName = CoreConstants.TABLE_NAME.FAMILY_MEMBER;
            }

            Event eventMember = CoreJsonFormUtils.createEvent(fields, metadata, formTag(allSharedPreferences), memberID,
                    eventType,
                    tableName
            );
            CoreJsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), eventMember);
            events.add(eventMember);


            return Triple.of(Pair.create(dod, encounterType), memberID, events);
        } catch (Exception e) {
            Timber.e(e.toString());
            return null;
        }
    }

    /**
     * Returns a value from json form field
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getValue(JSONObject jsonObject, String key) {
        try {
            JSONObject formField = com.vijay.jsonwizard.utils.FormUtils.getFieldFromForm(jsonObject, key);
            if (formField != null && formField.has(JsonFormConstants.VALUE)) {
                return formField.getString(JsonFormConstants.VALUE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    public static JSONObject getFormWithMetaData(String baseEntityID, Context context, String formName, String eventType) {
        JSONObject form = null;
        try {
            form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, baseEntityID);
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return form;

    }

    public static FamilyMember getFamilyMemberFromRegistrationForm(String jsonString, String familyBaseEntityId, String entityID) throws JSONException {
        FamilyMember member = new FamilyMember();

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
        if (!registrationFormParams.getLeft()) {
            return null;
        }

        JSONArray fields = registrationFormParams.getRight();

        member.setFamilyID(familyBaseEntityId);
        member.setMemberID(entityID);
        member.setPhone(getJsonFieldValue(fields, CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER));
        member.setOtherPhone(getJsonFieldValue(fields, CoreConstants.JsonAssets.FAMILY_MEMBER.OTHER_PHONE_NUMBER));
        member.setEduLevel(getJsonFieldValue(fields, CoreConstants.JsonAssets.FAMILY_MEMBER.HIGHEST_EDUCATION_LEVEL));
        member.setEverSchool(getJsonFieldValue(fields, CoreConstants.JsonAssets.FAMILY_MEMBER.EVER_SCHOOL));
        member.setSchoolLevel(getJsonFieldValue(fields, CoreConstants.JsonAssets.FAMILY_MEMBER.SCHOOL_LEVEL));
        member.setPrimaryCareGiver(
                getJsonFieldValue(fields, CoreConstants.JsonAssets.PRIMARY_CARE_GIVER).equalsIgnoreCase("Yes") ||
                        getJsonFieldValue(fields, CoreConstants.JsonAssets.IS_PRIMARY_CARE_GIVER).equalsIgnoreCase("Yes")
        );
        member.setFamilyHead(false);

        return member;
    }

    public static FamilyEventClient processFamilyUpdateForm(AllSharedPreferences allSharedPreferences, String jsonString, String familyBaseEntityId) {
        return processFamilyForm(allSharedPreferences, jsonString, familyBaseEntityId, org.smartregister.family.util.Utils.metadata().familyRegister.updateEventType);
    }

    private static FamilyEventClient processFamilyForm(AllSharedPreferences allSharedPreferences, String jsonString, String familyBaseEntityId, String encounterType) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!(Boolean)registrationFormParams.getLeft()) {
                return null;
            } else {
                JSONObject jsonForm = (JSONObject)registrationFormParams.getMiddle();
                JSONArray fields = (JSONArray)registrationFormParams.getRight();
                String entityId = getString(jsonForm, "entity_id");
                if (StringUtils.isBlank(entityId)) {
                    entityId = generateRandomUUIDString();
                }

                lastInteractedWith(fields);
                dobUnknownUpdateFromAge(fields);
                Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
                if (baseClient != null && !baseClient.getBaseEntityId().equals(familyBaseEntityId)) {
                    baseClient.addRelationship(org.smartregister.family.util.Utils.metadata().familyMemberRegister.familyRelationKey, familyBaseEntityId);
                }

                Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, "metadata"), formTag(allSharedPreferences), entityId, encounterType, org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);
                tagSyncMetadata(allSharedPreferences, baseEvent);
                if (encounterType.equals(org.smartregister.family.util.Utils.metadata().familyRegister.updateEventType) && baseClient != null) {
                    updateFamilyMembersLastName(familyBaseEntityId, baseClient.getFirstName(), formTag(allSharedPreferences), allSharedPreferences);
                }

                return new FamilyEventClient(baseClient, baseEvent);
            }
        } catch (Exception var10) {
            Timber.e(var10);
            return null;
        }
    }

    private static void updateFamilyMembersLastName(String familyBaseEntityId, String familyName, FormTag formTag, AllSharedPreferences allSharedPreferences) throws Exception {
        List<org.apache.commons.lang3.tuple.Pair<Client, Event>> familyMembersEvents = processFamilyMemberUpdateFamilyName(familyBaseEntityId, familyName, formTag, allSharedPreferences);

        for (org.apache.commons.lang3.tuple.Pair<Client, Event> familyMembersEvent : familyMembersEvents) {

            JSONObject eventPartialJson = new JSONObject(JsonFormUtils.gson.toJson(familyMembersEvent.getRight()));
            getSyncHelper().addEvent(familyMembersEvent.getLeft().getBaseEntityId(), eventPartialJson);
            JsonFormUtils.mergeAndSaveClient(getSyncHelper(), familyMembersEvent.getLeft());
            NCUtils.processEvent(familyMembersEvent.getRight().getBaseEntityId(), new JSONObject(JsonFormUtils.gson.toJson(familyMembersEvent.getRight())));
        }
    }

    protected static List<org.apache.commons.lang3.tuple.Pair<Client, Event>> processFamilyMemberUpdateFamilyName(String familyBaseEntityId, String familyName, FormTag formTag, AllSharedPreferences allSharedPreferences) {
        List<CoreFamilyMemberModel> familyMembers = FamilyMemberDao.familyMembersToUpdateLastName(familyBaseEntityId);
        List<org.apache.commons.lang3.tuple.Pair<Client, Event>> clientEventPairsList = new ArrayList<>();


        if (familyName != null && familyMembers != null) {
            for (CoreFamilyMemberModel familyMember : familyMembers) {

                Event event = new Event()
                        .withFormSubmissionId(generateRandomUUIDString())
                        .withBaseEntityId(familyMember.getBaseEntityId())
                        .withEventType(familyMember.getEntityType().equals("ec_family_member") ?  UPDATE_FAMILY_MEMBER_REGISTRATION : UPDATE_CHILD_REGISTRATION)
                        .withEntityType(familyMember.getEntityType())
                        .withEventDate(new Date());
                event.withDateCreated(new Date());

                List<Obs> obs = new ArrayList<>();
                obs.add(getObs(Collections.singletonList(familyName),
                        Collections.singletonList(familyName)));
                event.setObs(obs);

                tagSyncMetadata(allSharedPreferences, event);

                // client
                Client client = (Client) new Client(familyMember.getBaseEntityId()).withLastName(familyName)
                        .withDateCreated(new Date());

                client.setLocationId(formTag.locationId);
                client.setTeamId(formTag.teamId);

                client.setClientApplicationVersion(formTag.appVersion);
                client.setClientApplicationVersionName(formTag.appVersionName);
                client.setClientDatabaseVersion(formTag.databaseVersion);


                clientEventPairsList.add(org.apache.commons.lang3.tuple.Pair.of(client, event));
            }
            return clientEventPairsList;
        }
        return clientEventPairsList;
    }

    public static ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    private static Obs getObs(List<Object> values, List<Object> humanReadableValues) {
        Obs obs = new Obs();
        obs.setFieldType("concept");
        obs.setFieldDataType("text");
        obs.setFieldCode("lastName");
        obs.setParentCode("");
        obs.setValues(values);
        obs.setFormSubmissionField("lastName");
        obs.setHumanReadableValues(humanReadableValues);
        return obs;
    }


    public static String getJsonFieldValue(JSONArray jsonArray, String key) {
        try {
            JSONObject jsonObject = getFieldJSONObject(jsonArray, key);
            if (jsonObject.has(org.smartregister.family.util.JsonFormUtils.VALUE)) {
                return jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);
            } else {
                return "";
            }
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return "";
    }

    public static Pair<List<Client>, List<Event>> processFamilyUpdateRelations(CoreChwApplication coreChwApplication, Context context, FamilyMember familyMember, String lastLocationId) throws Exception {
        List<Client> clients = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        ECSyncHelper syncHelper = coreChwApplication.getEcSyncHelper();
        JSONObject clientObject = syncHelper.getClient(familyMember.getFamilyID());
        Client familyClient = syncHelper.convert(clientObject, Client.class);
        if (familyClient == null) {
            String birthDate = clientObject.getString("birthdate");
            if (StringUtils.isNotBlank(birthDate)) {
                birthDate = birthDate.replace("-00:44:30", getTimeZone());
                clientObject.put("birthdate", birthDate);
            }

            familyClient = syncHelper.convert(clientObject, Client.class);
        }

        Map<String, List<String>> relationships = familyClient.getRelationships();

        if (familyMember.getPrimaryCareGiver()) {
            relationships.put(CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER, toStringList(familyMember.getMemberID()));
            familyClient.setRelationships(relationships);
        }

        if (familyMember.getFamilyHead()) {
            relationships.put(CoreConstants.RELATIONSHIP.FAMILY_HEAD, toStringList(familyMember.getMemberID()));
            familyClient.setRelationships(relationships);
        }

        clients.add(familyClient);


        JSONObject metadata = FormUtils.getInstance(context)
                .getFormJson(Utils.metadata().familyRegister.formName)
                .getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);

        metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

        FormTag formTag = new FormTag();
        formTag.providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
        formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();

        Event eventFamily = createEvent(new JSONArray(), metadata, formTag, familyMember.getFamilyID(),
                CoreConstants.EventType.UPDATE_FAMILY_RELATIONS, Utils.metadata().familyRegister.tableName);
        tagSyncMetadata(Utils.context().allSharedPreferences(), eventFamily);


        Event eventMember = createEvent(new JSONArray(), metadata, formTag, familyMember.getMemberID(),
                CoreConstants.EventType.UPDATE_FAMILY_MEMBER_RELATIONS,
                Utils.metadata().familyMemberRegister.tableName);
        tagSyncMetadata(Utils.context().allSharedPreferences(), eventMember);

        eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.PHONE_NUMBER.CODE, "",
                toList(familyMember.getPhone()), new ArrayList<>(), null, DBConstants.KEY.PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.CODE, CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.PARENT_CODE,
                toList(familyMember.getOtherPhone()), new ArrayList<>(), null, DBConstants.KEY.OTHER_PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", CoreConstants.FORM_CONSTANTS.CHANGE_CARE_GIVER.HIGHEST_EDU_LEVEL.CODE, "",
                toList(getEducationLevels(context).get(familyMember.getEduLevel())), toList(familyMember.getEduLevel()), null, DBConstants.KEY.HIGHEST_EDU_LEVEL));


        events.add(eventFamily);
        events.add(eventMember);

        return Pair.create(clients, events);
    }

    public static String getTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime);
        return localTime.substring(0, 3) + ":" + localTime.substring(3, 5);
    }

    public static List<String> toStringList(String... vals) {
        return new ArrayList<>(Arrays.asList(vals));
    }

    public static HashMap<String, String> getEducationLevels(Context context) {
        HashMap<String, String> educationLevels = new HashMap<>();
        educationLevels.put(context.getResources().getString(R.string.edu_level_none), "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_primary), "1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_secondary), "1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_post_secondary), "159785AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return educationLevels;
    }

    public static HashMap<String, String> getSchoolLevels(Context context) {
        HashMap<String, String> schoolLevels = new HashMap<>();
        schoolLevels.put(context.getResources().getString(R.string.school_level_none), "school_level_none");
        schoolLevels.put(context.getResources().getString(R.string.school_level_early_childhood), "school_level_early_childhood");
        schoolLevels.put(context.getResources().getString(R.string.school_level_primary), "school_level_primary");
        schoolLevels.put(context.getResources().getString(R.string.school_level_lower_secondary), "school_level_lower_secondary");
        schoolLevels.put(context.getResources().getString(R.string.school_level_upper_secondary), "school_level_upper_secondary");
        schoolLevels.put(context.getResources().getString(R.string.school_level_alternative), "school_level_alternative");
        schoolLevels.put(context.getResources().getString(R.string.school_level_Higher), "school_level_Higher");
        return schoolLevels;
    }

    public static HashMap<String, String> getEverSchoolOptions(Context context) {
        HashMap<String, String> everSchoolOptions = new HashMap<>();
        everSchoolOptions.put("Yes", "key_yes");
        everSchoolOptions.put("No", "key_no");
        return everSchoolOptions;
    }

    public static JSONObject getAncPncForm(Integer title_resource, String formName, MemberObject memberObject, Context context) {
        JSONObject form = null;
        boolean isPrimaryCareGiver = memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId());
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
        CommonPersonObject personObject = commonRepository.findByBaseEntityId(memberObject.getBaseEntityId());
        CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());
        if (formName.equals(CoreConstants.JSON_FORM.getAncRegistration())) {
            form = getAutoJsonEditAncFormString(
                    memberObject.getBaseEntityId(), context, formName, CoreConstants.EventType.UPDATE_ANC_REGISTRATION, context.getResources().getString(title_resource));
        } else if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {
            form = getAutoPopulatedJsonEditMemberFormString(
                    (title_resource != null) ? context.getResources().getString(title_resource) : null,
                    CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                    context, client, Utils.metadata().familyMemberRegister.updateEventType, memberObject.getFamilyName(), isPrimaryCareGiver);
        }
        return form;
    }

    public static JSONObject getAutoJsonEditAncFormString(String baseEntityID, Context context, String formName, String eventType, String title) {
        try {

            Event event = getEditAncLatestProperties(baseEntityID);
            final List<Obs> observations = event.getObs();
            JSONObject form = getFormWithMetaData(baseEntityID, context, formName, eventType);
            if (form != null) {
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(TITLE, title);
                }
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(KEY).equalsIgnoreCase("last_menstrual_period") ||
                            jsonObject.getString(KEY).equalsIgnoreCase("delivery_method")) {
                        jsonObject.put(READ_ONLY, true);
                    }
                    try {
                        for (Obs obs : observations) {
                            if (obs.getFormSubmissionField().equalsIgnoreCase(jsonObject.getString(KEY))) {
                                if (jsonObject.getString("type").equals("spinner")) {
                                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getHumanReadableValues().get(0));
                                } else {
                                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getValue());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                return form;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private static Event getEditAncLatestProperties(String baseEntityID) {

        Event ecEvent = null;

        String query_event = String.format("select json from event where baseEntityId = '%s' and eventType in ('%s','%s') order by updatedAt desc limit 1;",
                baseEntityID, CoreConstants.EventType.UPDATE_ANC_REGISTRATION, CoreConstants.EventType.ANC_REGISTRATION);

        try (Cursor cursor = CoreChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_event, new String[]{})) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                ecEvent = AssetHandler.jsonStringToJava(cursor.getString(0), Event.class);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        }
        return ecEvent;
    }

    public static Intent getAncPncStartFormIntent(JSONObject jsonForm, Context context) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        return intent;
    }

    public static JSONObject getAutoPopulatedJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {
        return new BAJsonFormUtils(CoreChwApplication.getInstance()).getAutoJsonEditMemberFormString(title, formName, context, client, eventType, familyName, isPrimaryCaregiver);
    }

    public static List<JSONObject> getFormSteps(JSONObject jsonObject) throws JSONException {
        List<JSONObject> steps = new ArrayList<>();
        int x = 1;
        while (true) {
            String step_name = "step" + x;
            if (jsonObject.has(step_name)) {
                steps.add(jsonObject.getJSONObject(step_name));
            } else {
                break;
            }
            x++;
        }
        return steps;
    }

    public static void populateJsonForm(@NotNull JSONObject jsonObject, @NotNull Map<String, String> valueMap) throws JSONException {
        Map<String, String> _valueMap = new HashMap<>(valueMap);
        int step = 1;
        while (jsonObject.has("step" + step)) {
            JSONObject jsonStepObject = jsonObject.getJSONObject("step" + step);
            JSONArray array = jsonStepObject.getJSONArray(JsonFormConstants.FIELDS);
            int position = 0;
            while (position < array.length() && _valueMap.size() > 0) {

                JSONObject object = array.getJSONObject(position);
                String key = object.getString(JsonFormConstants.KEY);

                if (_valueMap.containsKey(key)) {
                    object.put(JsonFormConstants.VALUE, _valueMap.get(key));
                    _valueMap.remove(key);
                }

                position++;
            }

            step++;
        }
    }

    public static void addLocationsToDropdownField(List<Location> locations, JSONObject dropdownField) throws JSONException {
        if (dropdownField != null) {
            Collections.sort(locations, (firstLocation, secondLocation) ->
                    firstLocation.getProperties().getName().compareTo(secondLocation.getProperties().getName()));

            if (dropdownField.has(JsonFormConstants.TYPE) && dropdownField.getString(JsonFormConstants.TYPE)
                    .equalsIgnoreCase(JsonFormConstants.SPINNER)) {

                JSONArray values = new JSONArray();
                JSONObject openMrsChoiceIds = new JSONObject();
                JSONObject locationUUIDs = new JSONObject();

                for (Location location : locations) {
                    LocationProperty locationProperties = location.getProperties();
                    values.put(locationProperties.getName());
                    openMrsChoiceIds.put(locationProperties.getName(), locationProperties.getName());
                    locationUUIDs.put(locationProperties.getName(), location.getId());
                }

                dropdownField.put(JsonFormConstants.VALUES, values);
                dropdownField.put(JsonFormConstants.KEYS, values);
                dropdownField.put(JsonFormConstants.OPENMRS_CHOICE_IDS, openMrsChoiceIds);
                dropdownField.put(LOCATION_UUIDS, locationUUIDs);
            }
        }
    }

    public static String getSyncLocationUUIDFromDropdown(JSONObject dropdownField) throws JSONException {
        if (dropdownField.has(JsonFormConstants.TYPE) && dropdownField.getString(JsonFormConstants.TYPE)
                .equalsIgnoreCase(JsonFormConstants.SPINNER) && dropdownField.has(JsonFormConstants.VALUE)) {
            String fieldValue = dropdownField.getString(JsonFormConstants.VALUE);
            if (dropdownField.has(LOCATION_UUIDS) && dropdownField.getJSONObject(LOCATION_UUIDS).has(fieldValue)) {
                return dropdownField.getJSONObject(LOCATION_UUIDS).getString(fieldValue);
            }
        }
        return null;
    }

    public static JSONObject getJsonField(JSONObject form, String step, String key) {
        JSONArray field = fields(form, step);
        return getFieldJSONObject(field, key);
    }
}
