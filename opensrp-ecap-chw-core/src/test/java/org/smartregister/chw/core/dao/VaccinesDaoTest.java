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
import org.smartregister.repository.Repository;

@RunWith(MockitoJUnitRunner.class)
public class VaccinesDaoTest extends ChildDao {

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
    public void testGetVaccineName() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"vaccineName", "12345"});
        matrixCursor.addRow(new Object[]{"demo name", "formSubmissionId"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String vaccineName = VaccinesDao.getVaccineName("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());

        Assert.assertEquals("demo name", vaccineName);
    }

    @Test
    public void testGetVaccineDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"vaccineDate", "12345"});
        matrixCursor.addRow(new Object[]{"23/02/2020", "formSubmissionId"});

        Mockito.doReturn(matrixCursor).when(database).rawQuery(Mockito.any(), Mockito.any());

        String vaccineDate = VaccinesDao.getVaccineDate("12345");

        Mockito.verify(database).rawQuery(Mockito.anyString(), Mockito.any());

        Assert.assertEquals("23/02/2020", vaccineDate);
    }
}
