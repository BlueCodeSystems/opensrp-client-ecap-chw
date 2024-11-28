package org.smartregister.chw.core.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.domain.Task;
import org.smartregister.repository.TaskRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CoreAncMemberProfileInteractorTest extends BaseUnitTest {

    private final Visit lastVisit = new Visit();
    private final Set<Task> taskList = new HashSet<>();
    private final MemberObject memberObject = new MemberObject();
    private CoreAncMemberProfileInteractor interactor;
    @Mock
    private BaseAncMemberProfileContract.InteractorCallBack interactorCallBack;
    @Mock
    private AncLibrary ancLibrary;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private CoreChwApplication chwApplication;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ChwTaskRepository chwTaskRepository;
    @Mock
    private AncMemberProfileContract.InteractorCallBack callback;

    private AppExecutors appExecutors = new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        interactor = Mockito.mock(CoreAncMemberProfileInteractor.class, Mockito.CALLS_REAL_METHODS);

        //memberObject = Mockito.mock(MemberObject.class);
        memberObject.setPregnancyRiskLevel("Low");
        memberObject.setBaseEntityId("some-base-entity-id");
        memberObject.setFamilyName("Some Family Name");

        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
    }

    @Test
    public void getPregnancyRiskDetailsTest() {
        interactor = spy(interactor);
        interactor.getPregnancyRiskDetails(memberObject);
        Assert.assertNotNull(memberObject);
        Assert.assertEquals(memberObject.getPregnancyRiskLevel(), "Low");
    }

    @Test
    public void getClientTasksTest() {
        doReturn(taskRepository).when(chwApplication).getTaskRepository();
        String planId = "some plan Id";
        String baseEntityId = "some baseEntityId";
        when(chwTaskRepository.getReferralTasksForClientByStatus(planId, baseEntityId, "Referred")).thenReturn(taskList);
        Task task = new Task();
        task.setIdentifier(Mockito.anyString());
        task.setPlanIdentifier(Mockito.anyString());
        task.setGroupIdentifier(Mockito.anyString());
        interactor.getClientTasks(planId, baseEntityId, callback);
        Mockito.verify(callback).setClientTasks(taskList);
    }

    @Test
    public void getMemberClientTest() {
        interactor.getMemberClient(memberObject.getBaseEntityId());
        Assert.assertEquals(AncDao.getMember(memberObject.getBaseEntityId()), interactor.getMemberClient(memberObject.getBaseEntityId()));
    }

    @Test
    public void testRefreshProfileInfo() {
        interactor = spy(interactor);

        Date lastVisitDate = new Date();

        doReturn(visitRepository).when(ancLibrary).visitRepository();
        when(visitRepository.getLatestVisit(memberObject.getBaseEntityId(), "ANC Home Visit")).thenReturn(lastVisit);
        lastVisit.setDate(lastVisitDate);

        Whitebox.setInternalState(interactor, "appExecutors", appExecutors);
        appExecutors.diskIO().execute(() -> {
            interactor.refreshProfileInfo(memberObject, interactorCallBack);
        });

        Assert.assertEquals(interactor.getLastVisitDate(memberObject), lastVisitDate);
    }
}
