package com.bluecodeltd.ecap.chw.sync;

import android.content.Context;

import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.sync.ClientProcessorForJava;

public class ChwClientProcessor extends CoreClientProcessor {
    private ChwClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ChwClientProcessor(context);
        }
        return instance;
    }

}
