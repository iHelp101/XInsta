package com.ihelp101.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ErrorActivity extends Activity {

    String getDirectory = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alert();
    }

    void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ErrorActivity.this);

        alertDialog.setTitle("XInsta Error");

        alertDialog.setMessage("Do you want to report this issue to the developer?");

        alertDialog.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                        onBackPressed();
                    }
                });

        alertDialog.setNegativeButton("Report",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendErrorLog();
                        dialog.cancel();
                    }
                });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
                onBackPressed();
            }
        });

        alertDialog.show();
    }

    void sendErrorLog() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Error Log");
        intent.putExtra(Intent.EXTRA_TEXT, "Please check out my XInsta Error Log.");
        intent.setType("text/plain");

        File root = new File(getDirectory, ".Instagram");
        File file = new File(root, "Error.txt");
        Uri uri = Uri.fromFile(file);
        if (!file.exists() || !file.canRead()) {
            setToast("Error Log Missing!");
            return;
        }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Email"));
    }

    void setToast(String message) {
        Toast toast = Toast.makeText(ErrorActivity.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
