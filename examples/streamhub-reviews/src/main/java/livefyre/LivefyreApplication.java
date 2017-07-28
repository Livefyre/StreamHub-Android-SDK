package livefyre;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;
import com.squareup.otto.Bus;

import livefyre.activities.SplashActivity;

public class LivefyreApplication extends MultiDexApplication {
    private static final int TIMEOUT_VALUE = 10000;
    private static final String LIVEFYRE = "livefyre";
    Bus mBus;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        LivefyreConfig.setLivefyreNetworkID(LFSConfig.NETWORK_ID);
        AppSingleton.getInstance().setApplication(this);
        MultiDex.install(this);
        init();
    }


    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences(
                LIVEFYRE, MODE_PRIVATE);
        mBus = new Bus();
    }

    public void saveDataInSharedPreferences(String key, String sessionId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, sessionId);
        editor.commit();
    }

    public String getDataFromSharedPreferences(String reqString) {
        return sharedPreferences.getString(reqString, "");
    }


    public boolean isDeviceConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null);
    }

    public void printLog(boolean print, String tag, String value) {
        if (print)
            Log.d(tag, value);
    }

    public int getRequestTimeOut() {
        try {
            return TIMEOUT_VALUE;
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public String getErrorStringFromResourceCode(int resourceCode) {
        return getResources().getText(resourceCode).toString();
    }
    private void ShortcutIcon() {

        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Livefyre Comments");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.livefyreappicon));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
    public Bus getBus(){
        return mBus;
    }
}
