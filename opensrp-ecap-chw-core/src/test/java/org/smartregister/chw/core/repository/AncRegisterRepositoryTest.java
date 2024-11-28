package org.smartregister.chw.core.repository;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.BaseRobolectricTest;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;

import static org.smartregister.chw.core.repository.AncRegisterRepository.BASE_ENTITY_ID;
import static org.smartregister.repository.BaseRepository.COLLATE_NOCASE;

public class AncRegisterRepositoryTest extends BaseRobolectricTest {

    private AncRegisterRepository repository;

    @Mock
    private SQLiteDatabase database;

    private String baseEntityId;

    @Before
    public void setUp() {
        baseEntityId = "some-base-entity-id";
        repository = Mockito.spy(new AncRegisterRepository());
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        Mockito.doReturn(database).when(repository).getWritableDatabase();
    }

    @Test
    public void getFamilyNameAndPhone() {
        MatrixCursor cursor = new MatrixCursor(AncRegisterRepository.TABLE_COLUMNS);
        cursor.addRow(new String[]{"Ed","Edd", "Eddy", "123"});
        String selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{baseEntityId};
        Mockito.doReturn(cursor).when(database).query(AncRegisterRepository.TABLE_NAME, AncRegisterRepository.TABLE_COLUMNS, 
                selection, selectionArgs,  null, null, null);
        HashMap<String, String> familyNameAndPhone = repository.getFamilyNameAndPhone(baseEntityId);
        Assert.assertEquals(familyNameAndPhone.size(), 2);
        Assert.assertEquals(familyNameAndPhone.get(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME),"Ed Edd Eddy");
        Assert.assertEquals(familyNameAndPhone.get(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE),"123");
    }

    @Test
    public void getGaIfAncWoman() {
        MatrixCursor cursor = new MatrixCursor(AncRegisterRepository.LAST_MENSTRUAL_PERIOD_COLUMNS);
        cursor.addRow(new String[]{"2019-02-06"});
        String selection = DBConstants.KEY.BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " +
                org.smartregister.chw.anc.util.DBConstants.KEY.IS_CLOSED + " = ? " + COLLATE_NOCASE;
        String[] selectionArgs = new String[]{baseEntityId, "0"};
        Mockito.doReturn(cursor).when(database).query(CoreConstants.TABLE_NAME.ANC_MEMBER,
                AncRegisterRepository.LAST_MENSTRUAL_PERIOD_COLUMNS, selection, selectionArgs, null, null, null);
        String womanGA = repository.getGaIfAncWoman(baseEntityId);
        Assert.assertEquals(womanGA, "2019-02-06");
    }

    @Test
    public void getAncCommonPersonObject() {
        Assert.assertNull(repository.getAncCommonPersonObject(baseEntityId));
    }
}