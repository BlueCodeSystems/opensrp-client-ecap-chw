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

    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.all_households, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption op2 = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.all_benefeciaries, Constants.DrawerMenu.BENEFICIARIES, 0);
            NavigationOption op3 = new NavigationOption(R.mipmap.sidemenu_index,R.mipmap.sidemenu_index_active,R.string.all_indexes, Constants.DrawerMenu.INDEXES,0);
            NavigationOption op4 = new NavigationOption(R.mipmap.sidemenu_case_plan,R.mipmap.sidemenu_case_plan_active,R.string.all_indexes, Constants.DrawerMenu.CASE_PLANS,0);
            //NavigationOption op3 = new NavigationOption(R.mipmap.sidemenu_anc, R.mipmap.sidemenu_anc_active, R.string.menu_anc, Constants.DrawerMenu.ANC, 0);
            //NavigationOption op5 = new NavigationOption(R.mipmap.sidemenu_pnc, R.mipmap.sidemenu_pnc_active, R.string.menu_pnc, Constants.DrawerMenu.PNC, 0);
            //NavigationOption op6 = new NavigationOption(R.mipmap.sidemenu_fp, R.mipmap.sidemenu_fp_active, R.string.menu_family_planing, Constants.DrawerMenu.FAMILY_PLANNING, 0);
           // NavigationOption op7 = new NavigationOption(R.mipmap.sidemenu_malaria, R.mipmap.sidemenu_malaria_active, R.string.menu_malaria, Constants.DrawerMenu.MALARIA, 0);
            //NavigationOption op8 = new NavigationOption(R.mipmap.sidemenu_referrals, R.mipmap.sidemenu_referrals_active, R.string.menu_referrals, Constants.DrawerMenu.REFERRALS, 0);
           //NavigationOption op9 = new NavigationOption(R.mipmap.sidemenu_updates, R.mipmap.sidemenu_updates_active, R.string.updates, CoreConstants.DrawerMenu.UPDATES, 0);

            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH && BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH)
                navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_clients, CoreConstants.DrawerMenu.ALL_CLIENTS, 0));

            navigationOptions.addAll(Arrays.asList(op1,op2 ,op3,op4));

            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH)
               // navigationOptions.add(op8);

            //navigationOptions.add(op9);
        }

        return navigationOptions;
    }
}
