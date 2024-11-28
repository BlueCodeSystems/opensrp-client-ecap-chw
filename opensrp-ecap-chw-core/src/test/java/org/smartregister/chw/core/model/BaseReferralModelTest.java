package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BaseReferralModelTest {

    private BaseReferralModel referralModel;

    @Before
    public void setUp() {
        referralModel = Mockito.spy(BaseReferralModel.class);
    }

    @Test
    public void countSelect() {
        String countSelect = referralModel.countSelect("task", "date_removed = null");
        Assert.assertEquals(countSelect, "SELECT COUNT(*) FROM task WHERE date_removed = null ");
    }

    @Test
    public void mainSelect() {
        String mainSelect = referralModel.mainSelect("task", "entity_table", "date_removed = null");
        Assert.assertEquals(mainSelect, "Select task._id as _id , task.focus , task.requester , task.start FROM task WHERE date_removed = null ");
    }

    @Test
    public void mainColumns() {
        String[] mainColumns = referralModel.mainColumns("task", "ec_task_table");
        Assert.assertEquals(3, mainColumns.length);
        Assert.assertEquals(mainColumns[0], "task.focus");
        Assert.assertEquals(mainColumns[1], "task.requester");
        Assert.assertEquals(mainColumns[2], "task.start");
    }
}