package com.kelsos.mbrc.ui.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class SettingsActivityGB extends PreferenceActivity {
    @Inject private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.main_menu_title_settings);
        addPreferencesFromResource(R.xml.application_settings);
        final Context mContext = this;

        final Preference mOpenSource = findPreference(getResources().getString(R.string.preferences_open_source));
        final Preference mManager = findPreference(getResources().getString(R.string.preferences_key_connection_manager));
        final Preference mVersion = findPreference(getResources().getString(R.string.settings_version));
        final Preference mBuild = findPreference(getResources().getString(R.string.pref_key_build_time));
        final Preference mRevision = findPreference(getResources().getString(R.string.pref_key_revision));
        if (mOpenSource != null) {
            mOpenSource.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    showOpenSourceLicenseDialog();
                    return false;
                }
            });
        }

        if (mManager != null) {
            mManager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(mContext, ConnectionManagerActivity.class));
                    return false;
                }
            });
        }

        if (mVersion != null) {
            mVersion.setSummary(String.format(getResources().getString(R.string.settings_version_number), BuildConfig.VERSION_NAME));
        }

        if (mBuild != null) {
            mBuild.setSummary(BuildConfig.BUILD_TIME);
        }

        if (mRevision != null) {
            mRevision.setSummary(BuildConfig.GIT_SHA);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final Preference mShowNotification = findPreference(getResources().
                    getString(R.string.settings_key_notification_control));
            if (mShowNotification != null) {
                mShowNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean value = (Boolean) newValue;
                        if (!value) {
                            bus.post(new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION));
                        }
                        return true;
                    }
                });
            }
        }

        final Preference mLicense = findPreference(getResources().getString(R.string.settings_key_license));
        if (mLicense != null) {
            mLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    showLicenseDialog();
                    return false;
                }
            });
        }
    }

    private void showLicenseDialog() {
        final WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/license.html");
        new AlertDialog.Builder(this)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("MusicBee Remote license")
                .create()
                .show();
    }

    private void showOpenSourceLicenseDialog() {
        final WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/licenses.html");
        new AlertDialog.Builder(this)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle("Open source licenses")
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
