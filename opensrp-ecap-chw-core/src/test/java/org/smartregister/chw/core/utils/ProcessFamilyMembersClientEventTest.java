package org.smartregister.chw.core.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.chw.core.dao.FamilyMemberDao;
import org.smartregister.chw.core.model.CoreFamilyMemberModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.smartregister.chw.core.dao.FamilyMemberDao.familyMembersToUpdateLastName;

@RunWith(PowerMockRunner.class)
public class ProcessFamilyMembersClientEventTest {

    @Mock
    private Context context;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    private FamilyMetadata metadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FamilyLibrary.init(context, null, 1, 1);
        FamilyLibrary.getInstance().setMetadata(getMetadata());
    }

    @PrepareForTest({FamilyMemberDao.class})
    @Test
    public void processFamilyMemberUpdateFamilyNameShouldReturnEventAndClient() {
        PowerMockito.mockStatic(FamilyMemberDao.class);
        PowerMockito.when(familyMembersToUpdateLastName("6543421")).thenReturn(Collections.singletonList(new CoreFamilyMemberModel("demo", "123456", "6543421", "Update Family Member Registration")));
        List<Pair<Client, Event>> familyEventClient = CoreJsonFormUtils.processFamilyMemberUpdateFamilyName("6543421", "demo", formTag(allSharedPreferences), allSharedPreferences);
        assertNotNull(familyEventClient);
        assertEquals(familyEventClient.size(), 1);
        assertNotNull(familyEventClient.get(0).getRight());
        assertNotNull(familyEventClient.get(0).getLeft());
    }

    protected FamilyMetadata getMetadata() {
        BaseProfileActivity activity = Mockito.mock(BaseProfileActivity.class);
        if (metadata == null) {
            metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class, activity.getClass(), "UNIQUE_IDENTIFIER_KEY", true);
            metadata.updateFamilyRegister("FAMILY_REGISTER", "FAMILY", "FAMILY_REGISTRATION", "UPDATE_FAMILY_REGISTRATION", "FAMILY_REGISTER", "FAMILY_HEAD", "PRIMARY_CAREGIVER");
            metadata.updateFamilyMemberRegister("FAMILY_MEMBER_REGISTER", "FAMILY_MEMBER", "FAMILY_MEMBER_REGISTRATION", "UPDATE_FAMILY_MEMBER_REGISTRATION", "FAMILY_MEMBER_REGISTER", "FAMILY");
            metadata.updateFamilyDueRegister("FAMILY_MEMBER", 20, true);
            metadata.updateFamilyActivityRegister("FAMILY_MEMBER", Integer.MAX_VALUE, false);
            metadata.updateFamilyOtherMemberRegister("FAMILY_MEMBER", Integer.MAX_VALUE, false);
        }
        return metadata;
    }

    protected static FormTag formTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }
}


