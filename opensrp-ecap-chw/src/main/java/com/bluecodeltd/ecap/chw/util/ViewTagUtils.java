package com.bluecodeltd.ecap.chw.util;

import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;

public final class ViewTagUtils {

    private ViewTagUtils() {
    }

    public static CommonPersonObjectClient getTaggedClient(View view) {
        if (view == null) return null;

        Object tag = view.getTag();
        if (tag instanceof CommonPersonObjectClient) {
            return (CommonPersonObjectClient) tag;
        }

        View parent = (view.getParent() instanceof View) ? (View) view.getParent() : null;
        while (parent != null) {
            Object parentTag = parent.getTag();
            if (parentTag instanceof CommonPersonObjectClient) {
                return (CommonPersonObjectClient) parentTag;
            }
            parent = (parent.getParent() instanceof View) ? (View) parent.getParent() : null;
        }

        return null;
    }
}
