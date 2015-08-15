package com.actionbarsherlock;

/**
 * Hack while waiting ABS to fix AAR issue. https://github.com/JakeWharton/ActionBarSherlock/issues/1001
 */
public class BuildConfig {

    // TODO remove this with next ABS release
    public static final boolean DEBUG = ir.arusha.android.sms_plus.BuildConfig.DEBUG;
}
