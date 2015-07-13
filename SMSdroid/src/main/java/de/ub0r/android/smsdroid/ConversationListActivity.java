/*
 * Copyright (C) 2009-2015 Felix Bechstein
 * 
 * This file is part of SMSdroid.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package de.ub0r.android.smsdroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import de.ub0r.android.lib.Utils;
import de.ub0r.android.lib.apis.Contact;
import de.ub0r.android.lib.apis.ContactsWrapper;
import de.ub0r.android.logg0r.Log;
import ir.arusha.android.sms_plus.cache.PhoneNumberCache;
import ir.arusha.android.sms_plus.filter.FilterManager;

import java.util.Calendar;

/**
 * Main {@link SherlockActivity} showing conversations.
 *
 * @author flx
 */
public final class ConversationListActivity extends SherlockActivity implements
        OnItemClickListener, OnItemLongClickListener {

    /**
     * Tag for output.
     */
    public static final String TAG = "main";
    /**
     * Minimum date.
     */
    public static final long MIN_DATE = 10000000000L;
    /**
     * Miliseconds per seconds.
     */
    public static final long MILLIS = 1000L;
    /**
     * ORIG_URI to resolve.
     */
    static final Uri URI = Uri.parse("content://mms-sms/conversations/");
    /**
     * Number of items.
     */
    private static final int WHICH_N = 7;
    /**
     * Index in dialog: answer.
     */
    private static final int WHICH_ANSWER = 0;
    /**
     * Index in dialog: answer.
     */
    private static final int WHICH_CALL = 1;
    /**
     * Index in dialog: view/add contact.
     */
    private static final int WHICH_VIEW_CONTACT = 2;
    /**
     * Index in dialog: view.
     */
    private static final int WHICH_VIEW = 3;
    /**
     * Index in dialog: delete.
     */
    private static final int WHICH_DELETE = 4;
    /**
     * Index in dialog: mark as spam.
     */
    private static final int WHICH_MARK_SPAM = 5;
    /**
     * Index in dialog: mark as white listed.
     */
    private static final int WHICH_MARK_WHITE_LIST = 6;
    /**
     * {@link Calendar} holding today 00:00.
     */
    private static final Calendar CAL_DAYAGO = Calendar.getInstance();
    /**
     * Show contact's photo.
     */
    public static boolean showContactPhoto = false;
    /**
     * Show emoticons in {@link MessageListActivity}.
     */
    public static boolean showEmoticons = false;

    static {
        // Get time for now - 24 hours
        CAL_DAYAGO.add(Calendar.DAY_OF_MONTH, -1);
    }

    /**
     * Dialog items shown if an item was long clicked.
     */
    private String[] longItemClickDialog = null;
    /**
     * Conversations.
     */
    private ConversationAdapter adapter = null;

    /**
     * Show all rows of a particular {@link Uri}.
     *
     * @param context {@link Context}
     * @param u       {@link Uri}
     */
    @SuppressWarnings("UnusedDeclaration")
    static void showRows(final Context context, final Uri u) {
        Log.d(TAG, "-----GET HEADERS-----");
        Log.d(TAG, "-- ", u.toString(), " --");
        Cursor c = context.getContentResolver().query(u, null, null, null, null);
        if (c != null) {
            int l = c.getColumnCount();
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < l; i++) {
                buf.append(i).append(":");
                buf.append(c.getColumnName(i));
                buf.append(" | ");
            }
            Log.d(TAG, buf.toString());
        }

    }

    /**
     * Show rows for debugging purposes.
     *
     * @param context {@link Context}
     */
    static void showRows(@SuppressWarnings("UnusedParameters") final Context context) {
        // showRows(ContactsWrapper.getInstance().getUriFilter());
        // showRows(context, URI);
        // showRows(context, Uri.parse("content://sms/"));
        // showRows(context, Uri.parse("content://mms/"));
        // showRows(context, Uri.parse("content://mms/part/"));
        // showRows(context, ConversationProvider.CONTENT_URI);
        // showRows(context, Uri.parse("content://mms-sms/threads"));
        // showRows(Uri.parse(MessageList.URI));
    }

    /**
     * Mark all messages with a given {@link Uri} as read.
     *
     * @param context {@link Context}
     * @param uri     {@link Uri}
     * @param read    read status
     */
    static void markRead(final Context context, final Uri uri, final int read) {
        Log.d(TAG, "markRead(", uri, ",", read, ")");
        if (uri == null) {
            return;
        }
        String[] sel = Message.SELECTION_UNREAD;
        if (read == 0) {
            sel = Message.SELECTION_READ;
        }
        final ContentResolver cr = context.getContentResolver();
        final ContentValues cv = new ContentValues();
        cv.put(Message.PROJECTION[Message.INDEX_READ], read);
        try {
            cr.update(uri, cv, Message.SELECTION_READ_UNREAD, sel);
        } catch (IllegalArgumentException | SQLiteException e) {
            Log.e(TAG, "failed update", e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        SmsReceiver.updateNewMessageNotification(context, null);
    }

    /**
     * Delete messages with a given {@link Uri}.
     *
     * @param context  {@link Context}
     * @param uri      {@link Uri}
     * @param title    title of Dialog
     * @param message  message of the Dialog
     * @param activity {@link Activity} to finish when deleting.
     */
    static void deleteMessages(final Context context, final Uri uri, final int title,
                               final int message, final Activity activity) {
        Log.i(TAG, "deleteMessages(..,", uri, " ,..)");
        final Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                try {
                    final int ret = context.getContentResolver().delete(uri, null, null);
                    Log.d(TAG, "deleted: ", ret);
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                    if (ret > 0) {
                        Conversation.flushCache();
                        Message.flushCache();
                        SmsReceiver.updateNewMessageNotification(context, null);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Argument Error", e);
                    Toast.makeText(context, R.string.error_unknown, Toast.LENGTH_LONG).show();
                } catch (SQLiteException e) {
                    Log.e(TAG, "SQL Error", e);
                    Toast.makeText(context, R.string.error_unknown, Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.show();
    }

    /**
     * Add or remove an entry to/from blacklist.
     *
     * @param addr address
     */
    private static void addToOrRemoveFromSpamlist(final String addr) {
        if (!FilterManager.isInBlackList(addr)) {
            FilterManager.addToBlackList(addr);
            Log.d(TAG, "Added ", addr, " to spam list");
        } else {
            FilterManager.removeFromBlackList(addr);
            Log.d(TAG, "Removed ", addr, " from spam list");
        }
    }

    private static void addToOrRemoveFromWhiteList(final String addr) {
        if (!FilterManager.isInWhiteList(addr)) {
            FilterManager.addToWhiteList(addr);
            Log.d(TAG, "Added ", addr, " to white list");
        } else {
            FilterManager.removeFromWhiteList(addr);
            Log.d(TAG, "Removed ", addr, " from white list");
        }
    }

    /**
     * Get a {@link Intent} for sending a new message.
     *
     * @param context     {@link Context}
     * @param address     address
     * @param showChooser show chooser
     * @return {@link Intent}
     */
    static Intent getComposeIntent(final Context context, final String address,
                                   final boolean showChooser) {
        Intent i = null;

        if (!showChooser && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Search for WebSMS
            PackageManager pm = context.getPackageManager();
            i = pm == null ? null : pm.getLaunchIntentForPackage("de.ub0r.android.websms");
        }

        if (i == null) {
            Log.d(TAG, "WebSMS is not installed!");
            i = new Intent(Intent.ACTION_SENDTO);
        }

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (address == null) {
            i.setData(Uri.parse("sms:"));
        } else {
            i.setData(Uri.parse("smsto:" + PreferencesActivity.fixNumber(context, address)));
        }

        return i;
    }

    /**
     * Convert time into formated date.
     *
     * @param context {@link Context}
     * @param time    time
     * @return formated date.
     */
    static String getDate(final Context context, final long time) {
        long t = time;
        if (t < MIN_DATE) {
            t *= MILLIS;
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PreferencesActivity.PREFS_FULL_DATE, false)) {
            return DateFormat.getTimeFormat(context).format(t) + " "
                    + DateFormat.getDateFormat(context).format(t);
        } else if (t < CAL_DAYAGO.getTimeInMillis()) {
            return DateFormat.getDateFormat(context).format(t);
        } else {
            return DateFormat.getTimeFormat(context).format(t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        AsyncHelper.setAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        AsyncHelper.setAdapter(null);
    }

    /**
     * Get {@link AbsListView}.
     *
     * @return {@link AbsListView}
     */
    private AbsListView getListView() {
        return (AbsListView) findViewById(android.R.id.list);
    }

    /**
     * Set {@link ListAdapter} to {@link ListView}.
     *
     * @param la ListAdapter
     */
    private void setListAdapter(final ListAdapter la) {
        AbsListView v = getListView();
        if (v instanceof GridView) {
            //noinspection RedundantCast
            ((GridView) v).setAdapter(la);
        } else if (v instanceof ListView) {
            //noinspection RedundantCast
            ((ListView) v).setAdapter(la);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewIntent(final Intent intent) {
        if (intent != null) {
            Log.d(TAG, "got intent: ", intent.getAction());
            Log.d(TAG, "got uri: ", intent.getData());
            final Bundle b = intent.getExtras();
            if (b != null) {
                Log.d(TAG, "user_query: ", b.get("user_query"));
                Log.d(TAG, "got extra: ", b);
            }
            final String query = intent.getStringExtra("user_query");
            Log.d(TAG, "user query: ", query);
            // TODO: do something with search query
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        final Intent i = getIntent();
        Log.d(TAG, "got intent: ", i.getAction());
        Log.d(TAG, "got uri: ", i.getData());
        Log.d(TAG, "got extra: ", i.getExtras());

        setTheme(PreferencesActivity.getTheme(this));
        Utils.setLocale(this);
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("use_gridlayout", false)) {
            setContentView(R.layout.conversationgrid);
        } else {
            setContentView(R.layout.conversationlist);
        }

        // debug info
        showRows(this);

        final AbsListView list = getListView();
        adapter = new ConversationAdapter(this);
        setListAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
        longItemClickDialog = new String[WHICH_N];
        longItemClickDialog[WHICH_ANSWER] = getString(R.string.reply);
        longItemClickDialog[WHICH_CALL] = getString(R.string.call);
        longItemClickDialog[WHICH_VIEW_CONTACT] = getString(R.string.view_contact_);
        longItemClickDialog[WHICH_VIEW] = getString(R.string.view_thread_);
        longItemClickDialog[WHICH_DELETE] = getString(R.string.delete_thread_);
        longItemClickDialog[WHICH_MARK_SPAM] = getString(R.string.filter_spam_);
        longItemClickDialog[WHICH_MARK_WHITE_LIST] = getString(R.string.add_to_white_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                // check if this is the default sms app.
                // If the device doesn't support Telephony.Sms (i.e. tablet) getDefaultSmsPackage() will
                // be null. Don't show message in this case.
                final String smsPackage = Telephony.Sms.getDefaultSmsPackage(this);
                if (smsPackage != null && !smsPackage.equals(BuildConfig.APPLICATION_ID)) {
                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    b.setTitle(R.string.not_default_app);
                    b.setMessage(R.string.not_default_app_message);
                    b.setNegativeButton(android.R.string.cancel, null);
                    b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            Intent intent =
                                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                    BuildConfig.APPLICATION_ID);
                            startActivity(intent);
                        }
                    });
                    b.show();
                }
            } catch (SecurityException e) {
                // some samsung devices/tablets want permission GET_TASKS o.O
                Log.e(TAG, "failed to query default SMS app", e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CAL_DAYAGO.setTimeInMillis(System.currentTimeMillis());
        CAL_DAYAGO.add(Calendar.DAY_OF_MONTH, -1);

        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        showContactPhoto = p.getBoolean(PreferencesActivity.PREFS_CONTACT_PHOTO, true);
        showEmoticons = p.getBoolean(PreferencesActivity.PREFS_EMOTICONS, false);
        adapter.startMsgListQuery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhoneNumberCache.write();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getSupportMenuInflater().inflate(R.menu.conversationlist, menu);
//        if (true) {
//            menu.removeItem(R.id.item_donate);
//        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hideDeleteAll = p
                .getBoolean(PreferencesActivity.PREFS_HIDE_DELETE_ALL_THREADS, false);
        menu.findItem(R.id.item_delete_all_threads).setVisible(!hideDeleteAll);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_compose:
                final Intent i = getComposeIntent(this, null, false);
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "error launching intent: ", i.getAction(), ", ", i.getData());
                    Toast.makeText(this,
                            "error launching messaging app!\nPlease contact the developer.",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.item_settings: // start settings activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startActivity(new Intent(this, Preferences11Activity.class));
                } else {
                    startActivity(new Intent(this, PreferencesActivity.class));
                }
                return true;
//            case R.id.item_donate:
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, DonationHelper.DONATOR_URI));
//                } catch (ActivityNotFoundException e) {
//                    Log.e(TAG, "error opening play store with donation app", e);
//                }
//                return true;
            case R.id.item_delete_all_threads:
                deleteMessages(this, Uri.parse("content://sms/"), R.string.delete_threads_,
                        R.string.delete_threads_question, null);
                return true;
            case R.id.item_mark_all_read:
                markRead(this, Uri.parse("content://sms/"), 1);
                markRead(this, Uri.parse("content://mms/"), 1);
                return true;
            case R.id.item_filter:
                FilterManager.toggleShowAll();
                restartActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     */
    public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                            final long id) {
        final Conversation c = Conversation.getConversation(this,
                (Cursor) parent.getItemAtPosition(position), false);
        final Uri target = c.getUri();
        final Intent i = new Intent(this, MessageListActivity.class);
        i.setData(target);
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "error launching intent: ", i.getAction(), ", ", i.getData());
            Toast.makeText(this,
                    "error launching messaging app!\n" + "Please contact the developer.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                   final int position, final long id) {
        final Conversation c = Conversation.getConversation(this,
                (Cursor) parent.getItemAtPosition(position), true);
        final Uri target = c.getUri();
        Builder builder = new Builder(this);
        String[] items = longItemClickDialog;
        final Contact contact = c.getContact();
        final String a = contact.getNumber();
        Log.d(TAG, "p: ", a);
        final String n = contact.getName();
        if (TextUtils.isEmpty(n)) {
            builder.setTitle(a);
            items = items.clone();
            items[WHICH_VIEW_CONTACT] = getString(R.string.add_contact_);
        } else {
            builder.setTitle(n);
        }
        if (FilterManager.isInBlackList(a)) {
            items = items.clone();
            items[WHICH_MARK_SPAM] = getString(R.string.dont_filter_spam_);
        }
        if (FilterManager.isInWhiteList(a)) {
            items = items.clone();
            items[WHICH_MARK_WHITE_LIST] = getString(R.string.remove_from_white_list);
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Intent i;
                try {
                    switch (which) {
                        case WHICH_ANSWER:
                            ConversationListActivity.this.startActivity(getComposeIntent(
                                    ConversationListActivity.this, a, false));
                            break;
                        case WHICH_CALL:
                            i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + a));
                            ConversationListActivity.this.startActivity(i);
                            break;
                        case WHICH_VIEW_CONTACT:
                            if (n == null) {
                                i = ContactsWrapper.getInstance().getInsertPickIntent(a);
                                Conversation.flushCache();
                            } else {
                                final Uri uri = c.getContact().getUri();
                                i = new Intent(Intent.ACTION_VIEW, uri);
                            }
                            ConversationListActivity.this.startActivity(i);
                            break;
                        case WHICH_VIEW:
                            i = new Intent(ConversationListActivity.this,
                                    MessageListActivity.class);
                            i.setData(target);
                            ConversationListActivity.this.startActivity(i);
                            break;
                        case WHICH_DELETE:
                            ConversationListActivity
                                    .deleteMessages(ConversationListActivity.this, target,
                                            R.string.delete_thread_,
                                            R.string.delete_thread_question,
                                            null);
                            break;
                        case WHICH_MARK_SPAM:
                            ConversationListActivity.addToOrRemoveFromSpamlist(
                                    c.getContact().getNumber());
                            restartActivity();
                            break;
                        case WHICH_MARK_WHITE_LIST:
                            ConversationListActivity.addToOrRemoveFromWhiteList(
                                    c.getContact().getNumber());
                            restartActivity();
                            break;
                        default:
                            break;
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "unable to launch activity:", e);
                    Toast.makeText(ConversationListActivity.this, R.string.error_unknown,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
        return true;
    }
}
