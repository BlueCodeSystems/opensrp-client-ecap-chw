package org.smartregister.chw.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NavigationOptionTest {
    private int resourceID;
    private int resourceActiveID;
    private int titleID;
    private String menuTitle;
    private long registerCount;

    @Before
    public void setup() {
        resourceID = 2345678;
        resourceActiveID = 4567890;
        titleID = 234567;
        menuTitle = "jambo";
        registerCount = 987654L;    }
    @Test
    public void getResourceIDTest(){
        NavigationOption navigationOption = new NavigationOption(resourceID, resourceActiveID, titleID, menuTitle, registerCount);
        Assert.assertEquals(navigationOption.getResourceID(), resourceID);
    }
    @Test
    public void getResourceActiveIDTest(){
        NavigationOption navigationOption = new NavigationOption(resourceID, resourceActiveID, titleID, menuTitle, registerCount);
        Assert.assertEquals(navigationOption.getResourceActiveID(), resourceActiveID);
    }
    @Test
    public void getTitleIDTest(){
        NavigationOption navigationOption = new NavigationOption(resourceID, resourceActiveID, titleID, menuTitle, registerCount);
        Assert.assertEquals(navigationOption.getTitleID(), titleID);
    }
    @Test
    public void getMenuTitleTest(){
        NavigationOption navigationOption = new NavigationOption(resourceID, resourceActiveID, titleID, menuTitle, registerCount);
        Assert.assertEquals(navigationOption.getMenuTitle(), menuTitle);
    }
    @Test
    public void getRegisterCountTest(){
        NavigationOption navigationOption = new NavigationOption(resourceID, resourceActiveID, titleID, menuTitle, registerCount);
        Assert.assertEquals(navigationOption.getRegisterCount(), registerCount);
    }
}
