package ir.arusha.android.sms_plus.intent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import ir.arusha.android.sms_plus.R;

/**
 * Created by maJid~ASGARI on 8/13/2015.
 */
public class OtherAppsIntent extends Activity {

    private enum Publisher {
        Bazaar, Cando
    }

    private static final String DEVELOPER_ID = "arusha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Publisher publisher = Publisher.Bazaar;
        try {
            final ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager
                    .GET_META_DATA);
            final String publisherString = (String) ai.metaData.get("publisher");
            if (publisherString.equals("cando")) publisher = Publisher.Cando;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (publisher == Publisher.Cando) {
                intent.setData(Uri.parse("cando://publisher?id=majid.asgari@gmail.com"));
            } else {
                intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=" + DEVELOPER_ID));
                intent.setPackage("com.farsitel.bazaar");
            }
            startActivity(intent);
        } catch (Throwable th) {
            if (publisher == Publisher.Cando) {
                Toast.makeText(this, getString(R.string.install_cando), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://cafebazaar.ir/developer/" + DEVELOPER_ID));
                startActivity(intent);
            }
        }
        this.finish();
    }
}
