package ir.arusha.android.sms_plus.app;

import android.app.Application;
import ir.arusha.android.sms_plus.filter.FilterManager;

/**
 * Created by Majid on 7/9/2015.
 */
public class ArushaSmsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FilterManager.initialize(this.getApplicationContext());
    }
}
