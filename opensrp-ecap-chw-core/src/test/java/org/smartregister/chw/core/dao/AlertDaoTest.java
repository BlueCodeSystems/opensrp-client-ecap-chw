package org.smartregister.chw.core.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.Repository;

public class AlertDaoTest extends AlertDao {

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
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"scheduleName"});
        matrixCursor.addRow(new Object[]{"1232434"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertDao.getActiveAlerts("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testGetAlert() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"scheduleName"});
        matrixCursor.addRow(new Object[]{"1232434"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertDao.getActiveAlert("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testGetActiveAlertsForVaccines() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"scheduleName"});
        matrixCursor.addRow(new Object[]{"1232434"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertDao.getActiveAlertsForVaccines("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testGetActiveAlert() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"scheduleName"});
        matrixCursor.addRow(new Object[]{"1232434"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertDao.getActiveAlertsForVaccines("123456");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testUpdateOfflineVaccineAlerts() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"scheduleName"});
        matrixCursor.addRow(new Object[]{"1232434"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertDao.updateOfflineVaccineAlerts("123456");

        Mockito.verify(database, Mockito.times(2)).rawQuery(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void getFamilyAlertStatusReturnsCorrectStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"case"});
        matrixCursor.addRow(new Object[]{"2"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        AlertStatus status = AlertDao.getFamilyAlertStatus("entity-id-123");
        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());
        Assert.assertEquals(AlertStatus.complete, status);
    }

}
