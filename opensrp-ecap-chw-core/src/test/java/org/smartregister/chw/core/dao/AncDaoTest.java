package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.repository.Repository;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AncDaoTest extends AncDao {

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
    public void testGetAncDateCreated() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"date_created"});
        matrixCursor.addRow(new Object[]{"2019-09-24T03:00:00.000+03:00"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        String dateCreated = AncDao.getAncDateCreated("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(dateCreated, "2019-09-24T03:00:00.000+03:00");
    }

    @Test
    public void testIsANCMember() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{"1"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        boolean isANC = AncDao.isANCMember("6238d8aa-6632-47f9-8a88-26bfedd942c2");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(isANC);
    }

    @Test
    public void testGetMember() {

        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id"});
        matrixCursor.addRow(new Object[]{"12345"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        MemberObject memberObject = AncDao.getMember("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(memberObject.getBaseEntityId(), "12345");
    }

    @Test
    public void testGetAncWomenCount() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{"20"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        int womenCount = AncDao.getAncWomenCount("55874d8aa-6632-47f9-8a88-26bfedd942c2");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(womenCount, 20);
    }

    @Test
    public void testShowTT() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{"1"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        boolean showTT = AncDao.showTT("623987d8aa-6632-47f9-8a88-26bfedd942c2");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(showTT);
    }

    @Test
    public void testGetTestDone() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"human_readable_details"});
        matrixCursor.addRow(new Object[]{"HIV test"});
        matrixCursor.addRow(new Object[]{"Malaria test"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<String> testDone = AncDao.getTestDone("56776ef1-6679-4a86-b6d4-9d30554cb2ec");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(testDone.size(), 2);
    }

}
