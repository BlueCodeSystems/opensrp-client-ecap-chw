package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.core.domain.Child;

public class ChildTest {

    @Test
    public void testChildGettersAndSetters() {
        Child child = new Child();
        child.setFirstName("Tony");
        Assert.assertEquals(child.getFirstName(), "Tony");
    }
}
