package org.smartregister.chw.core.form_data;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

public class NativeFormsFormLoader implements FormLoader {
    @Override
    public JSONObject getJsonForm(Context context, String fileName) throws Exception {
        return FormUtils.getInstance(context).getFormJson(fileName);
    }

}
