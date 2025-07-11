package org.smartregister.chw.core.sync.intent;

import android.content.Intent;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.repository.TaskRepository;

/**
 * Created by cozej4 on 2020-02-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class SyncClientEventsPerTaskIntentService extends ChwCoreSyncIntentService {

    private static final String TAG = SyncClientEventsPerTaskIntentService.class.getSimpleName();
    private final TaskRepository taskRepository;


    public SyncClientEventsPerTaskIntentService() {
        super(TAG);
        taskRepository = CoreChwApplication.getInstance().getTaskRepository();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchMissingEventsRetry(0, ((ChwTaskRepository)taskRepository).getTasksWithoutClientsAndEvents());
    }
}