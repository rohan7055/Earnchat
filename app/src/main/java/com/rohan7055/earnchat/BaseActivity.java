package com.rohan7055.earnchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * Created by meet on 17-07-2016.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        //setProxy();
    }

//    public static void applyFont(final Context context, final View root) {
//        try {
//            if (root instanceof ViewGroup) {
//                ViewGroup viewGroup = (ViewGroup) root;
//                for (int i = 0; i < viewGroup.getChildCount(); i++)
//                    applyFont(context, viewGroup.getChildAt(i));
//            } else if (root instanceof TextView)
//                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font)));
//        } catch (Exception e) {
//            Log.e("ProjectName", String.format("Error occured when trying to apply %s font for %s view",context.getString(R.string.font), root));
//            e.printStackTrace();
//        }
//    }

//    private void setProxy() {
//        //Device proxy settings
//        ProxySelector defaultProxySelector = ProxySelector.getDefault();
//        Proxy proxy = null;
//        List<Proxy> proxyList = defaultProxySelector.select(URI.create("http://www.google.in"));
//        if (proxyList.size() > 0) {
//            proxy = proxyList.get(0);
//
//            Log.d("proxy", String.valueOf(proxy));
//
//            try {
//                String proxyType = String.valueOf(proxy.type());
//
//                //setting HTTP Proxy
//                if (proxyType.equals("HTTP")) {
//                    String proxyAddress = String.valueOf(proxy.address());
//                    String[] proxyDetails = proxyAddress.split(":");
//                    String proxyHost = proxyDetails[0];
//                    String proxyPort = proxyDetails[1];
//                    Log.d("proxy", proxyType + " " + proxyHost + " " + proxyPort);
//
//                    System.setProperty("http.proxyHost", proxyHost);
//                    System.setProperty("http.proxyPort", proxyPort);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}