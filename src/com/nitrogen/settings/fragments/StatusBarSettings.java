package com.nitrogen.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String DATA_ACTIVITY_ARROWS = "data_activity_arrows";
    private static final String WIFI_ACTIVITY_ARROWS = "wifi_activity_arrows";

    private SwitchPreference mDataActivityEnabled;
    private SwitchPreference mWifiActivityEnabled;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.nitrogen_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mDataActivityEnabled = (SwitchPreference) findPreference(DATA_ACTIVITY_ARROWS);
        boolean mActivityEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.DATA_ACTIVITY_ARROWS,
                showActivityDefault(getActivity()), UserHandle.USER_CURRENT) != 0;
        mDataActivityEnabled.setChecked(mActivityEnabled);
        mDataActivityEnabled.setOnPreferenceChangeListener(this);

        mWifiActivityEnabled = (SwitchPreference) findPreference(WIFI_ACTIVITY_ARROWS);
        mActivityEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.WIFI_ACTIVITY_ARROWS,
                showActivityDefault(getActivity()), UserHandle.USER_CURRENT) != 0;
        mWifiActivityEnabled.setChecked(mActivityEnabled);
        mWifiActivityEnabled.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mDataActivityEnabled) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putIntForUser(resolver, Settings.System.DATA_ACTIVITY_ARROWS,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mDataActivityEnabled.setChecked(showing);
            return true;
        } else if (preference == mWifiActivityEnabled) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putIntForUser(resolver, Settings.System.WIFI_ACTIVITY_ARROWS,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mWifiActivityEnabled.setChecked(showing);
            return true;
        }
        return false;
    }

    public static int showActivityDefault(Context context) {
/*
        final boolean showByDefault = context.getResources().getBoolean(
                com.android.internal.R.bool.config_showActivity);

        if (showByDefault) {
            return 1;
        }
*/
        return 0;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        Settings.System.putIntForUser(resolver,
                Settings.System.DATA_ACTIVITY_ARROWS, showActivityDefault(mContext), UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.WIFI_ACTIVITY_ARROWS, showActivityDefault(mContext), UserHandle.USER_CURRENT);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NITROGEN_SETTINGS;
    }

}
