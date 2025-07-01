package org.smartregister.chw.core.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class PermissionEventTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAndSetPermissionType() {
        PermissionEvent permissionEvent = new PermissionEvent();
        permissionEvent.setPermissionType(1);
        Assert.assertEquals(permissionEvent.getPermissionType(), 1);
    }

    @Test
    public void testSetGranted() {
        PermissionEvent permissionEvent = new PermissionEvent();
        permissionEvent.setGranted(true);
        Assert.assertEquals(permissionEvent.isGranted(), true);
    }

    @Test
    public void testisGranted() {
        PermissionEvent permissionEvent = new PermissionEvent(1, true);
        Assert.assertEquals(permissionEvent.isGranted(), true);
    }
}
