package ir.arusha.android.sms_plus.filter;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import ir.arusha.android.sms_plus.cache.PhoneNumberCache;

/**
 * Created by Majid on 7/9/2015.
 */
public class FilterCursorWrapper extends CursorWrapper {
    Context context;
    private int[] index;
    private int count = 0;
    private int pos = 0;

    public FilterCursorWrapper(Context context, Cursor cursor, boolean doFilter, int column) {
        super(cursor);
        this.context = context;
        if (doFilter) {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                super.moveToPosition(i);
                if (!isHidden(this.getString(column)))
                    this.index[this.pos++] = i;
            }
            this.count = this.pos;
            this.pos = 0;
            super.moveToFirst();
        } else {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                this.index[i] = i;
            }
        }
    }

    public boolean isHidden(String contactId) {
        final String number = PhoneNumberCache.getNumber(contactId);
        return FilterManager.isFiltered(number, false);
    }

    @Override
    public boolean move(int offset) {
        return this.moveToPosition(this.pos + offset);
    }

    @Override
    public boolean moveToNext() {
        return this.moveToPosition(this.pos + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return this.moveToPosition(this.pos - 1);
    }

    @Override
    public boolean moveToFirst() {
        return this.moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return this.moveToPosition(this.count - 1);
    }

    @Override
    public boolean moveToPosition(int position) {
        return !(position >= this.count || position < 0) && super.moveToPosition(this.index[position]);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }
}