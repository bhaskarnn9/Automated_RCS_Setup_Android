package com.bhaskarn.automated_rcs_setup;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static String TAG = "DebugTest";
    private UiDevice mUiDevice;
    private static String mAutPackageName = "com.google.android.apps.messaging";
    Long wait_time_medium = 5000L;
    Long wait_time_short = 2500L;
    Long wait_time_very_short = 1000L;

    @Before
    public void setup() {
        mUiDevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void useAppContext() throws UiObjectNotFoundException {
        // Context of the app under test.
        Context appContext = getInstrumentation().getTargetContext();

        assertEquals("com.bhaskarn.automated_rcs_setup", appContext.getPackageName());
        Log.d("DebugTest", "useAppContext: start test");

        Launch_Play();
        check_beta_enrollment();
        Install_MessagesBeta();
        clear_carrier_services_storage();
        Launch_Set_RCS_Flags_Activity();
        click_Set_ACS_URL();
        send_acs_text();
        click_OTP_PATTERN();
        send_otp_text();
        hide_soft_keyboard();
        click_apply();
        click_apply();
        click_apply();

    }

    public void Launch_Play() {

        try {
            Context currentContext = InstrumentationRegistry.getInstrumentation().getContext();
            Intent launchAppOnPlay = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + mAutPackageName));
            launchAppOnPlay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currentContext.startActivity(launchAppOnPlay);

        } catch (ActivityNotFoundException a_n_f_e) {
            Log.d(TAG, "Launch_Play: ActivityNotFoundException " + a_n_f_e.getLocalizedMessage());
        }
    }

    public void check_beta_enrollment() {

        String appName = "Messages (Beta)";
        String appTitle;

        try {
            appTitle = mUiDevice.findObject(new UiSelector()
                    .resourceId("com.android.vending:id/title_title")).getText();

            if (!appTitle.contains(appName)) {
                Log.d("DebugTest", "Install_Messages: Messages registered for beta");
            }
        } catch (UiObjectNotFoundException e) {
            Log.d("DebugTest", "Install_Messages: " + e.getLocalizedMessage());
        }
        SystemClock.sleep(wait_time_medium);

    }

    public void Install_MessagesBeta() {

        UiObject button_action = mUiDevice.findObject(new UiSelector()
                .className("android.widget.Button").text("Install"));
        button_action.waitForExists(wait_time_short);
        if (!button_action.exists()) {
            button_action = mUiDevice.findObject(new UiSelector()
                    .className("android.widget.Button").text("Update"));
        }
        button_action.waitForExists(wait_time_short);
        if (button_action.exists()) {
            try {
                button_action.clickAndWaitForNewWindow();
            } catch (UiObjectNotFoundException e) {
                Log.d("DebugTest", "Install_MessagesBeta: " + e.getLocalizedMessage());
            }
        } else
            Log.d("DebugTest", "Install_MessagesBeta: Install button doesn't exist ");

        waitUntilProgressbarIsGone();


        PackageManager pMgr = InstrumentationRegistry.getInstrumentation()
                .getContext().getPackageManager();
        PackageInfo appInfo = null;

        try {

            appInfo = pMgr.getPackageInfo(mAutPackageName, 0);
            Log.d(TAG, "Install_MessagesBeta: app is installed");

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("DebugTest", "Install_MessagesBeta: " + e.getLocalizedMessage());
        }
    }

    public void waitUntilProgressbarIsGone() {
        UiObject progressBar = mUiDevice.findObject(
                new UiSelector().className("android.widget.ProgressBar").packageName("com.android.vending"));
        Log.d(TAG, "Utils_waitUntilProgressbarIsGone: progressBar exists "
                + progressBar.waitForExists(2*wait_time_medium));
        if (progressBar.exists()) {
            Log.d(TAG, "waitUntilProgressbarIsGone: Maximum wait time of 120 secs here for the progress bar to be gone");
            boolean flag = progressBar.waitUntilGone(20*wait_time_medium);
            Log.d(TAG, "Utils_waitUntilProgressbarIsGone: " + flag);
            SystemClock.sleep(wait_time_very_short);
        }
    }

    public void Launch_Set_RCS_Flags_Activity() {
        String rcs_activity = ".ui.appsettings.rcs.overrides.OverrideFlagsActivity";
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("am start -S -n " + mAutPackageName + "/" + mAutPackageName
                        + rcs_activity);

    }

    public void click_Set_ACS_URL() {

        UiObject set_acs_url = mUiDevice.findObject(new UiSelector().text("Set ACS Url"));
        clickByBounds(set_acs_url);
    }

    public void click_OTP_PATTERN() {

        UiObject set_otp_pattern = mUiDevice.findObject(new UiSelector().text("Set OTP Pattern"));
        clickByBounds(set_otp_pattern);
    }

    public void clickByBounds(UiObject uiObject) {
        try {
            Rect uiObjectBounds = uiObject.getVisibleBounds();
            int X = uiObjectBounds.centerX();
            int Y = uiObjectBounds.centerY();
            mUiDevice.waitForIdle();
            mUiDevice.click(X, Y);
            SystemClock.sleep(wait_time_medium);
        } catch (UiObjectNotFoundException e) {
            Log.d(TAG, "clickByBounds: exception " + e.getMessage());
        }

    }

    public boolean isSoftKeyboardOpen() {
        for (AccessibilityWindowInfo window : InstrumentationRegistry.getInstrumentation()
                .getUiAutomation().getWindows()) {
            if (window.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                return true;
            }
        }
        return false;
    }

    public void hide_soft_keyboard() {
        if (isSoftKeyboardOpen()) {
            mUiDevice.pressBack();
        }
    }

    public void click_apply() {
        UiObject button_apply = mUiDevice.findObject(new UiSelector().text("Apply"));
        button_apply.waitForExists(wait_time_medium);
        clickByBounds(button_apply);
        SystemClock.sleep(wait_time_short);
    }

    public void clear_carrier_services_storage() {

        String package_carrier_services = "com.google.android.ims";
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("pm clear " + package_carrier_services);

    }

    public void send_otp_text() {

        String otp_input = "Your\\sMessenger\\sverification\\scode\\sis\\sG-(\\d{6})";
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("input text " + otp_input);
        click_enter();
    }

    public void send_acs_text() {

        String acs_input = "http://rcs-acs-prod-us.sandbox.google.com/";
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("input text " + acs_input);
        click_enter();
    }

    public void click_enter() {
        mUiDevice.pressKeyCode(KEYCODE_ENTER);
    }

    public void launch_messages_home() {

        String home_activity = ".home.HomeActivity";
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("am start -S -n " + mAutPackageName + "/" + mAutPackageName
                        + home_activity);
    }


}
