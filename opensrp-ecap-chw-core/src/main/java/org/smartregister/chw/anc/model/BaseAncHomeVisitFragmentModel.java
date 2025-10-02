package org.smartregister.chw.anc.model;

import android.content.res.Resources;
import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitFragmentContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.opensrp_chw_anc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseAncHomeVisitFragmentModel implements BaseAncHomeVisitFragmentContract.Model {


    @Override
    public void processJson(JSONObject jsonObject, BaseAncHomeVisitFragmentContract.Presenter presenter) {
        if (jsonObject != null) {

            // extract title , question and view type and notify the view
            String title = getTitle(jsonObject);
            String question = getQuestion(jsonObject);
            int image = getImage(jsonObject, presenter);
            BaseAncHomeVisitFragment.QuestionType questionType = getQuestionType(jsonObject);
            String value = getValue(jsonObject);
            String infoIconTitle = getInfoIconTitle(jsonObject);
            String infoIconDetails = getInfoIconDetails(jsonObject);
            List<JSONObject> options = getOptions(jsonObject);
            Map<String, String> dateConstraints = getDateConstraints(jsonObject);

            presenter.setTitle(title);
            presenter.setQuestion(question);
            presenter.setImageRes(image);
            presenter.setQuestionType(questionType);
            presenter.setInfoIconTitle(infoIconTitle);
            presenter.setInfoIconDetails(infoIconDetails);
            presenter.setOptions(options);
            presenter.setDateConstraints(dateConstraints);

            // must always be the last
            presenter.setValue(value);
        }
    }

    @Override
    public void writeValue(JSONObject jsonObject, String value) {
        if (jsonObject != null) {
            try {
                jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0).put(JsonFormConstants.VALUE, value);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }


    private String getTitle(JSONObject jsonObject) {
        try {
            return jsonObject.getJSONObject(JsonFormConstants.STEP1).getString(JsonFormConstants.STEP_TITLE);
        } catch (JSONException e) {
            Timber.e(e);
            return "";
        }
    }

    private String getQuestion(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            if (targetObject.has(JsonFormConstants.HINT))
                return targetObject.getString(JsonFormConstants.HINT);

        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    private int getImage(JSONObject jsonObject, BaseAncHomeVisitFragmentContract.Presenter presenter) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);

            String image = (targetObject.has(JsonFormUtils.IMAGE)) ? targetObject.getString(JsonFormUtils.IMAGE) : "";
            if (presenter.getView() != null && StringUtils.isNotBlank(image)) {
                Resources resources = presenter.getView().getMyContext().getResources();
                return resources.getIdentifier(image, "drawable",
                        presenter.getView().getMyContext().getPackageName());
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return R.drawable.badge;
    }

    private BaseAncHomeVisitFragment.QuestionType getQuestionType(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);

            String type = (targetObject.has(JsonFormConstants.TYPE)) ? targetObject.getString(JsonFormConstants.TYPE) : "";

            switch (type) {
                case JsonFormConstants.SPINNER:
                    return BaseAncHomeVisitFragment.QuestionType.BOOLEAN;
                case JsonFormConstants.CHECK_BOX:
                    return BaseAncHomeVisitFragment.QuestionType.MULTI_OPTIONS;
                case JsonFormConstants.RADIO_BUTTON:
                    return BaseAncHomeVisitFragment.QuestionType.RADIO;
                case JsonFormConstants.DATE_PICKER:
                    return BaseAncHomeVisitFragment.QuestionType.DATE_SELECTOR;
                default:
                    return BaseAncHomeVisitFragment.QuestionType.BOOLEAN;
            }
        } catch (JSONException e) {
            Timber.e(e);
            return BaseAncHomeVisitFragment.QuestionType.BOOLEAN;
        }
    }

    private String getInfoIconTitle(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            if (targetObject.has(JsonFormConstants.LABEL_INFO_TITLE))
                return targetObject.getString(JsonFormConstants.LABEL_INFO_TITLE);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    private String getInfoIconDetails(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            if (targetObject.has(JsonFormConstants.LABEL_INFO_TEXT))
                return targetObject.getString(JsonFormConstants.LABEL_INFO_TEXT);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    private String getValue(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            if (targetObject.has(JsonFormConstants.VALUE))
                return targetObject.getString(JsonFormConstants.VALUE);

            return "";
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    private List<JSONObject> getOptions(JSONObject jsonObject) {
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            if (targetObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                JSONArray array = targetObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                List<JSONObject> objects = new ArrayList<>();
                int count = array.length();
                int x = 0;
                while (x < count) {
                    objects.add(array.getJSONObject(x));
                    x++;
                }

                return objects;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    private Map<String, String> getDateConstraints(JSONObject jsonObject) {
        Map<String, String> dateConstraints = new HashMap<>();
        String minDateString = "";
        String maxDateString = "";
        try {
            JSONObject targetObject = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS).getJSONObject(0);
            minDateString = (targetObject.has(JsonFormConstants.MIN_DATE)) ? targetObject.getString(JsonFormConstants.MIN_DATE) : "";
            maxDateString = (targetObject.has(JsonFormConstants.MAX_DATE)) ? targetObject.getString(JsonFormConstants.MAX_DATE) : "";
        } catch (JSONException je) {
            Timber.e(je);
        }

        if (!TextUtils.isEmpty(minDateString))
            dateConstraints.put(JsonFormConstants.MIN_DATE, minDateString);

        if (!TextUtils.isEmpty(maxDateString))
            dateConstraints.put(JsonFormConstants.MAX_DATE, maxDateString);

        return dateConstraints;
    }
}
