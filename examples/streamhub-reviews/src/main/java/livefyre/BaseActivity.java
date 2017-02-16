package livefyre;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private ProgressDialog dialog;
    protected LivefyreApplication application = AppSingleton.getInstance().getApplication();

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

    protected boolean isNetworkAvailable() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true;
        } else {
            return false;
        }
    }
}