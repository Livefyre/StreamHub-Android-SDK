package com.livefyre.comments;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.livefyre.comments.activities.SplashActivity;
import com.livefyre.comments.manager.SharedPreferenceManager;
import com.livefyre.comments.util.Constant;
import com.livefyre.streamhub_android_sdk.util.LivefyreConfig;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceManager.getInstance().init(this);
        //set network id
        LivefyreConfig.setLivefyreNetworkID(Config.NETWORK_ID);

        //create shortcut icon on install and first launch the app only
        boolean isFirstTime = Boolean.parseBoolean(SharedPreferenceManager.getInstance().getString(Constant.IS_FIRST_TIME_STR, Constant.IS_FIRST_TIME));
        if (isFirstTime) {
//            shortcutIcon();
            SharedPreferenceManager.getInstance().putString(Constant.IS_FIRST_TIME_STR, Constant.IS_NOT_FIRST_TIME);
        }
    }

    /**
     * Flag controllable log
     *
     * @param print - print or not
     * @param tag   - tag to print
     * @param value - value to print
     */
    public void printLog(boolean print, String tag, String value) {
        if (print)
            Log.d(tag, value);
    }

    /**
     * Creates Shortcut icon on home
     */
    private void shortcutIcon() {
        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Livefyre Comments");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.splash));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
}
