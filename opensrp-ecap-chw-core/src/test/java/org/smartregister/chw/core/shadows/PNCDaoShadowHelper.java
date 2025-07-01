package org.smartregister.chw.core.shadows;

import org.joda.time.DateTime;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.model.ChildModel;

import java.util.ArrayList;
import java.util.List;

@Implements(PNCDao.class)
public class PNCDaoShadowHelper {

    @Implementation
    public static List<ChildModel> childrenForPncWoman(String baseEntityId) {
        List<ChildModel> childrenModels = new ArrayList<>();
        childrenModels.add(new ChildModel("Energizer Bunny", DateTime.now().minusMonths(6).toString(), "Manson", "test-entity-id"));
        return childrenModels;
    }

}
