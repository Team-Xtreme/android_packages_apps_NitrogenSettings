package com.nitrogen.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.development.DevelopmentSettings;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.nitrogen.settings.R;

public class StatusbarLogo extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "StatusbarLogo";

    private static final String CRDROID_LOGO = "status_bar_crdroid_logo";
    private static final String CRDROID_LOGO_COLOR = "status_bar_crdroid_logo_color";
    private static final String CRDROID_LOGO_POSITION = "status_bar_crdroid_logo_position";
    private static final String CRDROID_LOGO_STYLE = "status_bar_crdroid_logo_style";

    private SwitchPreference mCrDroidLogo;
    private ColorPickerPreference mCrDroidLogoColor;
    private ListPreference mCrDroidLogoPosition;
    private ListPreference mCrDroidLogoStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.statusbar_logo);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCrDroidLogo = (SwitchPreference) findPreference(CRDROID_LOGO);
        mCrDroidLogo.setOnPreferenceChangeListener(this);

        mCrDroidLogoPosition = (ListPreference) findPreference(CRDROID_LOGO_POSITION);
        int crdroidLogoPosition = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_POSITION, 0,
                UserHandle.USER_CURRENT);
        mCrDroidLogoPosition.setValue(String.valueOf(crdroidLogoPosition));
        mCrDroidLogoPosition.setSummary(mCrDroidLogoPosition.getEntry());
        mCrDroidLogoPosition.setOnPreferenceChangeListener(this);

       mCrDroidLogoColor =
                (ColorPickerPreference) findPreference(CRDROID_LOGO_COLOR);
        int intColor = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_COLOR, 0xFFFFFFFF,
                UserHandle.USER_CURRENT);
        String hexColor = ColorPickerPreference.convertToARGB(intColor);
        mCrDroidLogoColor.setNewPreviewColor(intColor);
        if (intColor != 0xFFFFFFFF) {
            mCrDroidLogoColor.setSummary(hexColor);
        } else {
            mCrDroidLogoColor.setSummary(R.string.default_string);
        }
        mCrDroidLogoColor.setOnPreferenceChangeListener(this);

        mCrDroidLogoStyle = (ListPreference) findPreference(CRDROID_LOGO_STYLE);
        int crdroidLogoStyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mCrDroidLogoStyle.setValue(String.valueOf(crdroidLogoStyle));
        mCrDroidLogoStyle.setSummary(mCrDroidLogoStyle.getEntry());
        mCrDroidLogoStyle.setOnPreferenceChangeListener(this);

        boolean mLogoEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO,
                0, UserHandle.USER_CURRENT) != 0;
        toggleLogo(mLogoEnabled);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCrDroidLogo) {
            boolean value = (Boolean) newValue;
            toggleLogo(value);
            return true;
        } else if (preference == mCrDroidLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.parseInt(String.valueOf(newValue)));
            int value = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_COLOR, value,
                UserHandle.USER_CURRENT);
            if (value != 0xFFFFFFFF) {
                mCrDroidLogoColor.setSummary(hex);
            } else {
                mCrDroidLogoColor.setSummary(R.string.default_string);
            }
            return true;
        } else if (preference == mCrDroidLogoPosition) {
            int value = Integer.parseInt((String) newValue);
            int index = mCrDroidLogoPosition.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                resolver, Settings.System.STATUS_BAR_CRDROID_LOGO_POSITION, value,
                UserHandle.USER_CURRENT);
            mCrDroidLogoPosition.setSummary(
                    mCrDroidLogoPosition.getEntries()[index]);
            return true;
        } else if (preference == mCrDroidLogoStyle) {
            int value = Integer.parseInt((String) newValue);
            int index = mCrDroidLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                resolver, Settings.System.STATUS_BAR_CRDROID_LOGO_STYLE, value,
                UserHandle.USER_CURRENT);
            mCrDroidLogoStyle.setSummary(
                    mCrDroidLogoStyle.getEntries()[index]);
            return true;
         }
         return false;
     }

    public void toggleLogo(boolean enabled) {
        mCrDroidLogoColor.setEnabled(enabled);
        mCrDroidLogoPosition.setEnabled(enabled);
        mCrDroidLogoStyle.setEnabled(enabled);
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_COLOR, 0xFFFFFFFF, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_POSITION, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.STATUS_BAR_CRDROID_LOGO_STYLE, 0, UserHandle.USER_CURRENT);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NITROGEN_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
