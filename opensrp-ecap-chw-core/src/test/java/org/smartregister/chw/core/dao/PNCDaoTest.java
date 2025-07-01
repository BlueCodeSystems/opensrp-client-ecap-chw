package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.model.ChildModel;
import org.smartregister.repository.Repository;

import java.util.Date;
import java.util.List;

public class PNCDaoTest extends PNCDao {

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
    public void testGetPNCDeliveryDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"delivery_date"});
        matrixCursor.addRow(new Object[]{getNativeFormsDateFormat().format(new Date())});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        Date date = PNCDao.getPNCDeliveryDate("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(getNativeFormsDateFormat().format(new Date()).equals(getNativeFormsDateFormat().format(date)));
    }

    @Test
    public void testisPNCMember() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{1});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        boolean state = PNCDao.isPNCMember("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertTrue(state);
    }

    @Test
    public void testGetPncWomenCount() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count"});
        matrixCursor.addRow(new Object[]{"4"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        int count = PNCDao.getPncWomenCount("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(count, 4);
    }

    @Test
    public void testGetMember() {

        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id"});
        matrixCursor.addRow(new Object[]{"12345"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        MemberObject memberObject = PNCDao.getMember("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(memberObject.getBaseEntityId(), "12345");
    }

    @Test
    public void testChildrenForPncWoman() {

        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"child_name"});
        matrixCursor.addRow(new Object[]{"Rob"});
        matrixCursor.addRow(new Object[]{"Tony"});
        matrixCursor.addRow(new Object[]{"Abednego"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        List<ChildModel> childModels = PNCDao.childrenForPncWoman("123456");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(childModels.size(), 3);
    }

    @Test
    public void testEarlyBreastFeeding() {

        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"early_bf_1hr"});
        matrixCursor.addRow(new Object[]{"Yes"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());
        PNCDao.earlyBreastFeeding("123456", "24324234");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }
}
