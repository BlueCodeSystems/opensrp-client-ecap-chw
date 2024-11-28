package org.smartregister.chw.core.activity.impl;

import org.smartregister.chw.core.activity.CoreAncMemberProfileActivity;
import org.smartregister.domain.Task;

import java.util.Set;

import timber.log.Timber;

public class CoreAncMemberProfileActivityImpl extends CoreAncMemberProfileActivity {
    @Override
    public void openUpcomingService() {
        Timber.v("openUpcomingService");
    }

    @Override
    public void openFamilyDueServices() {
        Timber.v("openFamilyDueServices");
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        Timber.v("setClientTasks");
    }
}
