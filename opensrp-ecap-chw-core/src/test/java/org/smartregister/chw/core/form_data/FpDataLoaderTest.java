package org.smartregister.chw.core.form_data;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.core.dataloader.FPDataLoader;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;

import java.util.List;

public class FpDataLoaderTest {

    @Test
    public void getEventTypesReturnsFamilyPlanningEventTypes() {
        List<String> eventTypeList = new FPDataLoader("").getEventTypes();
        Assert.assertEquals(2, eventTypeList.size());
        Assert.assertEquals(FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, eventTypeList.get(0));
        Assert.assertEquals(FamilyPlanningConstants.EventType.UPDATE_FAMILY_PLANNING_REGISTRATION, eventTypeList.get(1));
    }
}
