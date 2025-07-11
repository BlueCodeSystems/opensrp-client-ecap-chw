package org.smartregister.chw.core.shadows;

import android.content.Context;

import org.robolectric.annotation.Implements;
import org.smartregister.util.FormUtils;

@Implements(FormUtils.class)
public class FormUtilsShadowHelper {

    public void __constructor__(Context context) {
        //do nothing
    }
}
