package org.smartregister.chw.core.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.EditTextFactory;

import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.LookUpTextWatcherUtil;
import org.smartregister.chw.core.watchers.HIA2ReportFormTextWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceEditTextFactory extends EditTextFactory {

    @Override
    public void attachLayout(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                             MaterialEditText editText, ImageView editable) throws Exception {
        super.attachLayout(stepName, context, formFragment, jsonObject, editText, editable);

        if (jsonObject.has(CoreConstants.KeyIndicatorsUtil.HIA_2_INDICATOR)) {
            editText.setTag(jsonObject.get(CoreConstants.KeyIndicatorsUtil.HIA_2_INDICATOR));

            editText.addTextChangedListener(new HIA2ReportFormTextWatcher(formFragment, jsonObject.get(CoreConstants.KeyIndicatorsUtil.HIA_2_INDICATOR).toString()));
        }
    }

    private class ChildEditTextFactory extends EditTextFactory {

        @Override
        public void attachLayout(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                 MaterialEditText editText, ImageView editable) throws Exception {
            super.attachLayout(stepName, context, formFragment, jsonObject, editText, editable);

            if (jsonObject.has(CoreConstants.KeyIndicatorsUtil.LOOK_UP) &&
                    jsonObject.get(CoreConstants.KeyIndicatorsUtil.LOOK_UP).toString().equalsIgnoreCase(Boolean.TRUE.toString())) {

                String entityId = jsonObject.getString(CoreConstants.KeyIndicatorsUtil.ENTITY_ID);

                Map<String, List<View>> lookupMap = formFragment.getLookUpMap();

                List<View> lookUpViews = new ArrayList<>();
                if (lookupMap.containsKey(entityId)) {
                    lookUpViews = lookupMap.get(entityId);
                }

                if (!lookUpViews.contains(editText)) {
                    lookUpViews.add(editText);
                }
                lookupMap.put(entityId, lookUpViews);

                editText.addTextChangedListener(new LookUpTextWatcherUtil(editText, entityId));
                editText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            }

        }
    }
}
