package ir.arusha.android.sms_plus.cache;

import android.content.Context;
import android.util.Log;
import de.ub0r.android.lib.apis.Contact;

import java.io.*;
import java.util.HashMap;

/**
 * Created by maJid~ASGARI on 7/13/2015.
 */
public class PhoneNumberCache {
    private static final String TAG = "cache_file";
    private static final HashMap<Integer, String> numbers = new HashMap<>();
    private static File cacheFile;
    private static Context context;
    private static boolean needWrite = false;

    public static void initialize(final Context context) {
        PhoneNumberCache.context = context;
        final File cacheFolder = context.getCacheDir();
        if (cacheFolder == null) return;
        cacheFile = new File(cacheFolder.getAbsolutePath() + File.separator + "number_cache");
        BufferedReader in = null;
        try {
            String sCurrentLine;
            in = new BufferedReader(new FileReader(cacheFile), 1024);
            while ((sCurrentLine = in.readLine()) != null) {
                final String[] splits = sCurrentLine.split("~");
                numbers.put(Integer.parseInt(splits[0]), splits[1]);
            }
        } catch (Exception e) {
            Log.d(TAG, "error in reading file", e);
            cacheFile = null;
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                Log.d(TAG, "error in reading file", e);
                cacheFile = null;
            }
        }
    }

    public static String getNumber(final Integer contactId) {
        String number = numbers.get(contactId);
        if (number != null) return number;
        final Contact contact = new Contact(contactId);
        contact.update(context, true, false);
        number = contact.getNumber();
        //convert null to empty string to distinguish between null and "number which could not to obtained"
        if (number == null) number = "";
        synchronized (numbers) {
            numbers.put(contactId, number);
        }
        needWrite = true;
        return number;
    }

    public static void write() {
        synchronized (numbers) {
            if (cacheFile == null) return;
            if (!needWrite) return;
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(cacheFile), 1024);
                for (Integer key : numbers.keySet()) {
                    String value = numbers.get(key);
                    out.write(key + "~" + value);
                    out.newLine();
                    out.flush();
                }
            } catch (Exception e) {
                Log.d(TAG, "error in writing file", e);
                cacheFile = null;
            } finally {
                try {
                    if (out != null) out.close();
                } catch (IOException e) {
                    Log.d(TAG, "error in writing file", e);
                    cacheFile = null;
                }
            }
            needWrite = false;
        }
    }
}
