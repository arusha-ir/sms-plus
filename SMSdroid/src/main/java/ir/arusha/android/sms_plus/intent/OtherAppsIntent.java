package ir.arusha.android.sms_plus.intent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by maJid~ASGARI on 8/13/2015.
 */
public class OtherAppsIntent extends Activity {


    private static final String DEVELOPER_ID = "arusha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=" + DEVELOPER_ID));
            intent.setPackage("com.farsitel.bazaar");
            startActivity(intent);
        } catch (Throwable th) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://cafebazaar.ir/developer/" + DEVELOPER_ID));
            startActivity(intent);
        }
        this.finish();
    }
}
