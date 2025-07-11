package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BaseChwNotificationModelTest {

    private BaseChwNotificationModel chwNotificationModel;

    @Before
    public void setUp() {
        chwNotificationModel = Mockito.spy(BaseChwNotificationModel.class);
    }

    @Test
    public void mainColumns() {
        String[] mainColumns = chwNotificationModel.mainColumns("ec_anc_danger_signs", "entity_table");
        Assert.assertEquals(17, mainColumns.length);
        Assert.assertEquals(mainColumns[0], "task.owner");
        Assert.assertEquals(mainColumns[7], "entity_table.gender");
        Assert.assertEquals(mainColumns[16], "entity_table.middle_name");
    }
}