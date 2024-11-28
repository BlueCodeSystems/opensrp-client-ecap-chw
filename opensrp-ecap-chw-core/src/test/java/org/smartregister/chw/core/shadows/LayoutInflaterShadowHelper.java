package org.smartregister.chw.core.shadows;

import android.content.Context;
import android.view.LayoutInflater;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(LayoutInflater.class)
public class LayoutInflaterShadowHelper {

    private static LayoutInflater layoutInflater;

    @Implementation
    public static LayoutInflater from(Context context) {
        if (layoutInflater == null) {
            layoutInflater = Mockito.mock(LayoutInflater.class);
        }
        return layoutInflater;
    }

}
