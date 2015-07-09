package ir.arusha.android.sms_plus.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import de.ub0r.android.smsdroid.R;
import de.ub0r.android.smsdroid.SpamDB;
import ir.arusha.android.sms_plus.white_list.WhiteListDB;

import java.util.Collections;
import java.util.HashSet;

/**
 * Created by Majid on 7/9/2015.
 */
public class FilterManager {

    static String[] filters;
    static String[][] replaces;

    /**
     * List of blocked numbers.
     */
    static HashSet<String> blackList;

    /**
     * List of white list.
     */
    static HashSet<String> whiteList;
    static boolean showAll = false;
    static Context context;

    public static void initialize(Context c) {
        context = c;
        String[] replaces = c.getResources().getStringArray(R.array.replaces);
        String[] filters = c.getResources().getStringArray(R.array.filters);
        String[] whiteNumbers = c.getResources().getStringArray(R.array.whiteNumbers);

        FilterManager.replaces = new String[replaces.length / 2][2];
        for (int i = 0; i < replaces.length; i += 2) {
            FilterManager.replaces[i / 2] = new String[2];
            FilterManager.replaces[i / 2][0] = replaces[i];
            FilterManager.replaces[i / 2][1] = replaces[i + 1];
        }
        FilterManager.filters = filters;

        SpamDB spamDB = new SpamDB(c);
        spamDB.open();
        blackList = new HashSet<>();
        Collections.addAll(blackList, spamDB.getAllEntries());
        spamDB.close();

        WhiteListDB whitelistDB = new WhiteListDB(c);
        whitelistDB.open();
        whiteList = new HashSet<>();
        Collections.addAll(whiteList, whitelistDB.getAllEntries());
        Collections.addAll(whiteList, whiteNumbers);
        whitelistDB.close();

        SharedPreferences preferences = c.getSharedPreferences("filterManager", Context.MODE_PRIVATE);
        showAll = preferences.getBoolean("showAll", false);
    }

    public static void toggleShowAll() {
        synchronized (context) {
            SharedPreferences preferences = context.getSharedPreferences("filterManager", Context.MODE_PRIVATE);
            showAll = !showAll;
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("showAll", showAll);
            edit.apply();
            final int messageId = showAll ? R.string.click_again_to_filter : R.string.click_again_to_unfilter;
            Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
        }
    }

    private static String replace(String string) {
        if (string == null) return null;
        for (String[] replace : replaces)
            string = string.replaceAll(replace[0], replace[1]);
        return string;
    }

    public static boolean isInBlackList(String number) {
        if (number == null) return false;
        number = replace(number);
        return blackList.contains(number);
    }

    public static boolean isInWhiteList(String number) {
        if (number == null) return false;
        number = replace(number);
        return whiteList.contains(number);
    }

    public static void addToBlackList(String number) {
        if (number == null) return;
        number = replace(number);
        if (isInBlackList(number)) return;
        SpamDB spamDB = new SpamDB(context);
        spamDB.open();
        spamDB.insertNr(number);
        spamDB.close();
        blackList.add(number);
    }

    public static void addToWhiteList(String number) {
        if (number == null) return;
        number = replace(number);
        if (isInWhiteList(number)) return;
        WhiteListDB whiteListDB = new WhiteListDB(context);
        whiteListDB.open();
        whiteListDB.insertNr(number);
        whiteListDB.close();
        whiteList.add(number);
    }

    public static void removeFromBlackList(String number) {
        if (number == null) return;
        number = replace(number);
        if (!isInBlackList(number)) return;
        SpamDB spamDB = new SpamDB(context);
        spamDB.open();
        spamDB.removeNr(number);
        spamDB.close();
        blackList.remove(number);
    }

    public static void removeFromWhiteList(String number) {
        if (number == null) return;
        number = replace(number);
        if (!isInWhiteList(number)) return;
        WhiteListDB whiteListDB = new WhiteListDB(context);
        whiteListDB.open();
        whiteListDB.removeNr(number);
        whiteListDB.close();
        whiteList.remove(number);
    }

    public static boolean isFiltered(String number, boolean skipShowAll) {
        if (showAll && !skipShowAll) return false;
        if (number == null) return false;
        number = replace(number);
        if (isInBlackList(number)) return true;
        if (isInWhiteList(number)) return false;
        for (String filter : filters)
            if (number.matches(filter)) return false;
        return true;
    }
}
