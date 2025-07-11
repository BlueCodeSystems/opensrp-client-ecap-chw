package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.core.domain.HomeVisit;

public class HomeVisitTest {
    @Test
    public void testHomeVisitGettersAndSetters() {
        HomeVisit homeVisit = new HomeVisit();
        homeVisit.setName("Danger-Signs");
        Assert.assertEquals(homeVisit.getName(), "Danger-Signs");
    }
}
