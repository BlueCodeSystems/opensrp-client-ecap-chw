package com.bluecodeltd.ecap.chw.dao;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static org.smartregister.chw.core.utils.Utils.getDateDifferenceInDays;

import android.database.Cursor;

import com.bluecodeltd.ecap.chw.domain.CaseStatus;
import com.bluecodeltd.ecap.chw.domain.Mother;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.smartregister.chw.core.domain.Child;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class CaseStatusDao extends AbstractDao {
    public static String getActiveStatus(String baseEntityID) {
        String sql = "select count(*) count FROM ec_client_index\n" +
                "    where base_entity_id = '" + baseEntityID + "'";
        DataMap<CaseStatus> dataMap = c -> {
            return new CaseStatus(
                    getCursorValue(c, "status")
            );
        };

        List<CaseStatus> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() != 1)
            return null;

        return values.get(0).getStatus();
    }
}


