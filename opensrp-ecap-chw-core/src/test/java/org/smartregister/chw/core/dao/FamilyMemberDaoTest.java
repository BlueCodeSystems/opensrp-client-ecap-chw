package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.model.CoreFamilyMemberModel;
import org.smartregister.repository.Repository;

import java.util.List;

public class FamilyMemberDaoTest extends FamilyMemberDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
        Mockito.doReturn(database).when(repository).getReadableDatabase();
    }

    @Test
    public void testGetActiveAlerts() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"last_name", "base_entity_id", "relational_id", "entity_type"});
        matrixCursor.addRow(new Object[]{"demo_last_name", "123456", "654321", "Update"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<CoreFamilyMemberModel> familyMembers = FamilyMemberDao.familyMembersToUpdateLastName("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());

        Assert.assertNotNull(familyMembers);
        Assert.assertEquals(familyMembers.get(0).getLastName(), "demo_last_name");
    }

}
