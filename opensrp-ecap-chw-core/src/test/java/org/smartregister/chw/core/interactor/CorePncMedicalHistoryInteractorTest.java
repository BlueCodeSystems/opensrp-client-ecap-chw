package org.smartregister.chw.core.interactor;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.pnc.contract.BasePncMedicalHistoryContract;
import org.smartregister.dao.AbstractDao;
import org.smartregister.repository.Repository;

import java.util.concurrent.Executor;

public class CorePncMedicalHistoryInteractorTest extends BaseUnitTest implements Executor {

    private CorePncMedicalHistoryActivityInteractor interactor;
    private AppExecutors appExecutors;

    @Mock
    private Context context;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private BasePncMedicalHistoryContract.InteractorCallBack callBack;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appExecutors = new AppExecutors(this, this, this);
        interactor = Mockito.spy(CorePncMedicalHistoryActivityInteractor.class);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void getMemberHistoryCallBackIsExecutedWithVisitDetails() {
        ReflectionHelpers.setField(interactor, "appExecutors", appExecutors);
        ReflectionHelpers.setStaticField(AbstractDao.class, "repository", repository);
        interactor.getMemberHistory("ID1234", context, callBack);
        Mockito.verify(callBack).onDataFetched(Mockito.anyList());
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}