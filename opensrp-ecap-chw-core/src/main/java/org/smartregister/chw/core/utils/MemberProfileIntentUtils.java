package org.smartregister.chw.core.utils;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;

/**
 * Several base library activities (e.g. BaseAncUpcomingServicesActivity,
 * BaseAncMedicalHistoryActivity) read a MemberObject out of the launching Intent's extras and
 * dereference it unconditionally in their own onCreate()/setUpView(), before a subclass gets any
 * chance to intervene. When Android relaunches one of these activities without its original
 * Intent extras (recent-tasks restore, process death), that MemberObject is null and the base
 * class throws an NPE we cannot catch from outside.
 * <p>
 * Since super.onCreate() must always be called, the only way to avoid that crash is to seed a
 * placeholder MemberObject into the Intent before delegating to it.
 */
public final class MemberProfileIntentUtils {

    private MemberProfileIntentUtils() {
    }

    public static boolean hasMemberObjectExtra(Activity activity) {
        Intent intent = activity.getIntent();
        return intent != null
                && intent.getSerializableExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT) != null;
    }

    /**
     * Ensures the launching Intent carries a non-null MemberObject extra, inserting an empty
     * placeholder if it's missing, so the base activity's onCreate() doesn't NPE.
     *
     * @return true if a placeholder had to be inserted (i.e. the real data was missing)
     */
    public static boolean ensureMemberObjectExtra(Activity activity) {
        if (hasMemberObjectExtra(activity)) {
            return false;
        }
        Intent intent = activity.getIntent();
        if (intent == null) {
            intent = new Intent();
            activity.setIntent(intent);
        }
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject());
        return true;
    }
}
