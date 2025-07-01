package org.smartregister.chw.core.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class LookUpTextWatcherUtil implements TextWatcher {
    private final View mView;
    private final String mEntityId;
    private Map<String, EntityLookUpUtil> lookUpMap;

    public LookUpTextWatcherUtil(View view, String entityId) {
        mView = view;
        mEntityId = entityId;
        lookUpMap = new HashMap<>();
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Timber.v("beforeTextChanged");
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        Timber.v("onTextChanged");
    }

    public void afterTextChanged(Editable editable) {
        String text = (String) mView.getTag(com.vijay.jsonwizard.R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        String key = (String) mView.getTag(com.vijay.jsonwizard.R.id.key);

        boolean afterLookUp = (Boolean) mView.getTag(com.vijay.jsonwizard.R.id.after_look_up);
        if (afterLookUp) {
            mView.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            return;
        }

        EntityLookUpUtil entityLookUp = new EntityLookUpUtil();
        if (lookUpMap.containsKey(mEntityId)) {
            entityLookUp = lookUpMap.get(mEntityId);
        }

        if (StringUtils.isBlank(text)) {
            if (entityLookUp.containsKey(key)) {
                entityLookUp.remove(key);
            }
        } else {
            entityLookUp.put(key, text);
        }

        lookUpMap.put(mEntityId, entityLookUp);
    }

}
