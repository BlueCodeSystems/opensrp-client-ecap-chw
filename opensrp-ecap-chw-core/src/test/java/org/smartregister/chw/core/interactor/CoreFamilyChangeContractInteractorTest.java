package org.smartregister.chw.core.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.shadows.FamilyLibraryShadowUtil;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.AppExecutors;

import java.util.concurrent.Executor;

public class CoreFamilyChangeContractInteractorTest extends BaseUnitTest implements Executor {

    private CoreFamilyChangeContractInteractor interactor;

    @Mock
    private CoreChwApplication coreApplication;

    @Mock
    private FamilyChangeContract.Presenter presenter;

    @Mock
    private FamilyLibrary familyLibrary;

    @Mock
    private Context context;

    @Mock
    private FamilyMetadata metadata;

    @Mock
    private CoreFamilyChangeContractInteractor.Flavor flavor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AppExecutors appExecutors = new AppExecutors(this, this, this);
        interactor = Mockito.spy(new CoreFamilyChangeContractInteractor(appExecutors) {
            @Override
            protected void setCoreChwApplication() {
                this.coreChwApplication = coreApplication;
            }

            @Override
            protected void setFlavour() {
            }
        });
    }


    @Test
    public void testGetAdultMembersExcludeHOF() {

        ReflectionHelpers.setField(interactor, "flavor", flavor);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        CommonPersonObject personObject = Mockito.mock(CommonPersonObject.class);
        Mockito.doReturn(personObject).when(commonRepository).findByBaseEntityId(Mockito.anyString());

        Mockito.doReturn(context).when(familyLibrary).context();
        Mockito.doReturn(commonRepository).when(context).commonrepository(Mockito.any());

        metadata.familyRegister = Mockito.mock(FamilyMetadata.FamilyRegister.class);
        metadata.familyMemberRegister = Mockito.mock(FamilyMetadata.FamilyMemberRegister.class);
        Mockito.doReturn(metadata).when(familyLibrary).metadata();

        FamilyLibraryShadowUtil.setInstance(familyLibrary);

        interactor.getAdultMembersExcludeHOF("123445", presenter);
        Mockito.verify(presenter).renderAdultMembersExcludeHOF(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testGetAdultMembersExcludePCG() {

        ReflectionHelpers.setField(interactor, "flavor", flavor);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        CommonPersonObject personObject = Mockito.mock(CommonPersonObject.class);
        Mockito.doReturn(personObject).when(commonRepository).findByBaseEntityId(Mockito.anyString());

        Mockito.doReturn(context).when(familyLibrary).context();
        Mockito.doReturn(commonRepository).when(context).commonrepository(Mockito.any());

        metadata.familyRegister = Mockito.mock(FamilyMetadata.FamilyRegister.class);
        metadata.familyMemberRegister = Mockito.mock(FamilyMetadata.FamilyMemberRegister.class);
        Mockito.doReturn(metadata).when(familyLibrary).metadata();

        FamilyLibraryShadowUtil.setInstance(familyLibrary);

        interactor.getAdultMembersExcludePCG("123445", presenter);
        Mockito.verify(presenter).renderAdultMembersExcludePCG(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
