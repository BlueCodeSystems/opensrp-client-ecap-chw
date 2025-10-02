package org.smartregister.chw.anc.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.VisitUtils;

import timber.log.Timber;

public class HomeVisitIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private VisitRepository visitRepository;
    private VisitDetailsRepository visitDetailsRepository;

    public HomeVisitIntentService() {
        super("HomeVisitService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        visitRepository = AncLibrary.getInstance().visitRepository();
        visitDetailsRepository = AncLibrary.getInstance().visitDetailsRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            processVisits();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Process all the visit older than 24 hours
     *
     * @throws Exception
     */
    protected void processVisits() throws Exception {
        VisitUtils.processVisits(visitRepository, visitDetailsRepository, null);
    }
}
