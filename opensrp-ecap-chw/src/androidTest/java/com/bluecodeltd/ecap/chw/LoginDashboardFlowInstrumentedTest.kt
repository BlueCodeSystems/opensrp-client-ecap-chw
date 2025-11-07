package com.bluecodeltd.ecap.chw

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.bluecodeltd.ecap.chw.activity.AllClientsRegisterActivity
import com.bluecodeltd.ecap.chw.activity.LoginActivity
import com.bluecodeltd.ecap.chw.R
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI smoke test that covers:
 * - Login (if username/password passed as instrumentation args)
 * - Dashboard visibility check
 * - Open All Clients register and start registration form
 *
 * To run with credentials:
 * adb shell am instrument -w -r \
 *   -e username "yourUser" -e password "yourPass" \
 *   com.bluecodeltd.chw.ecap.test/androidx.test.runner.AndroidJUnitRunner
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginDashboardFlowInstrumentedTest {

    // Grant storage in case form engine or dashboard touches files
    @Rule @JvmField
    val storagePerms: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @Test
    fun login_dashboard_register_startForm() {
        val args = InstrumentationRegistry.getArguments()
        val username = args.getString("username")
        val password = args.getString("password")

        // Launch Login and perform login only if credentials were provided
        ActivityScenario.launch(LoginActivity::class.java)
        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            onView(withId(R.id.login_user_name_edit_text)).perform(click(), replaceText(username), closeSoftKeyboard())
            onView(withId(R.id.login_password_edit_text)).perform(click(), replaceText(password), closeSoftKeyboard())
            onView(withId(R.id.login_login_btn)).perform(click())
        }

        // Wait for dashboard marker view and assert it’s visible
        waitForView(withId(R.id.dash_facility_name), 30000)
        onView(withId(R.id.dash_facility_name)).check(matches(isDisplayed()))

        // Open All Clients register explicitly (avoid depending on nav drawer wiring in tests)
        val ctx: Context = ApplicationProvider.getApplicationContext()
        val regIntent = Intent(ctx, AllClientsRegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ActivityScenario.launch<AllClientsRegisterActivity>(regIntent)

        // Tap the bottom navigation “Add client” item to trigger form start
        onView(allOf(withId(R.id.action_register), isDisplayed())).perform(click())

        // Verify the JSON Wizard form screen is displayed (Next button present)
        onView(withId(com.vijay.jsonwizard.R.id.btn_next)).check(matches(isDisplayed()))
    }

    // Simple polling wait to allow async nav/network to settle
    private fun waitForView(matcher: org.hamcrest.Matcher<android.view.View>, timeoutMs: Long) {
        val start = System.currentTimeMillis()
        var lastError: Throwable? = null
        do {
            try {
                onView(matcher).check(matches(isDisplayed()))
                return
            } catch (t: Throwable) {
                lastError = t
                Thread.sleep(250)
            }
        } while (System.currentTimeMillis() - start < timeoutMs)
        // Surface the last failure for diagnostics
        throw AssertionError("View not found within ${timeoutMs}ms", lastError)
    }
}

