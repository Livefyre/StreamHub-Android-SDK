package com.livefyre.comments.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.livefyre.comments.R;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getName();

    private AlertDialog alertDialog;
    private ProgressDialog dialog;

    protected void showProgressDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void showProgressDialog() {
        showProgressDialog("Please Wait..");
    }

    protected void dismissProgressDialog() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }
    }

    protected void showAlert(String alertMsg, String actionName, DialogInterface.OnClickListener action) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(alertMsg);
        builder.setCancelable(false);
        String CANCEL = "CANCEL";

        if (actionName.equals("OK")) {
            CANCEL = "OK";
        } else {
            builder.setPositiveButton(actionName, action);
        }

        builder.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    protected void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks network availability and returns
     *
     * @return true is available
     */
    protected boolean isNetworkAvailable() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Custom toast as per requirement
     */
    protected void customToast() {
        LayoutInflater li = getLayoutInflater();
        View layout = li.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }
}