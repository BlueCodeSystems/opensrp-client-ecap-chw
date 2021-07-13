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
            NavigationOption allFamiliesNavigationOption = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.all_households, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption beneficiariesNavigationOption = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.all_benefeciaries, Constants.DrawerMenu.BENEFICIARIES, 0);
            NavigationOption indexesNavigationOption = new NavigationOption(R.mipmap.sidemenu_index, R.mipmap.sidemenu_index_active, R.string.all_indexes, Constants.DrawerMenu.INDEX, 0);
            NavigationOption casePlansNavigationOption = new NavigationOption(R.mipmap.sidemenu_case_plan, R.mipmap.sidemenu_case_plan_active, R.string.all_case_plans, Constants.DrawerMenu.CASE_PLAN, 0);

            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH && BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH)
                navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_clients, CoreConstants.DrawerMenu.ALL_CLIENTS, 0));

            navigationOptions.addAll(Arrays.asList(indexesNavigationOption,allFamiliesNavigationOption, beneficiariesNavigationOption,casePlansNavigationOption));
        }

        return navigationOptions;
    }
}
