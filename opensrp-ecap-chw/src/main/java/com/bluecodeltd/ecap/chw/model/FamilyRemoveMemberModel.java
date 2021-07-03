package com.bluecodeltd.ecap.chw.model;

import org.smartregister.chw.anc.util.DBConstants;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.model.CoreFamilyRemoveMemberModel;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Date;

public class FamilyRemoveMemberModel extends CoreFamilyRemoveMemberModel {

    @Override
    public String getForm(CommonPersonObjectClient client) {
        Date dob = Utils.dobStringToDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        if (ChwApplication.getApplicationFlavor().showChildrenUnderFiveAndGirlsAgeNineToEleven()) {
            return ((dob != null && getDiffYears(dob, new Date()) >= 11) ? CoreConstants.JSON_FORM.getFamilyDetailsRemoveMember() : CoreConstants.JSON_FORM.getFamilyDetailsRemoveChild());
        }
        return ((dob != null && getDiffYears(dob, new Date()) >= 5) ? CoreConstants.JSON_FORM.getFamilyDetailsRemoveMember() : CoreConstants.JSON_FORM.getFamilyDetailsRemoveChild());
    }
}

