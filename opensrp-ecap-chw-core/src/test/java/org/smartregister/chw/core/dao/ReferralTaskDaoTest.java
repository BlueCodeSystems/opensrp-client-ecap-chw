package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.Task;
import org.smartregister.repository.Repository;

import java.util.List;

import static org.smartregister.domain.Task.TaskStatus.READY;

public class ReferralTaskDaoTest extends ReferralTaskDao {
    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testGetToBeCompletedReferralTasks() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "plan_id", "group_id", "status", "business_status", "priority", "code", "description",
                "focus", "for", "start", "end", "authored_on", "last_modified", "owner", "sync_status", "server_version", "structure_id", "reason_reference", "location", "requester"});
        matrixCursor.addRow(new Object[]{"1", "plan", "group", READY, "Complete", "ROUTINE", "ANC", "referred", "ANC Visit",
                "location.properties.uid:415-2342fc", "1589962159596", "1589962159596", "1589962159596", "1589962159596", "chwone",
                1, "1566393632423", "structure._id.33efadf1-feda-4861-a979-ff4f7cec9ea7", "assigned to", "2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8", "chwone"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<Task> taskList = ReferralTaskDao.getToBeCompletedReferralTasks();

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(taskList.get(0).getIdentifier(), "1");
    }

}