package org.smartregister.chw.core.utils;

import androidx.annotation.Nullable;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;

import java.util.Date;

public class MalariaVisitUtil {

    public static MalariaFollowUpRule getMalariaStatus(Date malariaTestDate , @Nullable Date followUpDate) {
        MalariaFollowUpRule malariaFollowUpRule =
                new MalariaFollowUpRule(malariaTestDate, followUpDate);
        CoreChwApplication.getInstance().getRulesEngineHelper().getMalariaRule(malariaFollowUpRule, CoreConstants.RULE_FILE.MALARIA_FOLLOW_UP_VISIT);

        return malariaFollowUpRule;
    }
}
