package com.bluecodeltd.ecap.chw.application;

import com.bluecodeltd.ecap.chw.util.Threading;

import org.smartregister.DristhiConfiguration;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.util.Session;

import timber.log.Timber;

/**
 * UserService whose post-login settings writes survive a logout.
 *
 * After a remote login, UserService saves the ANM location, team and user info with AsyncTasks on the
 * shared serial executor. If the user logs out (or the session times out) before one of them runs,
 * logoutSession() has cleared the database password and the write throws
 * RuntimeException("Password has not been set!") on the AsyncTask thread, crashing the app. Here the
 * writes run on the IO pool and a failed write is logged instead; the next login saves them again.
 */
public class ChwUserService extends UserService {

    public ChwUserService(AllSettings allSettings, AllSharedPreferences allSharedPreferences,
                          HTTPAgent httpAgent, Session session, DristhiConfiguration configuration) {
        super(allSettings, allSharedPreferences, httpAgent, session, configuration);
    }

    @Override
    protected void executeSaveAnmLocationTask(AllSettings allSettings, String anmLocationString) {
        saveSetting("ANM location", () -> allSettings.saveANMLocation(anmLocationString));
    }

    @Override
    protected void executeSaveANMTeamTask(AllSettings allSettings, String anmTeamString) {
        saveSetting("ANM team", () -> allSettings.saveANMTeam(anmTeamString));
    }

    @Override
    protected void executeSaveUserInfoTask(AllSettings allSettings, String userInfoString) {
        saveSetting("user info", () -> allSettings.saveUserInformation(userInfoString));
    }

    private static void saveSetting(String what, Runnable write) {
        Threading.io(() -> {
            try {
                write.run();
            } catch (RuntimeException e) {
                Timber.w(e, "Could not save %s after login (logged out before the write ran?)", what);
            }
        });
    }
}
