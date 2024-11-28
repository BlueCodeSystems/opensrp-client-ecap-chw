package org.smartregister.chw.core.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.repository.CommunityResponderRepository;

import java.util.ArrayList;
import java.util.List;

@Implements(CommunityResponderRepository.class)
public class CommunityResponderRepositoryShadowHelper {
    private static long respondersCount;

    public static void setRespondersCount(long count) {
        respondersCount = count;
    }

    @Implementation
    public long getRespondersCount() {
        return respondersCount;
    }

    @Implementation
    public List<CommunityResponderModel> readAllResponders() {
        List<CommunityResponderModel> communityResponderModelList = new ArrayList<>();
        communityResponderModelList.add(new CommunityResponderModel("responder 1", "0723456789", "-1 2", null));
        communityResponderModelList.add(new CommunityResponderModel("responder 2", "0723456789", "-1 2", null));
        return communityResponderModelList;
    }

}
