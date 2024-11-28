package org.smartregister.chw.core.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.job.BaseJob;

@Implements(BaseJob.class)
public class BaseJobShadowUtil {
    public static String scheduleJobMN = "scheduleJob(String, Long, Long)";
    public static String scheduleJobImmediatelyMN = "scheduleJobImmediately(String)";
    private static ShadowHelper shadowHelper = new ShadowHelper();

    @Implementation
    public static void scheduleJob(String jobTag, Long start, Long flex) {
        shadowHelper.addMethodCall(scheduleJobMN, jobTag, start, flex);
    }

    @Implementation
    public static void scheduleJobImmediately(String jobTag) {
        shadowHelper.addMethodCall(scheduleJobImmediatelyMN, jobTag);
    }

    public static ShadowHelper getShadowHelper() {
        return shadowHelper;
    }

}
