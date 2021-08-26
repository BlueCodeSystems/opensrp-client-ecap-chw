package com.bluecodeltd.ecap.chw.model;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationModelFlv implements NavigationModel.Flavor {

    private static final List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {

            NavigationOption indexesNavigationOption = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.facility_ovc_register, Constants.DrawerMenu.INDEX, 0);
            NavigationOption motherNavigationOption = new NavigationOption(R.mipmap.sidemenu_index, R.mipmap.sidemenu_index_active, R.string.mother_index_register, Constants.DrawerMenu.MOTHER_REGISTER, 0);
            NavigationOption allFamiliesNavigationOption = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.all_households, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption posNavigationOption = new NavigationOption(R.mipmap.sidemenu_case_plan, R.mipmap.sidemenu_case_plan_active, R.string.all_case_plans, Constants.DrawerMenu.CASE_PLANS, 0);
            NavigationOption refNavigationOption = new NavigationOption(R.mipmap.sidemenu_referrals, R.mipmap.sidemenu_referrals_active, R.string.referrals, Constants.DrawerMenu.REFERRALS, 0);

            navigationOptions.addAll(Arrays.asList(indexesNavigationOption, motherNavigationOption, allFamiliesNavigationOption, posNavigationOption, refNavigationOption));
        }

        return navigationOptions;
    }
}
