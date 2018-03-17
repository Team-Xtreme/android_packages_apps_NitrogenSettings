package com.nitrogen.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;
import com.nitrogen.settings.preferences.Utils;

public class NotificationSettings extends SettingsPreferenceFragment
                        implements OnPreferenceChangeListener {

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";
    private static final String DISABLE_IMMERSIVE_MESSAGE = "disable_immersive_message";

    private ListPreference mAnnoyingNotifications;
    private Preference mChargingLeds;
    private SwitchPreference mDisableIM;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.nitrogen_settings_notifications);

        PreferenceScreen prefScreen = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }

        mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        int notificationThreshold = Settings.System.getInt(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, 0);
        mAnnoyingNotifications.setValue(Integer.toString(notificationThreshold));
        int valueIndex = mAnnoyingNotifications.findIndexOfValue(String.valueOf(notificationThreshold));
        if (valueIndex > 0) {
            mAnnoyingNotifications.setSummary(mAnnoyingNotifications.getEntries()[valueIndex]);
        }
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);
        }

        mDisableIM = (SwitchPreference) findPreference(DISABLE_IMMERSIVE_MESSAGE);
            mDisableIM.setOnPreferenceChangeListener(this);
            int DisableIM = Settings.System.getInt(getContentResolver(),
                    DISABLE_IMMERSIVE_MESSAGE, 0);
            mDisableIM.setChecked(DisableIM != 0);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAnnoyingNotifications) {
            String notificationThreshold = (String) newValue;
            int notificationThresholdValue = Integer.parseInt(notificationThreshold);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, notificationThresholdValue);
            int notificationThresholdIndex = mAnnoyingNotifications
                    .findIndexOfValue(notificationThreshold);
            mAnnoyingNotifications
                    .setSummary(mAnnoyingNotifications.getEntries()[notificationThresholdIndex]);
            return true;
        } else if (preference == mDisableIM) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), DISABLE_IMMERSIVE_MESSAGE,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NITROGEN_SETTINGS;
    }
}
