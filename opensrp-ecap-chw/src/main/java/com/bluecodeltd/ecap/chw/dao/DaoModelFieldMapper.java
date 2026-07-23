package com.bluecodeltd.ecap.chw.dao;

import android.database.Cursor;

import java.lang.reflect.Method;

public final class DaoModelFieldMapper {

    private DaoModelFieldMapper() {
    }

    public static void captureAdditionalFields(Cursor cursor, Object model) {
        if (cursor == null || model == null) {
            return;
        }

        Method setter = findAdditionalFieldSetter(model.getClass());
        if (setter == null) {
            return;
        }

        String[] names = cursor.getColumnNames();
        if (names == null || names.length == 0) {
            return;
        }

        for (String name : names) {
            if (name == null) {
                continue;
            }
            try {
                String value = cursor.getString(cursor.getColumnIndex(name));
                setter.invoke(model, name, value);
            } catch (Exception ignored) {
                // Best-effort capture only.
            }
        }
    }

    private static Method findAdditionalFieldSetter(Class<?> modelClass) {
        try {
            return modelClass.getMethod("setAdditionalField", String.class, String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
