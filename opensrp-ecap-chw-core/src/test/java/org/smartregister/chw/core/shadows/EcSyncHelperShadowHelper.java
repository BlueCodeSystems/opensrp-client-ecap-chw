package org.smartregister.chw.core.shadows;

import org.json.JSONObject;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.sync.helper.ECSyncHelper;

@Implements(ECSyncHelper.class)
public class EcSyncHelperShadowHelper {

    private static Client testClient;

    public static void setTestClient(Client client) {
        testClient = client;
    }

    @Implementation
    public JSONObject getClient(String baseEntityId) {
        return new JSONObject();
    }

    @Implementation
    public <T> T convert(JSONObject jo, Class<T> t) {
        return (testClient == null) ? (T) new Client("base-entity-test-1234") : (T) testClient;
    }
}
