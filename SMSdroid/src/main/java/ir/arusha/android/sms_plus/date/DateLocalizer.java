package ir.arusha.android.sms_plus.date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import de.ub0r.android.smsdroid.PreferencesActivity;
import de.ub0r.android.smsdroid.R;

/**
 * Created by maJid~ASGARI on 8/15/2015.
 */
public class DateLocalizer {
    private static char[] digits = new char[10];
    private static String am;
    private static String pm;
    private static boolean shamsi;
    private static boolean fullDate;

    public static void init(Context context) {
        int[] codes = new int[]{R.string.digit_0, R.string.digit_1, R.string.digit_2, R.string.digit_3,
                R.string.digit_4, R.string.digit_5, R.string.digit_6, R.string.digit_7, R.string.digit_8,
                R.string.digit_9};
        final Resources resources = context.getResources();
        for (int i = 0; i <= 9; i++) digits[i] = context.getResources().getText(codes[i]).charAt(0);
        am = resources.getText(R.string.clock_am).toString();
        pm = resources.getText(R.string.clock_pm).toString();
//        shamsi = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
//                PreferencesActivity.PREFS_SHAMSI, true);
//        fullDate = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
//                PreferencesActivity.PREFS_FULL_DATE, false);
//        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences
//                .OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                                  String key) {
//                if (key.equals(PreferencesActivity.PREFS_SHAMSI))
//                    shamsi = sharedPreferences.getBoolean(PreferencesActivity.PREFS_SHAMSI, true);
//                if (key.equals(PreferencesActivity.PREFS_FULL_DATE))
//                    fullDate = sharedPreferences.getBoolean(PreferencesActivity.PREFS_FULL_DATE, true);
//            }
//        };
//        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static String localize(final Context context, long date, long dayAgo) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        shamsi = pref.getBoolean(PreferencesActivity.PREFS_SHAMSI, true);
        fullDate = pref.getBoolean(PreferencesActivity.PREFS_FULL_DATE, false);
        final boolean includeDate;
        final boolean includeTime;
        if (fullDate) {
            includeDate = includeTime = true;
        } else if (date < dayAgo) {
            includeDate = true;
            includeTime = false;
        } else {
            includeDate = false;
            includeTime = true;
        }
        if (shamsi) {
            KhorshidiCalendar cal = new KhorshidiCalendar();
            cal.setEpoch(date);
            StringBuilder builder = new StringBuilder(13);
            if (includeDate) builder.append(cal.getYear() % 100).append('/')
                    .append(cal.getMonth()).append('/')
                    .append(cal.getDay());
            if (includeDate && includeTime) builder.append(' ');
            if (includeTime) builder.append(cal.getHour() < 9 ? '0' : "").append(cal.getHour()).append(':')
                    .append(cal.getMin() < 9 ? '0' : "").append(cal.getMin());
//                .append(':').append(cal.getSec() < 9 ? '0' : "").append(cal.getSec());
            return localize(builder);
        } else {
            if (includeDate && includeTime)
                return DateFormat.getTimeFormat(context).format(date) + " "
                        + DateFormat.getDateFormat(context).format(date);
            else if (includeDate)
                return DateFormat.getDateFormat(context).format(date);
            else return DateFormat.getTimeFormat(context).format(date);
        }
    }

    private static String localize(StringBuilder date, boolean includeAmPm) {
        final int length = date.length();
        for (int i = 0; i < length; i++) {
            final char ch = date.charAt(i);
            if (ch >= '0' && ch <= '9') date.setCharAt(i, digits[ch - '0']);
            else if (includeAmPm && (ch == 'A' || ch == 'P' || i < length - 1))
                if (date.charAt(i + 1) == 'M') {
                    date.replace(i, i + 2, ch == 'A' ? am : pm);//am,pm includes 3 characters in all languages
                    i++;
                }
        }
        return date.toString();
    }

    private static String localize(StringBuilder date) {
        final int length = date.length();
        for (int i = 0; i < length; i++) {
            final char ch = date.charAt(i);
            if (ch >= '0' && ch <= '9') date.setCharAt(i, digits[ch - '0']);
        }
        return date.toString();
    }
}
